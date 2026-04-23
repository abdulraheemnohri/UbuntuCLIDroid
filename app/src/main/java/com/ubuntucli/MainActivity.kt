package com.ubuntucli

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ubuntucli.terminal.TerminalViewModel
import com.ubuntucli.terminal.TerminalView
import com.ubuntucli.ui.Theme
import com.ubuntucli.system.SystemMonitor
import com.ubuntucli.system.ProcessInfo
import com.ubuntucli.`package`.PackageManager
import com.ubuntucli.core.SystemInitializer
import com.ubuntucli.filemanager.FileManager
import com.ubuntucli.settings.SettingsManager
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initializer = SystemInitializer(this)

        setContent {
            val vm: TerminalViewModel = viewModel()
            var currentScreen by remember { mutableStateOf("terminal") }
            var isInitialized by remember { mutableStateOf(initializer.isInitialized()) }
            var initStatus by remember { mutableStateOf("Checking DNA layer...") }
            var hasError by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            Theme {
                if (!isInitialized) {
                    Column(
                        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!hasError) {
                            CircularProgressIndicator(color = Color.Green)
                        } else {
                            Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("UbuntuCLI Droid", style = MaterialTheme.typography.headlineMedium, color = Color.Green)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = initStatus,
                            color = if (hasError) Color.Red else Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )

                        if (hasError) {
                            Spacer(Modifier.height(24.dp))
                            Button(onClick = {
                                hasError = false
                                initStatus = "Retrying DNA check..."
                                scope.launch {
                                    try {
                                        initializer.initialize { initStatus = it }
                                        isInitialized = true
                                    } catch (e: Exception) {
                                        hasError = true
                                        initStatus = "Error: ${e.message}"
                                    }
                                }
                            }) {
                                Text("Retry Initialization")
                            }
                        }

                        LaunchedEffect(Unit) {
                            if (!isInitialized) {
                                try {
                                    initializer.initialize { initStatus = it }
                                    isInitialized = true
                                } catch (e: Exception) {
                                    hasError = true
                                    initStatus = "Error: ${e.message}"
                                }
                            }
                        }
                    }
                } else {
                    MainScaffold(currentScreen, { currentScreen = it }, vm, this@MainActivity)
                }
            }
        }
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
                ).forEach { (id, icon, label) ->
                    NavigationBarItem(
                        icon = { Icon(icon, label) },
                        label = { Text(label) },
                        selected = currentScreen == id,
                        onClick = { onScreenChange(id) }
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
    var currentPath by remember { mutableStateOf("/") }
    val files = remember(currentPath) { fm.listFiles(currentPath) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (currentPath != "/") currentPath = File(currentPath).parent ?: "/" }) {
                Icon(Icons.Default.ArrowBack, null)
            }
            Text("Path: $currentPath", style = MaterialTheme.typography.titleMedium)
        }
        Divider()
        LazyColumn {
            items(files) { file ->
                ListItem(
                    headlineContent = { Text(file.name) },
                    supportingContent = { Text(if (file.isDirectory) "Directory" else "${file.length()} bytes") },
                    leadingContent = { Icon(if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile, null) },
                    modifier = Modifier.clickable { if (file.isDirectory) currentPath = file.absolutePath }
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
        ListItem(headlineContent = { Text("Font Size") }, trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { sm.updateFontSize(sm.fontSize.value - 1) }) { Icon(Icons.Default.Remove, null) }
                Text(sm.fontSize.value.toString())
                IconButton(onClick = { sm.updateFontSize(sm.fontSize.value + 1) }) { Icon(Icons.Default.Add, null) }
            }
        })
        ListItem(headlineContent = { Text("Default Shell") }, trailingContent = { Text(sm.defaultShell.value) })
        ListItem(headlineContent = { Text("Scrollback Size") }, trailingContent = { Text(sm.scrollbackSize.value.toString()) })
        ListItem(headlineContent = { Text("PIN Lock") }, trailingContent = { Switch(sm.pinLockEnabled.value, onCheckedChange = { sm.updatePinLockEnabled(it) }) })
    }
}
