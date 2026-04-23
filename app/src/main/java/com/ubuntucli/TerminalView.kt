package com.ubuntucli

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel

data class TerminalTab(val id: Int, val title: String)

@Composable
fun TerminalView(vm: TerminalViewModel = viewModel()) {
    var nextTabId by remember { mutableIntStateOf(1) }
    val tabs = remember { mutableStateListOf(TerminalTab(0, "Tab 0")) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isSplitScreen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (vm.tabHistories.isEmpty()) {
            vm.startSession(0)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
                modifier = Modifier.weight(1f)
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(tab.title) }
                    )
                }
            }
            Row {
                IconButton(onClick = { isSplitScreen = !isSplitScreen }) {
                    Icon(Icons.Default.VerticalSplit, contentDescription = "Split Screen", tint = if (isSplitScreen) Color.Green else Color.Gray)
                }
                IconButton(onClick = {
                    val newId = nextTabId++
                    tabs.add(TerminalTab(newId, "Tab $newId"))
                    vm.startSession(newId)
                    selectedTabIndex = tabs.size - 1
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Tab")
                }
            }
        }

        if (isSplitScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    TerminalSession(tabs[selectedTabIndex], vm)
                }
                Divider(modifier = Modifier.fillMaxHeight().width(1.dp), color = Color.Gray)
                Box(modifier = Modifier.weight(1f)) {
                    // Show a secondary session in split screen
                    val secondaryIndex = if (selectedTabIndex + 1 < tabs.size) selectedTabIndex + 1 else 0
                    TerminalSession(tabs[secondaryIndex], vm)
                }
            }
        } else {
            TerminalSession(tabs[selectedTabIndex], vm)
        }
    }
}

@Composable
fun TerminalSession(tab: TerminalTab, vm: TerminalViewModel) {
    var inputText by remember { mutableStateOf("") }
    val history = vm.tabHistories[tab.id] ?: remember { mutableStateListOf() }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    // Auto-scroll to bottom on new output
    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        // Copy entire history to clipboard on long press
                        val fullText = history.joinToString("\n")
                        clipboardManager.setText(AnnotatedString(fullText))
                    }
                )
            }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(history) { line ->
                Text(
                    text = line,
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                // Copy single line to clipboard
                                clipboardManager.setText(AnnotatedString(line))
                            }
                        )
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text(
                text = "root@ubuntu:~# ",
                color = Color.Cyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                ),
                cursorBrush = SolidColor(Color.Green),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (inputText.isNotBlank()) {
                        vm.sendCommand(tab.id, inputText)
                        inputText = ""
                    }
                })
            )
        }
    }
}
