package com.ubuntucli

import com.ubuntucli.apkg.PackageManager
import org.junit.Test
import org.junit.Assert.*

class PackageManagerTest {
    @Test
    fun testInstallCmd() {
        val pm = PackageManager()
        assertEquals("apt install vim -y", pm.getAptInstallCmd("vim"))
    }
}
