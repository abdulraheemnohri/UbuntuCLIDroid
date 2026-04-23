package com.ubuntucli.core

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SystemInitializer(private val context: Context) {
    private val ubuntuDir = File(context.filesDir, "ubuntu")
    private val initializedFile = File(context.filesDir, ".initialized")

    fun isInitialized(): Boolean = initializedFile.exists()

    fun initialize(onProgress: (String) -> Unit) {
        try {
            onProgress("Starting System Bootstrap...")

            if (!ubuntuDir.exists()) {
                ubuntuDir.mkdirs()
            }

            onProgress("Extracting Ubuntu Runtime...")
            extractAsset("ubuntu-rootfs.tar.gz", context.filesDir)

            val tarFile = File(context.filesDir, "ubuntu-rootfs.tar.gz")
            if (tarFile.exists()) {
                onProgress("Unpacking DNA Layers...")
                unpackTar(tarFile, ubuntuDir)
                tarFile.delete()
            }

            onProgress("Configuring Environment...")
            copyAssetsRecursive("default-config", ubuntuDir)

            // Create essential directories if they don't exist
            File(ubuntuDir, "dev").mkdirs()
            File(ubuntuDir, "proc").mkdirs()
            File(ubuntuDir, "sys").mkdirs()
            File(ubuntuDir, "tmp").mkdirs()
            File(ubuntuDir, "home/ubuntu").mkdirs()

            onProgress("Sealing System...")
            initializedFile.createNewFile()
            onProgress("UbuntuCLI Droid Ready.")
        } catch (e: Exception) {
            Log.e("SystemInitializer", "Bootstrap failed", e)
            throw e
        }
    }

    private fun extractAsset(assetName: String, targetDir: File) {
        val outFile = File(targetDir, assetName)
        context.assets.open(assetName).use { input ->
            FileOutputStream(outFile).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun unpackTar(tarFile: File, destDir: File) {
        val process = ProcessBuilder("tar", "-xzf", tarFile.absolutePath, "-C", destDir.absolutePath)
            .redirectErrorStream(true)
            .start()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            val error = process.inputStream.bufferedReader().readText()
            throw Exception("Tar extraction failed with code $exitCode: $error")
        }
    }

    private fun copyAssetsRecursive(path: String, targetDir: File) {
        val assets = context.assets.list(path) ?: return
        if (assets.isEmpty()) {
            // It might be a file
            val file = File(targetDir, path.substringAfterLast("/"))
            try {
                context.assets.open(path).use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                // Not a file or can't open
            }
            return
        }

        for (asset in assets) {
            val fullPath = if (path.isEmpty()) asset else "$path/$asset"
            val subAssets = context.assets.list(fullPath)
            val destFile = File(targetDir, asset)

            if (subAssets.isNullOrEmpty()) {
                destFile.parentFile?.mkdirs()
                try {
                    context.assets.open(fullPath).use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {}
            } else {
                destFile.mkdirs()
                copyAssetsRecursive(fullPath, destFile)
            }
        }
    }
}
