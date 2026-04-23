package com.ubuntucli.core

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class SystemInitializer(private val context: Context) {
    private val ubuntuDir = File(context.filesDir, "ubuntu")
    private val initializedFile = File(context.filesDir, ".initialized")

    fun isInitialized(): Boolean = initializedFile.exists()

    fun initialize(onProgress: (String) -> Unit) {
        try {
            onProgress("Extracting RootFS...")
            extractRootFS()
            onProgress("Applying default-config...")
            copyConfig("default-config", ubuntuDir)
            onProgress("Running first-time setup...")
            runFirstBoot()
            onProgress("Finalizing...")
            initializedFile.createNewFile()
            onProgress("System Ready.")
        } catch (e: Exception) {
            Log.e("SystemInitializer", "Initialization failed", e)
            onProgress("Error: ${e.message}")
        }
    }

    private fun extractRootFS() {
        val assetManager = context.assets
        val input = assetManager.open("ubuntu-rootfs.tar.gz")
        val outFile = File(context.filesDir, "ubuntu.tar.gz")

        input.use { ins ->
            FileOutputStream(outFile).use { outs ->
                ins.copyTo(outs)
            }
        }

        if (!ubuntuDir.exists()) ubuntuDir.mkdirs()

        val process = ProcessBuilder("tar", "-xzf", outFile.absolutePath, "-C", ubuntuDir.absolutePath)
            .redirectErrorStream(true)
            .start()
        process.waitFor()
        outFile.delete()
    }

    private fun copyConfig(assetPath: String, targetDir: File) {
        val assets = context.assets.list(assetPath) ?: return
        for (asset in assets) {
            val fullAssetPath = "$assetPath/$asset"
            val subAssets = context.assets.list(fullAssetPath)
            if (subAssets.isNullOrEmpty()) {
                val targetFile = File(targetDir, asset)
                targetFile.parentFile?.mkdirs()
                context.assets.open(fullAssetPath).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                copyConfig(fullAssetPath, File(targetDir, asset))
            }
        }
    }

    private fun runFirstBoot() {
        val bootScript = File(ubuntuDir, "init/first_boot.sh")
        if (bootScript.exists()) {
            bootScript.setExecutable(true)
            // Note: In real production, this would be executed via proot
            // for now we just prepare it.
        }
    }
}
