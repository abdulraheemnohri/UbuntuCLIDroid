package com.ubuntucli

import org.junit.Test
import org.junit.Assert.*

class PackageManagerTest {
    @Test
    fun testInstallPackage() {
        val pm = PackageManager()
        assertEquals("apt install vim", pm.installPackage("vim"))
    }

    @Test
    fun testRemovePackage() {
        val pm = PackageManager()
        assertEquals("apt remove vim", pm.removePackage("vim"))
    }
}
