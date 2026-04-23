package com.ubuntucli

import java.io.File

class SystemMonitor {
    fun getCpuUsage(): String {
        return try {
            val stats = File("/proc/stat").readLines().firstOrNull() ?: "Unknown"
            "CPU: $stats"
        } catch (e: Exception) {
            "CPU: Error reading /proc/stat"
        }
    }

    fun getMemoryUsage(): String {
        return try {
            val memInfo = File("/proc/meminfo").readLines().take(2)
            "MEM: ${memInfo.joinToString(", ")}"
        } catch (e: Exception) {
            "MEM: Error reading /proc/meminfo"
        }
    }

    fun getUptime(): String {
        return try {
            val uptime = File("/proc/uptime").readText().split(" ").firstOrNull() ?: "0"
            "Uptime: ${uptime}s"
        } catch (e: Exception) {
            "Uptime: Unknown"
        }
    }

    fun getRunningProcesses(): List<String> {
        return try {
            val procDir = File("/proc")
            procDir.listFiles { f -> f.isDirectory && f.name.all { it.isDigit() } }
                ?.take(10)
                ?.map { pidDir ->
                    val name = File(pidDir, "comm").readText().trim()
                    "PID ${pidDir.name}: $name"
                } ?: emptyList()
        } catch (e: Exception) {
            listOf("Error reading processes")
        }
    }
}
