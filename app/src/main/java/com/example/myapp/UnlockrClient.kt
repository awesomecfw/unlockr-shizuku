package com.example.myapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class UnlockrClient(private val context: Context) {
    private var service: IUnlockr? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = IUnlockr.Stub.asInterface(binder)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }
    }

    fun connect(): Boolean =
        context.bindService(
            Intent(context, UnlockrService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

    fun disconnect() {
        try { context.unbindService(connection) } catch (_: Exception) {}
        service = null
    }

    fun isConnected() = service != null
    fun version() = try { service?.version } catch (_: Exception) { null }
    fun exec(command: String) = service?.exec(command) ?: "unlockr unavailable"
    fun readFile(path: String) = service?.readFile(path) ?: "unlockr unavailable"
    fun writeFile(path: String, value: String) =
        service?.writeFile(path, value) ?: false
}
