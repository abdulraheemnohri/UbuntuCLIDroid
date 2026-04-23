package com.ubuntucli.filemanager

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileManager {
    fun listFiles(path: String): List<File> {
        val dir = File(path)
        return dir.listFiles()?.toList() ?: emptyList()
    }

    fun createDirectory(path: String): Boolean {
        return File(path).mkdirs()
    }

    fun delete(path: String): Boolean {
        return File(path).deleteRecursively()
    }

    fun zip(sourcePath: String, zipPath: String) {
        val sourceFile = File(sourcePath)
        ZipOutputStream(FileOutputStream(zipPath)).use { zos ->
            sourceFile.walkTopDown().forEach { file ->
                val entryName = file.absolutePath.removePrefix(sourceFile.absolutePath).removePrefix("/")
                if (entryName.isNotEmpty()) {
                    zos.putNextEntry(ZipEntry(entryName + (if (file.isDirectory) "/" else "")))
                    if (file.isFile) {
                        file.inputStream().use { it.copyTo(zos) }
                    }
                    zos.closeEntry()
                }
            }
        }
    }

    fun unzip(zipPath: String, destPath: String) {
        val destDir = File(destPath)
        if (!destDir.exists()) destDir.mkdirs()
        ZipInputStream(FileInputStream(zipPath)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val newFile = File(destDir, entry.name)
                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    FileOutputStream(newFile).use { fos -> zis.copyTo(fos) }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }
}
