package com.ubuntucli.installer

import android.content.Context

class StateManager(context: Context) {
    private val prefs = context.getSharedPreferences("system_state", Context.MODE_PRIVATE)

    fun isInstalled(): Boolean = prefs.getBoolean("installed", false)
    fun setInstalled(value: Boolean) = prefs.edit().putBoolean("installed", value).apply()

    fun getLastBootTime(): Long = prefs.getLong("last_boot", 0L)
    fun updateBootTime() = prefs.edit().putLong("last_boot", System.currentTimeMillis()).apply()
}
