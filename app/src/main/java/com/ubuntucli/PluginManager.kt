package com.ubuntucli

import java.io.File

class PluginManager {
    fun listPlugins(): List<String> {
        // Mock directory for plugins - in reality would use Context.getFilesDir()
        val pluginDir = File("/sdcard/UbuntuCLI/plugins")
        if (!pluginDir.exists()) pluginDir.mkdirs()

        val files = pluginDir.listFiles { _, name -> name.endsWith(".sh") }
        val foundPlugins = files?.map { it.nameWithoutExtension } ?: emptyList()

        return if (foundPlugins.isEmpty()) {
            listOf("ip-info (demo)", "github-cli (demo)")
        } else {
            foundPlugins
        }
    }

    fun runPlugin(name: String): String {
        return "bash /sdcard/UbuntuCLI/plugins/$name.sh"
    }
}
