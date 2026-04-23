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
            onProgress("Extracting RootFS DNA...")
            extractRootFS()

            onProgress("Merging System Configurations...")
            copyConfigRecursive("default-config", ubuntuDir)

            onProgress("Booting System...")
            initializedFile.createNewFile()
            onProgress("System Ready.")
        } catch (e: Exception) {
            val errorMsg = "Critical Error: ${e.javaClass.simpleName} - ${e.message}"
            Log.e("SystemInitializer", errorMsg, e)
            onProgress(errorMsg)
            // Rethrow to let the UI know it failed
            throw e
        }
    }

    private fun extractRootFS() {
        val assetManager = context.assets
        val assetName = "ubuntu-rootfs.tar.gz"

        // Use a more robust check for asset existence
        val assetExists = assetManager.list("")?.contains(assetName) ?: false
        if (!assetExists) {
            throw java.io.FileNotFoundException("Asset $assetName not found in APK")
        }

        val input: InputStream = assetManager.open(assetName)
        val outFile = File(context.filesDir, "ubuntu_tmp.tar.gz")

        input.use { ins ->
            FileOutputStream(outFile).use { outs ->
                ins.copyTo(outs)
            }
        }

        if (!ubuntuDir.exists()) ubuntuDir.mkdirs()

        val process = ProcessBuilder("tar", "-xzf", outFile.absolutePath, "-C", ubuntuDir.absolutePath)
            .redirectErrorStream(true)
            .start()

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            val errorOutput = process.inputStream.bufferedReader().readText()
            throw Exception("Tar extraction failed with code $exitCode: $errorOutput")
        }

        outFile.delete()
    }

    private fun copyConfigRecursive(assetPath: String, targetDir: File) {
        val assetManager = context.assets
        val assets = assetManager.list(assetPath) ?: return

        for (asset in assets) {
            val fullAssetPath = "$assetPath/$asset"
            val subAssets = assetManager.list(fullAssetPath)

            if (subAssets.isNullOrEmpty()) {
                // It's a file
                val targetFile = File(targetDir, asset)
                targetFile.parentFile?.mkdirs()
                assetManager.open(fullAssetPath).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                // It's a directory
                copyConfigRecursive(fullAssetPath, File(targetDir, asset))
            }
        }
    }
}
