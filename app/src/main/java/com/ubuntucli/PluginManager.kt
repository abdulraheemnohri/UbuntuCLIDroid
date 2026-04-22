package com.ubuntucli

class PluginManager {
    fun listPlugins(): List<String> {
        return listOf("ip-info", "github-cli", "curl-shortcuts")
    }

    fun runPlugin(name: String): String {
        return "ubuntu plugin run $name"
    }
}
