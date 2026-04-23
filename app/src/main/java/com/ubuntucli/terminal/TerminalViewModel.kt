package com.ubuntucli.terminal

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import java.io.File

class TerminalViewModel(application: Application) : AndroidViewModel(application) {
    val sessions = mutableStateListOf<TerminalSession>()
    val outputs = mutableMapOf<Int, MutableList<String>>()
    private val historyDir = File(application.filesDir, "history")

    init {
        if (!historyDir.exists()) historyDir.mkdirs()
    }

    fun createSession() {
        val id = if (sessions.isEmpty()) 0 else (sessions.maxOfOrNull { it.id } ?: 0) + 1
        val session = TerminalSession(id)
        val history = mutableStateListOf<String>()

        // Load history if exists
        val historyFile = File(historyDir, "session_$id.txt")
        if (historyFile.exists()) {
            history.addAll(historyFile.readLines().takeLast(1000))
        }

        outputs[id] = history

        session.onOutput = { text ->
            val lines = text.split("\n")
            lines.forEach { line ->
                if (line.isNotEmpty()) {
                    history.add(line)
                    if (history.size > 10000) history.removeAt(0)
                }
            }
            saveHistory(id, history)
        }

        sessions.add(session)
        session.start("/system/bin/sh", arrayOf("-"), emptyArray())
    }

    private fun saveHistory(id: Int, history: List<String>) {
        try {
            val file = File(historyDir, "session_$id.txt")
            file.writeText(history.takeLast(100).joinToString("\n"))
        } catch (e: Exception) {}
    }

    fun sendCommand(id: Int, cmd: String) {
        sessions.find { it.id == id }?.write(cmd + "\n")
    }

    override fun onCleared() {
        sessions.forEach { it.stop() }
        super.onCleared()
    }
}
