package com.ubuntucli.terminal

import android.util.Log
import java.io.*
import java.lang.reflect.Field

class TerminalSession(val id: Int) {
    private var ptyFd: Int = -1
    private var processId: Int = -1
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    var onOutput: ((String) -> Unit)? = null

    companion object {
        init {
            System.loadLibrary("ubuntucli")
        }
    }

    private external fun createPty(shellPath: String, args: Array<String>, envp: Array<String>, pProcessId: IntArray): Int
    private external fun setPtyWindowSize(fd: Int, rows: Int, cols: Int)
    private external fun terminateProcess(pid: Int)
    external fun waitFor(pid: Int): Int

    fun start(shellPath: String, args: Array<String>, envp: Array<String>) {
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

                startReading()
            } catch (e: Exception) {
                Log.e("TerminalSession", "Stream creation failed", e)
            }
        }
    }

    private fun startReading() {
        Thread {
            val buffer = ByteArray(4096)
            try {
                while (true) {
                    val read = inputStream?.read(buffer) ?: -1
                    if (read == -1) break
                    if (read > 0) {
                        onOutput?.invoke(String(buffer, 0, read))
                    }
                }
            } catch (e: Exception) {
                Log.e("TerminalSession", "Read error", e)
            }
        }.start()
    }

    fun write(data: String) {
        try {
            outputStream?.write(data.toByteArray())
            outputStream?.flush()
        } catch (e: Exception) {}
    }

    fun updateSize(rows: Int, cols: Int) {
        if (ptyFd != -1) setPtyWindowSize(ptyFd, rows, cols)
    }

    fun stop() {
        if (processId != -1) terminateProcess(processId)
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (e: Exception) {}
    }
}
