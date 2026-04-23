package com.ubuntucli

class AIEngine {
    fun generateCommand(prompt: String): String {
        return when {
            prompt.contains("install") -> "apt install ${prompt.substringAfter("install").trim()}"
            prompt.contains("list") -> "ls -la"
            prompt.contains("update") -> "apt update && apt upgrade"
            else -> "ubuntu-ai \"$prompt\""
        }
    }

    fun suggestFix(errorLog: String): String {
        return when {
            errorLog.contains("Permission denied") -> "Try running with sudo or check file permissions."
            errorLog.contains("command not found") -> "The package might not be installed. Try 'apt install <package>'."
            else -> "AI suggests checking dependencies."
        }
    }
}
