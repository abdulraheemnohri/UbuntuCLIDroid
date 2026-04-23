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
import com.ubuntucli.plugin.PluginManager
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
            var initStatus by remember { mutableStateOf("Checking system...") }
            val scope = rememberCoroutineScope()

            Theme {
                if (!isInitialized) {
                    InitializationScreen(initStatus)
                    LaunchedEffect(Unit) {
                        scope.launch {
                            initializer.initialize { status ->
                                initStatus = status
                                if (status == "System Ready.") {
                                    isInitialized = true
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

@Composable
fun InitializationScreen(status: String) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Color.Green)
        Spacer(Modifier.height(24.dp))
        Text("UbuntuCLI Droid", style = MaterialTheme.typography.headlineMedium, color = Color.Green)
        Text("Booting DNA Layer...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Text(status, color = Color.White, fontFamily = FontFamily.Monospace)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(currentScreen: String, onScreenChange: (String) -> Unit, vm: TerminalViewModel, context: Context) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("UbuntuCLI Droid") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Terminal, "Term") },
                    label = { Text("Terminal") },
                    selected = currentScreen == "terminal",
                    onClick = { onScreenChange("terminal") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Inventory, "Pkg") },
                    label = { Text("Packages") },
                    selected = currentScreen == "packages",
                    onClick = { onScreenChange("packages") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Extension, "Plugin") },
                    label = { Text("Plugins") },
                    selected = currentScreen == "plugins",
                    onClick = { onScreenChange("plugins") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Folder, "Files") },
                    label = { Text("Files") },
                    selected = currentScreen == "files",
                    onClick = { onScreenChange("files") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MonitorHeart, "Mon") },
                    label = { Text("Monitor") },
                    selected = currentScreen == "monitor",
                    onClick = { onScreenChange("monitor") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, "Set") },
                    label = { Text("Settings") },
                    selected = currentScreen == "settings",
                    onClick = { onScreenChange("settings") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "terminal" -> TerminalView(vm)
                "packages" -> PackagesScreen(vm, onScreenChange)
                "plugins" -> PluginsScreen(vm, onScreenChange, context)
                "files" -> FilesScreen()
                "monitor" -> MonitorScreen()
                "settings" -> SettingsScreen(context)
            }
        }
    }
}

@Composable
fun PackagesScreen(vm: TerminalViewModel, onScreenChange: (String) -> Unit) {
    val pm = remember { PackageManager() }
    val pkgs = pm.getPopularPackages()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Package Manager", style = MaterialTheme.typography.headlineMedium)
                Button(onClick = {
                    vm.sendCommand(0, pm.getAptUpdateCmd())
                    onScreenChange("terminal")
                }) { Text("Update") }
            }
        }
        items(pkgs) { pkg ->
            ListItem(
                headlineContent = { Text(pkg) },
                trailingContent = {
                    Button(onClick = {
                        vm.sendCommand(0, pm.getAptInstallCmd(pkg))
                        onScreenChange("terminal")
                    }) {
                        Text("Install")
                    }
                }
            )
        }
    }
}

@Composable
fun PluginsScreen(vm: TerminalViewModel, onScreenChange: (String) -> Unit, context: Context) {
    val pm = remember { PluginManager() }
    val plugins = pm.loadPlugins(File(context.filesDir, "ubuntu/root/plugins").absolutePath)

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Plugins", style = MaterialTheme.typography.headlineMedium) }
        if (plugins.isEmpty()) {
            item { Text("No plugins found in /root/plugins", color = Color.Gray) }
        }
        items(plugins) { plugin ->
            ListItem(
                headlineContent = { Text(plugin.name) },
                trailingContent = {
                    Button(onClick = {
                        vm.sendCommand(0, "bash ${plugin.path}")
                        onScreenChange("terminal")
                    }) { Text("Run") }
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Path: $currentPath", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = {
                if (currentPath != "/") currentPath = File(currentPath).parent ?: "/"
            }) {
                Icon(Icons.Default.ArrowUpward, "Up")
            }
        }
        Divider()
        LazyColumn {
            items(files) { file ->
                ListItem(
                    headlineContent = { Text(file.name) },
                    supportingContent = { Text(if (file.isDirectory) "Directory" else "${file.length()} bytes") },
                    leadingContent = { Icon(if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile, null) },
                    trailingContent = {
                        IconButton(onClick = { fm.deleteFile(file.absolutePath) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                        }
                    },
                    modifier = Modifier.clickable {
                        if (file.isDirectory) currentPath = file.absolutePath
                    }
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
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${proc.pid} ${proc.name}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                    Text("S: ${proc.state} T: ${proc.threads}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
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
        ListItem(
            headlineContent = { Text("Font Size") },
            trailingContent = { Text(sm.fontSize.value.toString()) },
            modifier = Modifier.clickable { sm.updateFontSize(sm.fontSize.value + 1) }
        )
        ListItem(
            headlineContent = { Text("Terminal Theme") },
            trailingContent = { Text(sm.theme.value) }
        )
        ListItem(
            headlineContent = { Text("PIN Lock") },
            trailingContent = { Switch(sm.pinEnabled.value, onCheckedChange = { sm.updatePinEnabled(it) }) }
        )
    }
}
