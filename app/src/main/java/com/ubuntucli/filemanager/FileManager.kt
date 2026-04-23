package com.ubuntucli.filemanager

import java.io.File

class FileManager {
    fun listFiles(path: String): List<File> {
        return File(path).listFiles()?.toList()?.sortedBy { !it.isDirectory } ?: emptyList()
    }

    fun deleteFile(path: String): Boolean = File(path).deleteRecursively()
    fun renameFile(path: String, newName: String): Boolean {
        val file = File(path)
        return file.renameTo(File(file.parent, newName))
    }
}
