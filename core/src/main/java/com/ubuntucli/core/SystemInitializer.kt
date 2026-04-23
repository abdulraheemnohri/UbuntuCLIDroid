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
            onProgress("Checking DNA layer...")

            onProgress("Extracting RootFS...")
            extractRootFS()

            onProgress("Applying System Configs...")
            copyAssetsRecursive("default-config", ubuntuDir)

            onProgress("Initializing Boot Sequence...")
            initializedFile.createNewFile()
            onProgress("System Ready.")
        } catch (e: Exception) {
            val errorMsg = "System Error: ${e.javaClass.simpleName} - ${e.message}"
            Log.e("SystemInitializer", errorMsg, e)
            onProgress(errorMsg)
            throw e
        }
    }

    private fun extractRootFS() {
        val assetName = "ubuntu-rootfs.tar.gz"
        val outFile = File(context.filesDir, "ubuntu_tmp.tar.gz")

        try {
            context.assets.open(assetName).use { ins ->
                FileOutputStream(outFile).use { outs ->
                    ins.copyTo(outs)
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to open asset $assetName: ${e.message}")
        }

        if (!ubuntuDir.exists()) ubuntuDir.mkdirs()

        val process = ProcessBuilder("tar", "-xzf", outFile.absolutePath, "-C", ubuntuDir.absolutePath)
            .redirectErrorStream(true)
            .start()

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            val errorOutput = process.inputStream.bufferedReader().readText()
            throw Exception("Extraction failed ($exitCode): $errorOutput")
        }

        outFile.delete()
    }

    private fun copyAssetsRecursive(assetPath: String, targetDir: File) {
        val assets = context.assets.list(assetPath) ?: return
        if (assets.isEmpty()) {
            // Might be a file
            try {
                copyFileFromAsset(assetPath, targetDir)
            } catch (e: Exception) {}
            return
        }

        for (asset in assets) {
            val subAssetPath = "$assetPath/$asset"
            val subAssets = context.assets.list(subAssetPath)
            val subTargetFile = File(targetDir, asset)

            if (subAssets.isNullOrEmpty()) {
                subTargetFile.parentFile?.mkdirs()
                copyFileFromAsset(subAssetPath, subTargetFile)
            } else {
                copyAssetsRecursive(subAssetPath, subTargetFile)
            }
        }
    }

    private fun copyFileFromAsset(assetPath: String, targetFile: File) {
        try {
            context.assets.open(assetPath).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            // It was a directory, assets.list returned empty but open failed
        }
    }
}
