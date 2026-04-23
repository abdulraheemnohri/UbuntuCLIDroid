package com.ubuntucli.plugin

import java.io.File

data class Plugin(val name: String, val path: String, val description: String)

class PluginManager {
    fun loadPlugins(dir: String): List<Plugin> {
        val pluginDir = File(dir)
        if (!pluginDir.exists()) pluginDir.mkdirs()
        return pluginDir.listFiles { _, name -> name.endsWith(".sh") }?.map {
            Plugin(it.nameWithoutExtension, it.absolutePath, "User script")
        } ?: emptyList()
    }
}
