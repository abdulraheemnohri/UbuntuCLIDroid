package com.ubuntucli

import java.io.File

class FileSystemBridge {
    fun getSdcardPath(): String = "/sdcard"

    fun listFiles(path: String): List<String> {
        val dir = File(path)
        return dir.list()?.toList() ?: emptyList()
    }

    fun copyToUbuntu(source: File, destination: File) {
        source.copyTo(destination, overwrite = true)
    }

    fun getInternalStoragePath(): String {
        // Reference to the Ubuntu rootfs location
        return "/data/data/com.ubuntucli/files/ubuntu"
    }
}
