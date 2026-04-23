package com.ubuntucli.settings

import android.content.Context
import androidx.compose.runtime.mutableStateOf

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("ubuntu_settings", Context.MODE_PRIVATE)

    var fontSize = mutableStateOf(prefs.getInt("font_size", 12))
    var theme = mutableStateOf(prefs.getString("theme", "Dracula") ?: "Dracula")
    var pinEnabled = mutableStateOf(prefs.getBoolean("pin_enabled", false))

    fun updateFontSize(newSize: Int) {
        fontSize.value = newSize
        prefs.edit().putInt("font_size", newSize).apply()
    }

    fun updateTheme(newTheme: String) {
        theme.value = newTheme
        prefs.edit().putString("theme", newTheme).apply()
    }

    fun updatePinEnabled(enabled: Boolean) {
        pinEnabled.value = enabled
        prefs.edit().putBoolean("pin_enabled", enabled).apply()
    }
}
