package com.ubuntucli.plugin

import java.io.File

data class Plugin(val name: String, val path: String, val description: String)

class PluginManager(private val pluginDir: File) {
    fun listPlugins(): List<Plugin> {
        if (!pluginDir.exists()) return emptyList()
        return pluginDir.listFiles()?.filter { it.extension == "sh" }?.map {
            Plugin(it.nameWithoutExtension, it.absolutePath, "User-defined plugin")
        } ?: emptyList()
    }

    fun executePlugin(name: String): String {
        return "ubuntu plugin run $name"
    }
}
