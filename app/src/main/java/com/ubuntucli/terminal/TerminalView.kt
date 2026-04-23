package com.ubuntucli.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TerminalView(vm: TerminalViewModel) {
    var selectedSessionId by remember { mutableIntStateOf(0) }
    var isSplitScreen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (vm.sessions.isEmpty()) vm.createSession()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedSessionId,
                edgePadding = 0.dp,
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent
            ) {
                vm.sessions.forEachIndexed { index, session ->
                    Tab(
                        selected = selectedSessionId == index,
                        onClick = { selectedSessionId = index },
                        text = { Text("Tab ${session.id}", fontSize = 10.sp) }
                    )
                }
            }
            Row {
                IconButton(onClick = { isSplitScreen = !isSplitScreen }) {
                    Icon(Icons.Default.VerticalSplit, "Split", tint = if (isSplitScreen) Color.Green else Color.Gray)
                }
                IconButton(onClick = { vm.createSession() }) {
                    Icon(Icons.Default.Add, "New")
                }
            }
        }

        if (isSplitScreen && vm.sessions.size >= 2) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    TerminalSessionScreen(vm.sessions[selectedSessionId], vm)
                }
                Divider(color = Color.Gray, thickness = 1.dp)
                Box(modifier = Modifier.weight(1f)) {
                    val nextIdx = (selectedSessionId + 1) % vm.sessions.size
                    TerminalSessionScreen(vm.sessions[nextIdx], vm)
                }
            }
        } else if (vm.sessions.isNotEmpty()) {
            TerminalSessionScreen(vm.sessions.getOrElse(selectedSessionId) { vm.sessions.first() }, vm)
        }
    }
}

@Composable
fun TerminalSessionScreen(session: TerminalSession, vm: TerminalViewModel) {
    var input by remember { mutableStateOf("") }
    val history = vm.outputs[session.id] ?: emptyList<String>()
    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) listState.animateScrollToItem(history.size - 1)
    }

    Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { input += clipboard.getText()?.text ?: "" }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.ContentPaste, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
            IconButton(onClick = { vm.sendCommand(session.id, "clear") }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.DeleteSweep, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    clipboard.setText(AnnotatedString(history.joinToString("\n")))
                })
            },
            state = listState
        ) {
            items(history) { line ->
                Text(
                    text = AnsiParser.parse(line),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(onTap = { clipboard.setText(AnnotatedString(line)) })
                    }
                )
            }
        }

        VirtualKeys { key ->
            when (key) {
                "TAB" -> session.write("\t")
                "CTRL-C" -> session.write("\u0003")
                "UP" -> session.write("\u001B[A")
                "DOWN" -> session.write("\u001B[B")
                "LEFT" -> session.write("\u001B[D")
                "RIGHT" -> session.write("\u001B[C")
                "ESC" -> session.write("\u001B")
                else -> session.write(key)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$ ", color = Color.Cyan, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
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
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("CTRL-C", "ESC", "TAB", "UP", "DOWN", "LEFT", "RIGHT").forEach { key ->
                TextButton(
                    onClick = { onKey(key) },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(28.dp).width(48.dp)
                ) {
                    Text(key, fontSize = 8.sp, color = Color.Gray)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("-", "/", "|", "..", ".", "*", "~").forEach { key ->
                TextButton(
                    onClick = { onKey(key) },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(28.dp).width(48.dp)
                ) {
                    Text(key, fontSize = 9.sp, color = Color.Green)
                }
            }
        }
    }
}
