package com.ubuntucli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

enum class Screen { Terminal, Plugins, Packages, Monitor, Settings }

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.Terminal) }
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            MaterialTheme(colorScheme = darkColorScheme()) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(Modifier.height(12.dp))
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Terminal, contentDescription = null) },
                                label = { Text("Terminal") },
                                selected = currentScreen == Screen.Terminal,
                                onClick = { currentScreen = Screen.Terminal; scope.launch { drawerState.close() } }
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Extension, contentDescription = null) },
                                label = { Text("Plugins") },
                                selected = currentScreen == Screen.Plugins,
                                onClick = { currentScreen = Screen.Plugins; scope.launch { drawerState.close() } }
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                                label = { Text("Packages") },
                                selected = currentScreen == Screen.Packages,
                                onClick = { currentScreen = Screen.Packages; scope.launch { drawerState.close() } }
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.MonitorHeart, contentDescription = null) },
                                label = { Text("System Monitor") },
                                selected = currentScreen == Screen.Monitor,
                                onClick = { currentScreen = Screen.Monitor; scope.launch { drawerState.close() } }
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = { Text("Settings") },
                                selected = currentScreen == Screen.Settings,
                                onClick = { currentScreen = Screen.Settings; scope.launch { drawerState.close() } }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = { Text("UbuntuCLI Droid") },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            when (currentScreen) {
                                Screen.Terminal -> TerminalView()
                                Screen.Plugins -> PluginsView()
                                Screen.Packages -> PackagesView()
                                Screen.Monitor -> MonitorView()
                                Screen.Settings -> SettingsView()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PluginsView() {
    val pm = remember { PluginManager() }
    val plugins = pm.listPlugins()
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(plugins) { plugin ->
            Text(text = plugin, color = Color.Green, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun PackagesView() {
    val pkgM = remember { PackageManager() }
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { /* TODO */ }) { Text("Update Packages") }
        Spacer(Modifier.height(8.dp))
        Text("Popular Packages:", color = Color.Green)
        listOf("vim", "git", "curl", "python3").forEach { pkg ->
            Text("- $pkg", color = Color.White, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun MonitorView() {
    val monitor = remember { SystemMonitor() }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("System Monitor", style = MaterialTheme.typography.headlineSmall, color = Color.Green)
        Spacer(Modifier.height(16.dp))
        Text(monitor.getCpuUsage(), color = Color.White)
        Text(monitor.getMemoryUsage(), color = Color.White)
        Text(monitor.getUptime(), color = Color.White)
    }
}

@Composable
fun SettingsView() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        var pin by remember { mutableStateOf("") }
        TextField(value = pin, onValueChange = { pin = it }, label = { Text("App PIN Lock") })
    }
}

@Composable
fun darkColorScheme() = darkColorScheme(
    primary = Color(0xFF00FF00),
    background = Color.Black,
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onBackground = Color.Green,
    onSurface = Color.Green
)
