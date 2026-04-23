package com.ubuntucli.monitor

import java.io.File

data class ProcessInfo(val pid: String, val name: String, val state: String)

class SystemMonitor {
    fun getCpuUsage(): String {
        return try {
            val lines = File("/proc/stat").readLines()
            if (lines.isEmpty()) return "N/A"
            val parts = lines[0].split(Regex("\\s+")).filter { it.isNotBlank() }
            if (parts.size < 5) return "N/A"
            val idle = parts[4].toLong()
            val total = parts.drop(1).sumOf { it.toLong() }
            "Total: $total, Idle: $idle"
        } catch (e: Exception) { "Error" }
    }

    fun getMemoryUsage(): Pair<Long, Long> {
        return try {
            val meminfo = File("/proc/meminfo").readLines()
            val total = meminfo.find { it.startsWith("MemTotal") }?.filter { it.isDigit() }?.toLong() ?: 0L
            val available = meminfo.find { it.startsWith("MemAvailable") }?.filter { it.isDigit() }?.toLong() ?: 0L
            total to available
        } catch (e: Exception) { 0L to 0L }
    }

    fun getRunningProcesses(): List<ProcessInfo> {
        val procs = mutableListOf<ProcessInfo>()
        val procDir = File("/proc")
        procDir.listFiles()?.filter { it.isDirectory && it.name.all { c -> c.isDigit() } }?.forEach { f ->
            try {
                val comm = File(f, "comm").readText().trim()
                val stat = File(f, "stat").readText().split(" ")
                val state = stat.getOrNull(2) ?: "?"
                procs.add(ProcessInfo(f.name, comm, state))
            } catch (e: Exception) {}
        }
        return procs.take(50)
    }
}
