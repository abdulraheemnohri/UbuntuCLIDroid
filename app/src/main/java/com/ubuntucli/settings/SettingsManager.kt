package com.ubuntucli.settings

import android.content.Context
import androidx.compose.runtime.mutableStateOf

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("ubuntu_settings", Context.MODE_PRIVATE)

    val fontSize = mutableStateOf(prefs.getInt("font_size", 12))
    val defaultShell = mutableStateOf(prefs.getString("default_shell", "/bin/bash") ?: "/bin/bash")
    val biometricEnabled = mutableStateOf(prefs.getBoolean("biometric_enabled", false))
    val theme = mutableStateOf(prefs.getString("theme", "Hacker") ?: "Hacker")

    fun updateFontSize(size: Int) {
        fontSize.value = size
        prefs.edit().putInt("font_size", size).apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        biometricEnabled.value = enabled
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    fun setTheme(newTheme: String) {
        theme.value = newTheme
        prefs.edit().putString("theme", newTheme).apply()
    }
}
