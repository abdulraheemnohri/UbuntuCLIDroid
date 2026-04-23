package com.ubuntucli

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

class TerminalViewModel : ViewModel() {
    private val sessions = mutableMapOf<Int, ShellSession>()
    val tabHistories = mutableMapOf<Int, MutableList<String>>()

    fun startSession(tabId: Int) {
        val session = ShellSession()
        sessions[tabId] = session
        val history = mutableStateListOf<String>()
        tabHistories[tabId] = history

        history.add("Initializing Ubuntu environment for Tab $tabId...")

        viewModelScope.launch(Dispatchers.IO) {
            session.startSession("/system/bin/sh", arrayOf("-"), emptyArray())
            // Simulation of reading from PTY
            history.add("Connected to PTY. Welcome to UbuntuCLI Droid.")
        }
    }

    fun sendCommand(tabId: Int, command: String) {
        val history = tabHistories[tabId]
        history?.add("root@ubuntu:~# $command")

        // In a real implementation:
        // sessions[tabId]?.outputStream?.write((command + "\n").toByteArray())

        // Simulation for now
        viewModelScope.launch {
            if (command == "help") {
                history?.add("Available commands: help, clear, exit, ubuntu-ai, apt")
            } else if (command.startsWith("apt")) {
                history?.add("Processing package operation...")
                history?.add("Operation successful.")
            } else {
                history?.add("Executed: $command")
            }
        }
    }
}
