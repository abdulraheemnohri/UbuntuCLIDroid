package com.ubuntucli

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class TerminalViewModel : ViewModel() {
    private val sessions = mutableMapOf<Int, ShellSession>()
    val tabHistories = mutableMapOf<Int, MutableList<String>>()
    private val aiEngine = AIEngine()
    private val pluginManager = PluginManager()

    fun startSession(tabId: Int) {
        val session = ShellSession()
        sessions[tabId] = session
        val history = mutableStateListOf<String>()
        tabHistories[tabId] = history

        history.add("UbuntuCLI Droid v1.0")
        history.add("Type 'help' for available commands.")

        viewModelScope.launch(Dispatchers.IO) {
            // In a real app, this would point to the proot/ubuntu environment
            session.startSession("/system/bin/sh", arrayOf("-"), emptyArray())
            val inputStream = session.inputStream
            if (inputStream != null) {
                readFromPty(tabId, inputStream)
            } else {
                withContext(Dispatchers.Main) {
                    history.add("Error: Could not connect to PTY.")
                }
            }
        }
    }

    private suspend fun readFromPty(tabId: Int, inputStream: InputStream) {
        val reader = inputStream.bufferedReader()
        try {
            while (true) {
                val line = withContext(Dispatchers.IO) { reader.readLine() } ?: break
                withContext(Dispatchers.Main) {
                    tabHistories[tabId]?.add(line)
                    if ((tabHistories[tabId]?.size ?: 0) > 2000) {
                        tabHistories[tabId]?.removeAt(0)
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                tabHistories[tabId]?.add("PTY Connection closed.")
            }
        }
    }

    fun sendCommand(tabId: Int, command: String) {
        val history = tabHistories[tabId] ?: return

        when {
            command.startsWith("ubuntu-ai") -> {
                val prompt = command.removePrefix("ubuntu-ai").trim().removeSurrounding("\"")
                history.add("root@ubuntu:~# $command")
                val aiResponse = aiEngine.generateCommand(prompt)
                history.add("AI suggesting: $aiResponse")
            }
            command.startsWith("ubuntu plugin run") -> {
                val pluginName = command.removePrefix("ubuntu plugin run").trim()
                history.add("root@ubuntu:~# $command")
                history.add("Executing plugin: $pluginName...")
                val pluginCmd = pluginManager.runPlugin(pluginName)
                executeRaw(tabId, pluginCmd)
            }
            command == "clear" -> {
                history.clear()
                history.add("Console cleared.")
            }
            command == "help" -> {
                history.add("root@ubuntu:~# help")
                history.add("Available Commands:")
                history.add("  ubuntu-ai \"prompt\"   - AI command generator")
                history.add("  ubuntu plugin run <n> - Execute a plugin")
                history.add("  clear                - Clear the screen")
                history.add("  apt install <pkg>    - Package installation")
                history.add("  exit                 - Close session")
            }
            else -> {
                executeRaw(tabId, command)
            }
        }
    }

    private fun executeRaw(tabId: Int, command: String) {
        val session = sessions[tabId]
        viewModelScope.launch(Dispatchers.IO) {
            try {
                session?.outputStream?.write((command + "\n").toByteArray())
                session?.outputStream?.flush()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tabHistories[tabId]?.add("Execution Error: ${e.message}")
                }
            }
        }
    }
}
