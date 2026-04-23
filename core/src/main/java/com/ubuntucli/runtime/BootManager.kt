package com.ubuntucli.runtime

import java.io.File

class BootManager(private val ubuntuDir: File) {
    fun generateBootScripts() {
        val bootDir = File(ubuntuDir, "root/boot")
        if (!bootDir.exists()) bootDir.mkdirs()

        File(bootDir, "boot.sh").writeText("""
            #!/bin/bash
            echo "[BOOT] Initializing Ubuntu Environment..."
            source /root/boot/env.sh
            source /root/boot/mounts.sh
            echo "[BOOT] System Ready."
        """.trimIndent())

        File(bootDir, "env.sh").writeText("""
            export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
            export TERM=xterm-256color
            export HOME=/root
        """.trimIndent())

        File(bootDir, "mounts.sh").writeText("""
            # Bind mounts handled by proot runner
        """.trimIndent())
    }
}
