package com.ubuntucli.settings

import android.content.Context

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("ubuntu_settings", Context.MODE_PRIVATE)

    var fontSize: Int
        get() = prefs.getInt("font_size", 14)
        set(value) = prefs.edit().putInt("font_size", value).apply()

    var theme: String
        get() = prefs.getString("theme", "Dracula") ?: "Dracula"
        set(value) = prefs.edit().putString("theme", value).apply()
}
