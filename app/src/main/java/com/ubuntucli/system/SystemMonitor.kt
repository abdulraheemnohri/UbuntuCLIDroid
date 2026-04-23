package com.ubuntucli.system

import java.io.File

data class ProcessInfo(val pid: String, val name: String, val state: String, val threads: String)

class SystemMonitor {
    fun getCpuUsage(): String {
        return try {
            File("/proc/stat").readLines().take(5).joinToString("\n")
        } catch (e: Exception) { "Error" }
    }

    fun getMemoryUsage(): Pair<Long, Long> {
        return try {
            val lines = File("/proc/meminfo").readLines()
            val total = lines.find { it.startsWith("MemTotal") }?.filter { it.isDigit() }?.toLong() ?: 0L
            val free = lines.find { it.startsWith("MemAvailable") }?.filter { it.isDigit() }?.toLong() ?: 0L
            total to free
        } catch (e: Exception) { 0L to 0L }
    }

    fun getRunningProcesses(): List<ProcessInfo> {
        val procDir = File("/proc")
        return procDir.listFiles { f -> f.isDirectory && f.name.all { it.isDigit() } }
            ?.take(20)
            ?.mapNotNull { pidDir ->
                try {
                    val stat = File(pidDir, "stat").readText().split(" ")
                    ProcessInfo(
                        pid = stat[0],
                        name = stat[1].removeSurrounding("(", ")"),
                        state = stat[2],
                        threads = stat[19]
                    )
                } catch (e: Exception) { null }
            } ?: emptyList()
    }
}
