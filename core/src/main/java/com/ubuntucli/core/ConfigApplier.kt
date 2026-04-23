package com.ubuntucli.core

import java.io.File

class ConfigApplier {
    fun apply(configDir: File, targetDir: File) {
        if (!configDir.exists()) return
        configDir.copyRecursively(targetDir, overwrite = true)
    }
}
