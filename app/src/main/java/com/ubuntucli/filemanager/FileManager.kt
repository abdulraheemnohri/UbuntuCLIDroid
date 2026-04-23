package com.ubuntucli.filemanager

import java.io.File

class FileManager {
    private var copyBuffer: File? = null

    fun listFiles(path: String): List<File> {
        return File(path).listFiles()?.toList()?.sortedBy { !it.isDirectory } ?: emptyList()
    }

    fun deleteFile(path: String): Boolean = File(path).deleteRecursively()

    fun moveFile(src: String, dest: String): Boolean = File(src).renameTo(File(dest))

    fun copyFile(src: String, dest: String): Boolean {
        return try {
            val source = File(src)
            val destination = File(dest)
            if (source.isDirectory) {
                source.copyRecursively(destination, overwrite = true)
            } else {
                source.copyTo(destination, overwrite = true)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun renameFile(path: String, newName: String): Boolean {
        val file = File(path)
        val newFile = File(file.parent, newName)
        return file.renameTo(newFile)
    }

    fun createDirectory(parentPath: String, name: String): Boolean {
        return File(parentPath, name).mkdirs()
    }
}
