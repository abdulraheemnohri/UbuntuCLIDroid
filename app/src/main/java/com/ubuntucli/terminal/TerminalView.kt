package com.ubuntucli.terminal

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ubuntucli.settings.SettingsManager

@Composable
fun TerminalView(vm: TerminalViewModel) {
    var selectedSessionId by remember { mutableIntStateOf(0) }
    var isSplitScreen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sm = remember { SettingsManager(context) }

    LaunchedEffect(Unit) {
        if (vm.sessions.isEmpty()) vm.createSession()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface), verticalAlignment = Alignment.CenterVertically) {
            ScrollableTabRow(selectedTabIndex = selectedSessionId, edgePadding = 0.dp, modifier = Modifier.weight(1f)) {
                vm.sessions.forEachIndexed { index, session ->
                    Tab(
                        selected = selectedSessionId == index,
                        onClick = { selectedSessionId = index },
                        text = { Text("Tab ${session.id}", fontSize = 10.sp) }
                    )
                }
            }
            IconButton(onClick = { isSplitScreen = !isSplitScreen }) {
                Icon(Icons.Default.VerticalSplit, "Split", tint = if (isSplitScreen) Color.Green else Color.Gray)
            }
            IconButton(onClick = { vm.createSession() }) {
                Icon(Icons.Default.Add, "New")
            }
        }

        if (isSplitScreen && vm.sessions.size >= 2) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    TerminalSessionScreen(vm.sessions[selectedSessionId], vm, sm)
                }
                Divider(color = Color.Gray, thickness = 1.dp)
                Box(modifier = Modifier.weight(1f)) {
                    val secondaryIndex = (selectedSessionId + 1) % vm.sessions.size
                    TerminalSessionScreen(vm.sessions[secondaryIndex], vm, sm)
                }
            }
        } else if (vm.sessions.isNotEmpty()) {
            val session = vm.sessions.getOrNull(selectedSessionId) ?: vm.sessions.first()
            TerminalSessionScreen(session, vm, sm)
        }
    }
}

@Composable
fun TerminalSessionScreen(session: TerminalSession, vm: TerminalViewModel, sm: SettingsManager) {
    var input by remember { mutableStateOf("") }
    val history = vm.outputs[session.id] ?: emptyList<String>()
    val listState = rememberLazyListState()
    val fontSize = sm.fontSize.value.sp

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        LazyColumn(modifier = Modifier.weight(1f), state = listState) {
            items(history) { line ->
                Text(
                    text = AnsiParser.parse(line),
                    fontFamily = FontFamily.Monospace,
                    fontSize = fontSize
                )
            }
        }

        VirtualKeys { key ->
            if (key == "CTRL") {
                // Toggle state or similar
            } else if (key == "TAB") {
                session.write("\t")
            } else {
                session.write(key)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$ ", color = Color.Cyan, fontFamily = FontFamily.Monospace, fontSize = fontSize)
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = fontSize),
                cursorBrush = SolidColor(Color.Green),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (input.isNotBlank()) {
                        vm.sendCommand(session.id, input)
                        input = ""
                    }
                })
            )
        }
    }
}

@Composable
fun VirtualKeys(onKey: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf("CTRL", "ALT", "TAB", "ESC", "-", "/", "|").forEach { key ->
            TextButton(
                onClick = { onKey(key) },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(32.dp).width(44.dp)
            ) {
                Text(key, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
