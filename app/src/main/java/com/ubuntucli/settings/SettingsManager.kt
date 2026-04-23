package com.ubuntucli.settings

import android.content.Context
import androidx.compose.runtime.mutableStateOf

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("ubuntu_settings", Context.MODE_PRIVATE)

    var fontSize = mutableStateOf(prefs.getInt("font_size", 12))
    var theme = mutableStateOf(prefs.getString("theme", "Dracula") ?: "Dracula")
    var pinLockEnabled = mutableStateOf(prefs.getBoolean("pin_lock", false))
    var defaultShell = mutableStateOf(prefs.getString("default_shell", "/bin/bash") ?: "/bin/bash")
    var scrollbackSize = mutableStateOf(prefs.getInt("scrollback_size", 10000))

    fun updateFontSize(newSize: Int) {
        fontSize.value = newSize
        prefs.edit().putInt("font_size", newSize).apply()
    }

    fun updateTheme(newTheme: String) {
        theme.value = newTheme
        prefs.edit().putString("theme", newTheme).apply()
    }

    fun updateDefaultShell(shell: String) {
        defaultShell.value = shell
        prefs.edit().putString("default_shell", shell).apply()
    }

    fun updateScrollbackSize(size: Int) {
        scrollbackSize.value = size
        prefs.edit().putInt("scrollback_size", size).apply()
    }

    fun updatePinLockEnabled(enabled: Boolean) {
        pinLockEnabled.value = enabled
        prefs.edit().putBoolean("pin_lock", enabled).apply()
    }
}
