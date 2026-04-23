package com.ubuntucli.core

import java.io.File
import java.security.MessageDigest

class IntegrityChecker {
    fun verify(file: File, expectedHash: String): Boolean {
        if (!file.exists()) return false
        val bytes = file.readBytes()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hash = digest.joinToString("") { "%02x".format(it) }
        return hash == expectedHash
    }
}
