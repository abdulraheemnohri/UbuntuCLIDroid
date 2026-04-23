package com.ubuntucli.terminal

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ubuntucli.ai.AIEngine
import kotlinx.coroutines.launch
import java.io.File

class TerminalViewModel(application: Application) : AndroidViewModel(application) {
    val sessions = mutableStateListOf<TerminalSession>()
    val outputs = mutableMapOf<Int, MutableList<String>>()
    private val historyDir = File(getApplication<Application>().filesDir, "history")
    val aiEngine = AIEngine(getApplication())

    init {
        if (!historyDir.exists()) historyDir.mkdirs()
    }

    fun createSession() {
        val id = if (sessions.isEmpty()) 0 else (sessions.maxOfOrNull { it.id } ?: 0) + 1
        val session = TerminalSession(id)
        val history = mutableStateListOf<String>()

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

        // Use the start script if it exists, otherwise fallback to sh
        val startScript = File(getApplication<Application>().filesDir, "ubuntu/bin/sh")
        val shellPath = if (startScript.exists()) startScript.absolutePath else "/system/bin/sh"

        session.start(shellPath, arrayOf("-l"), emptyArray())
    }

    private fun saveHistory(id: Int, history: List<String>) {
        try {
            val file = File(historyDir, "session_$id.txt")
            file.writeText(history.takeLast(100).joinToString("\n"))
        } catch (e: Exception) {}
    }

    fun sendCommand(id: Int, command: String) {
        val history = outputs[id] ?: return

        if (command == "clear") {
            history.clear()
            return
        }

        val session = sessions.find { it.id == id }
        session?.write(command + "\n")
    }

    fun requestAiSuggestion(sessionId: Int, prompt: String) {
        viewModelScope.launch {
            val suggestion = aiEngine.getCommandSuggestion(prompt)
            outputs[sessionId]?.add("\n[AI Suggestion]: $suggestion\n")
        }
    }

    override fun onCleared() {
        sessions.forEach { it.stop() }
        super.onCleared()
    }
}
