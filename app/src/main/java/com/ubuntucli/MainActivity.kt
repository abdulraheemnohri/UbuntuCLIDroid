package com.ubuntucli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.ubuntucli.`package`.PackageManager
import java.io.File
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: TerminalViewModel = viewModel()
            var currentScreen by remember { mutableStateOf("terminal") }

            Theme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("UbuntuCLI Droid") })
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Terminal, "Term") },
                                label = { Text("Terminal") },
                                selected = currentScreen == "terminal",
                                onClick = { currentScreen = "terminal" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Inventory, "Pkg") },
                                label = { Text("Packages") },
                                selected = currentScreen == "packages",
                                onClick = { currentScreen = "packages" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Folder, "Files") },
                                label = { Text("Files") },
                                selected = currentScreen == "files",
                                onClick = { currentScreen = "files" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.MonitorHeart, "Mon") },
                                label = { Text("Monitor") },
                                selected = currentScreen == "monitor",
                                onClick = { currentScreen = "monitor" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Settings, "Set") },
                                label = { Text("Settings") },
                                selected = currentScreen == "settings",
                                onClick = { currentScreen = "settings" }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "terminal" -> TerminalView(vm)
                            "packages" -> PackagesScreen(vm)
                            "files" -> FilesScreen()
                            "monitor" -> MonitorScreen()
                            "settings" -> SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PackagesScreen(vm: TerminalViewModel) {
    val pm = remember { PackageManager() }
    val pkgs = pm.getPopularPackages()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Package Manager", style = MaterialTheme.typography.headlineMedium) }
        items(pkgs) { pkg ->
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
    var currentPath by remember { mutableStateOf("/") }
    val files = remember(currentPath) { File(currentPath).listFiles()?.toList() ?: emptyList() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Path: $currentPath", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(files) { file ->
                ListItem(
                    headlineContent = { Text(file.name) },
                    supportingContent = { Text(if (file.isDirectory) "Directory" else "${file.length()} bytes") },
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

    LaunchedEffect(Unit) {
        while (true) {
            cpu = monitor.getCpuUsage()
            mem = monitor.getMemoryUsage()
            delay(1000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("System Monitor", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("CPU: $cpu", fontFamily = FontFamily.Monospace)
        Text("Memory: Total ${mem.first / 1024} MB, Available ${mem.second / 1024} MB")
    }
}

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        ListItem(headlineContent = { Text("Font Size") }, trailingContent = { Text("14") })
        ListItem(headlineContent = { Text("Theme") }, trailingContent = { Text("Hacker Green") })
        ListItem(headlineContent = { Text("PIN Lock") }, trailingContent = { Switch(false, onCheckedChange = {}) })
    }
}
