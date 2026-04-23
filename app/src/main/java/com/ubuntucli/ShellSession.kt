package com.ubuntucli

import android.util.Log
import java.io.*
import java.lang.reflect.Field

class ShellSession {
    private var ptyFd: Int = -1
    private var processId: Int = -1
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null

    companion object {
        init {
            System.loadLibrary("ubuntucli")
        }
    }

    private external fun createPty(shellPath: String, args: Array<String>, envp: Array<String>, pProcessId: IntArray): Int
    private external fun setPtyWindowSize(fd: Int, rows: Int, cols: Int)
    private external fun terminateProcess(pid: Int)
    external fun waitFor(pid: Int): Int

    fun startSession(shellPath: String, args: Array<String>, envp: Array<String>) {
        val pids = IntArray(1)
        ptyFd = createPty(shellPath, args, envp, pids)
        processId = pids[0]

        if (ptyFd != -1) {
            val fd = FileDescriptor()
            try {
                val field: Field = FileDescriptor::class.java.getDeclaredField("descriptor")
                field.isAccessible = true
                field.setInt(fd, ptyFd)

                inputStream = FileInputStream(fd)
                outputStream = FileOutputStream(fd)

                Log.i("ShellSession", "PTY Created with FD: $ptyFd and PID: $processId")
            } catch (e: Exception) {
                Log.e("ShellSession", "Failed to create streams", e)
            }
        }
    }

    fun updateWindowSize(rows: Int, cols: Int) {
        if (ptyFd != -1) {
            setPtyWindowSize(ptyFd, rows, cols)
        }
    }

    fun stopSession() {
        if (processId != -1) {
            terminateProcess(processId)
        }
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (e: Exception) {}
    }
}
