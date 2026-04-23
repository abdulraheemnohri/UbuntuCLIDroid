package com.ubuntucli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Screen { Terminal, Plugins, Packages, Monitor, Settings, Bridge }
enum class TerminalTheme(val primary: Color, val onBackground: Color) {
    Green(Color(0xFF00FF00), Color.Green),
    Amber(Color(0xFFFFB000), Color(0xFFFFB000)),
    White(Color.White, Color.White)
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.Terminal) }
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var terminalTheme by remember { mutableStateOf(TerminalTheme.Green) }
            var appPin by remember { mutableStateOf("") }
            var isLocked by remember { mutableStateOf(false) }

            MaterialTheme(colorScheme = darkColorScheme(terminalTheme.primary, terminalTheme.onBackground)) {
                if (isLocked && appPin.isNotEmpty()) {
                    LockScreen(appPin) { isLocked = false }
                } else {
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
                                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                                    label = { Text("File Bridge") },
                                    selected = currentScreen == Screen.Bridge,
                                    onClick = { currentScreen = Screen.Bridge; scope.launch { drawerState.close() } }
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
                                    },
                                    actions = {
                                        if (appPin.isNotEmpty()) {
                                            IconButton(onClick = { isLocked = true }) {
                                                Icon(Icons.Default.Lock, contentDescription = "Lock")
                                            }
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
                                    Screen.Settings -> SettingsView(terminalTheme, { terminalTheme = it }, appPin, { appPin = it })
                                    Screen.Bridge -> BridgeView()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BridgeView() {
    val bridge = remember { FileSystemBridge() }
    val files = remember { mutableStateListOf<String>() }
    LaunchedEffect(Unit) {
        files.addAll(bridge.listFiles("/sdcard"))
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("File Bridge (/sdcard)", style = MaterialTheme.typography.headlineSmall, color = Color.Green)
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(files) { file ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Text(file, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LockScreen(correctPin: String, onUnlock: () -> Unit) {
    var input by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("UbuntuCLI Locked", color = Color.Green, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        TextField(
            value = input,
            onValueChange = {
                input = it
                if (it == correctPin) onUnlock()
            },
            label = { Text("Enter PIN") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )
    }
}

@Composable
fun PluginsView() {
    val pm = remember { PluginManager() }
    val plugins = pm.listPlugins()
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(plugins) { plugin ->
            Text(text = plugin, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun PackagesView() {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { /* TODO */ }) { Text("Update Packages") }
        Spacer(Modifier.height(8.dp))
        Text("Popular Packages:", color = MaterialTheme.colorScheme.primary)
        listOf("vim", "git", "curl", "python3").forEach { pkg ->
            Text("- $pkg", color = Color.White, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun MonitorView() {
    val monitor = remember { SystemMonitor() }
    var cpuUsage by remember { mutableStateOf("Loading...") }
    var memUsage by remember { mutableStateOf("Loading...") }
    var uptime by remember { mutableStateOf("Loading...") }
    val processes = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        while (true) {
            cpuUsage = monitor.getCpuUsage()
            memUsage = monitor.getMemoryUsage()
            uptime = monitor.getUptime()
            processes.clear()
            processes.addAll(monitor.getRunningProcesses())
            delay(2000)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("System Monitor", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text(cpuUsage, color = Color.White)
        Text(memUsage, color = Color.White)
        Text(uptime, color = Color.White)
        Spacer(Modifier.height(16.dp))
        Text("Top Processes:", color = MaterialTheme.colorScheme.primary)
        processes.forEach { proc ->
            Text(proc, color = Color.Green, fontSize = 12.sp)
        }
    }
}

@Composable
fun SettingsView(currentTheme: TerminalTheme, onThemeChange: (TerminalTheme) -> Unit, pin: String, onPinChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text("Terminal Theme")
        Row {
            TerminalTheme.values().forEach { theme ->
                RadioButton(selected = currentTheme == theme, onClick = { onThemeChange(theme) })
                Text(theme.name, modifier = Modifier.padding(top = 12.dp, end = 8.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        TextField(value = pin, onValueChange = onPinChange, label = { Text("App PIN Lock") })
    }
}

@Composable
fun darkColorScheme(primary: Color, onBackground: Color) = darkColorScheme(
    primary = primary,
    background = Color.Black,
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onBackground = onBackground,
    onSurface = onBackground
)
