package com.ubuntucli.monitor

import java.io.File

class SystemMonitor {
    fun getCpuUsage(): String {
        return try {
            File("/proc/stat").readLines().firstOrNull() ?: "N/A"
        } catch (e: Exception) { "Error" }
    }

    fun getMemoryInfo(): Pair<Long, Long> {
        val meminfo = File("/proc/meminfo").readLines()
        val total = meminfo.find { it.startsWith("MemTotal") }?.filter { it.isDigit() }?.toLong() ?: 0L
        val available = meminfo.find { it.startsWith("MemAvailable") }?.filter { it.isDigit() }?.toLong() ?: 0L
        return total to available
    }
}
