package com.ubuntucli

class AIEngine {
    fun generateCommand(prompt: String): String {
        return "ubuntu-ai \"$prompt\""
    }

    fun suggestFix(errorLog: String): String {
        return "AI suggests checking permissions or missing dependencies."
    }
}
