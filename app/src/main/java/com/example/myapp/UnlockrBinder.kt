package com.example.myapp

import android.os.Binder

class UnlockrBinder : IUnlockr.Stub() {
    override fun getVersion() = "unlockr-0.1"
    override fun getServerUid() = android.os.Process.myUid()
    override fun isPrivileged() =
        android.os.Process.myUid() == 0 || android.os.Process.myUid() == 2000

    override fun exec(command: String): String {
        if (command.isBlank()) return ""
        return try {
            val p = Runtime.getRuntime().exec(arrayOf("/system/bin/sh", "-c", command))
            val out = p.inputStream.bufferedReader().readText()
            val err = p.errorStream.bufferedReader().readText()
            p.waitFor()
            buildString {
                append(out)
                if (err.isNotBlank()) {
                    if (isNotEmpty()) append('\n')
                    append(err)
                }
            }
        } catch (e: Exception) {
            "unlockr error: ${e.message}"
        }
    }

    override fun readFile(path: String): String =
        try { java.io.File(path).readText() }
        catch (e: Exception) { "unlockr error: ${e.message}" }

    override fun writeFile(path: String, value: String): Boolean =
        try {
            java.io.File(path).writeText(value)
            true
        } catch (_: Exception) {
            false
        }

    override fun hasAccess(packageName: String) = packageName.isNotBlank()
}
