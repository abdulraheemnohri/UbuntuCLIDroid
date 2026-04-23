package com.ubuntucli.terminal

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TerminalView(vm: TerminalViewModel) {
    var selectedSessionId by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (vm.sessions.isEmpty()) vm.createSession()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        ScrollableTabRow(selectedTabIndex = selectedSessionId, edgePadding = 0.dp) {
            vm.sessions.forEachIndexed { index, session ->
                Tab(
                    selected = selectedSessionId == index,
                    onClick = { selectedSessionId = index },
                    text = { Text("Session ${session.id}", fontSize = 12.sp) }
                )
            }
            IconButton(onClick = { vm.createSession() }) {
                Icon(Icons.Default.Add, "New Tab")
            }
        }

        if (vm.sessions.isNotEmpty()) {
            val session = vm.sessions.getOrNull(selectedSessionId) ?: vm.sessions.first()
            TerminalSessionScreen(session, vm)
        }
    }
}

@Composable
fun TerminalSessionScreen(session: TerminalSession, vm: TerminalViewModel) {
    var input by remember { mutableStateOf("") }
    val history = vm.outputs[session.id] ?: emptyList<String>()
    val listState = rememberLazyListState()

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        LazyColumn(modifier = Modifier.weight(1f), state = listState) {
            items(history) { line ->
                Text(
                    text = AnsiParser.parse(line),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }

        Row {
            Text("$ ", color = Color.White, fontFamily = FontFamily.Monospace)
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 14.sp),
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
