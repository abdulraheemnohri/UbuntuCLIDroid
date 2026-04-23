package com.ubuntucli.terminal

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class TerminalViewModel : ViewModel() {
    val sessions = mutableStateListOf<TerminalSession>()
    val outputs = mutableMapOf<Int, MutableList<String>>()

    fun createSession() {
        val id = if (sessions.isEmpty()) 0 else (sessions.maxOfOrNull { it.id } ?: 0) + 1
        val session = TerminalSession(id)
        val history = mutableStateListOf<String>()
        outputs[id] = history

        session.onOutput = { text ->
            val lines = text.split("\n")
            lines.forEach { line ->
                if (line.isNotEmpty()) {
                    history.add(line)
                    if (history.size > 10000) history.removeAt(0)
                }
            }
        }

        sessions.add(session)
        // Production: Use start.sh path
        session.start("/system/bin/sh", arrayOf("-"), emptyArray())
    }

    fun sendCommand(id: Int, cmd: String) {
        sessions.find { it.id == id }?.write(cmd + "\n")
    }

    override fun onCleared() {
        sessions.forEach { it.stop() }
        super.onCleared()
    }
}
