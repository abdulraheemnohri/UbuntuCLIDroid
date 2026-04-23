package com.ubuntucli.filemanager

import java.io.File

class FileManager {
    fun listFiles(path: String): List<File> {
        return File(path).listFiles()?.toList() ?: emptyList()
    }

    fun deleteFile(path: String) = File(path).delete()

    fun moveFile(src: String, dest: String) = File(src).renameTo(File(dest))
}
