package com.ubuntucli

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ubuntucli.core.SystemInitializer
import com.ubuntucli.filemanager.FileManager
import com.ubuntucli.monitor.ProcessInfo
import com.ubuntucli.monitor.SystemMonitor
import com.ubuntucli.apkg.PackageManager
import com.ubuntucli.settings.SettingsManager
import com.ubuntucli.terminal.*
import com.ubuntucli.ui.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initializer = SystemInitializer(this)
        val sm = SettingsManager(this)

        setContent {
            val vm: TerminalViewModel = viewModel()
            var currentScreen by remember { mutableStateOf("terminal") }
            var isInitialized by remember { mutableStateOf(initializer.isInitialized()) }
            var initStatus by remember { mutableStateOf("Checking DNA layer...") }
            var hasError by remember { mutableStateOf(false) }
            var isAuthenticated by remember { mutableStateOf(!sm.biometricEnabled.value) }
            val scope = rememberCoroutineScope()

            Theme(theme = sm.theme.value) {
                if (!isInitialized) {
                    InitializationScreen(initStatus, hasError, onRetry = {
                        hasError = false
                        scope.launch {
                            try {
                                initializer.initialize { initStatus = it }
                                isInitialized = true
                            } catch (e: Exception) {
                                hasError = true
                                initStatus = "Error: ${e.message}"
                            }
                        }
                    })

                    LaunchedEffect(Unit) {
                        try {
                            initializer.initialize { initStatus = it }
                            isInitialized = true
                        } catch (e: Exception) {
                            hasError = true
                            initStatus = "Error: ${e.message}"
                        }
                    }
                } else if (!isAuthenticated) {
                    AuthScreen { showBiometricPrompt { isAuthenticated = true } }
                } else {
                    MainScaffold(currentScreen, { currentScreen = it }, vm, this@MainActivity)
                }
            }
        }
    }

    private fun showBiometricPrompt(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("UbuntuCLI Droid Secure Access")
            .setSubtitle("Authenticate to access your Linux environment")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

@Composable
fun InitializationScreen(status: String, hasError: Boolean, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!hasError) CircularProgressIndicator(color = Color.Green)
        else Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(48.dp))

        Spacer(Modifier.height(16.dp))
        Text("UbuntuCLI Droid", style = MaterialTheme.typography.headlineMedium, color = Color.Green)
        Spacer(Modifier.height(8.dp))
        Text(text = status, color = if (hasError) Color.Red else Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp)

        if (hasError) {
            Spacer(Modifier.height(24.dp))
            Button(onClick = onRetry) { Text("Retry Initialization") }
        }
    }
}

@Composable
fun AuthScreen(onAuthRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Lock, null, tint = Color.Green, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(24.dp))
        Button(onClick = onAuthRequest) { Text("Unlock Environment") }

        LaunchedEffect(Unit) { onAuthRequest() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(currentScreen: String, onScreenChange: (String) -> Unit, vm: TerminalViewModel, context: Context) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Triple("terminal", Icons.Default.Terminal, "Term"),
                    Triple("packages", Icons.Default.Inventory, "Pkgs"),
                    Triple("files", Icons.Default.Folder, "Files"),
                    Triple("monitor", Icons.Default.MonitorHeart, "Mon"),
                    Triple("settings", Icons.Default.Settings, "Set")
                ).forEach { (screen, icon, label) ->
                    NavigationBarItem(
                        icon = { Icon(icon, label) },
                        label = { Text(label) },
                        selected = currentScreen == screen,
                        onClick = { onScreenChange(screen) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "terminal" -> TerminalView(vm)
                "packages" -> PackagesScreen(vm)
                "files" -> FilesScreen()
                "monitor" -> MonitorScreen()
                "settings" -> SettingsScreen(context)
            }
        }
    }
}

@Composable
fun PackagesScreen(vm: TerminalViewModel) {
    val pm = remember { PackageManager() }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Package Manager", style = MaterialTheme.typography.headlineMedium) }
        items(pm.getPopularPackages()) { pkg ->
            ListItem(
                headlineContent = { Text(pkg) },
                trailingContent = {
                    Button(onClick = { vm.sendCommand(0, pm.getAptInstallCmd(pkg)) }) {
                        Text("Install")
                    }
                }
            )
        }
    }
}

@Composable
fun FilesScreen() {
    val fm = remember { FileManager() }
    var path by remember { mutableStateOf("/") }
    val files = remember(path) { fm.listFiles(path) }
    var showDialog by remember { mutableStateOf(false) }
    var newDirName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Folder") },
            text = { TextField(value = newDirName, onValueChange = { newDirName = it }) },
            confirmButton = {
                Button(onClick = {
                    fm.createDirectory(File(path, newDirName).absolutePath)
                    showDialog = false
                }) { Text("Create") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (path != "/") path = File(path).parent ?: "/" }) {
                Icon(Icons.Default.ArrowBack, null)
            }
            Text("Path: $path", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.CreateNewFolder, null)
            }
        }
        Divider()
        LazyColumn {
            items(files) { file ->
                ListItem(
                    headlineContent = { Text(file.name) },
                    supportingContent = { Text(if (file.isDirectory) "Directory" else "${file.length()} bytes") },
                    leadingContent = { Icon(if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile, null) },
                    modifier = Modifier.clickable { if (file.isDirectory) path = file.absolutePath }
                )
            }
        }
    }
}

@Composable
fun MonitorScreen() {
    val monitor = remember { SystemMonitor() }
    var cpu by remember { mutableStateOf("Loading...") }
    var mem by remember { mutableStateOf(0L to 0L) }
    val processes = remember { mutableStateListOf<ProcessInfo>() }

    LaunchedEffect(Unit) {
        while (true) {
            cpu = monitor.getCpuUsage()
            mem = monitor.getMemoryUsage()
            processes.clear()
            processes.addAll(monitor.getRunningProcesses())
            delay(2000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("System Monitor", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("CPU: $cpu", fontSize = 12.sp, color = Color.Green)
        Text("Memory: Total ${mem.first / 1024} MB, Avail ${mem.second / 1024} MB", fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))
        Text("Running Processes:", style = MaterialTheme.typography.titleSmall)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(processes) { proc ->
                Text("${proc.pid} ${proc.name} [${proc.state}]", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun SettingsScreen(context: Context) {
    val sm = remember { SettingsManager(context) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        ListItem(headlineContent = { Text("Biometric Lock") }, trailingContent = {
            Switch(checked = sm.biometricEnabled.value, onCheckedChange = { sm.setBiometricEnabled(it) })
        })

        ListItem(headlineContent = { Text("Font Size") }, trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { sm.updateFontSize(sm.fontSize.value - 1) }) { Icon(Icons.Default.Remove, null) }
                Text(sm.fontSize.value.toString())
                IconButton(onClick = { sm.updateFontSize(sm.fontSize.value + 1) }) { Icon(Icons.Default.Add, null) }
            }
        })

        ListItem(headlineContent = { Text("Terminal Theme") }, trailingContent = {
            Text(sm.theme.value, modifier = Modifier.clickable {
                val themes = listOf("Hacker", "Amber", "White")
                val next = themes[(themes.indexOf(sm.theme.value) + 1) % themes.size]
                sm.setTheme(next)
            })
        })
    }
}
