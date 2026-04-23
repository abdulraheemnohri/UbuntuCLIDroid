package com.ubuntucli.system

import java.io.File

class SystemMonitor {
    fun getCpuUsage(): String {
        return try {
            File("/proc/stat").readLines().firstOrNull() ?: "N/A"
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
}
