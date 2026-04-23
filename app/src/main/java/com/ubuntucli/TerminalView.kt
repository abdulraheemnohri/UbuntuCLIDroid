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
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.VerticalSplit
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
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(tab.title, fontSize = 12.sp) }
                    )
                }
            }
            Row {
                IconButton(onClick = { isSplitScreen = !isSplitScreen }) {
                    Icon(Icons.Default.VerticalSplit, contentDescription = "Split", tint = if (isSplitScreen) Color.Green else Color.Gray, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = {
                    val newId = nextTabId++
                    tabs.add(TerminalTab(newId, "Tab $newId"))
                    vm.startSession(newId)
                    selectedTabIndex = tabs.size - 1
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp))
                }
            }
        }

        if (isSplitScreen) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    TerminalSession(tabs[selectedTabIndex], vm)
                }
                Divider(modifier = Modifier.fillMaxWidth().height(1.dp), color = Color.Gray)
                Box(modifier = Modifier.weight(1f)) {
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

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = {
                val text = clipboardManager.getText()?.text ?: ""
                inputText += text
            }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = { vm.sendCommand(tab.id, "clear") }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            val fullText = history.joinToString("\n")
                            clipboardManager.setText(AnnotatedString(fullText))
                        }
                    )
                }
        ) {
            items(history) { line ->
                Text(
                    text = line,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
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
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "root@ubuntu:~# ",
                color = Color.Cyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
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
