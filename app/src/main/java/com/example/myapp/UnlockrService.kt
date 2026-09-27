package com.example.myapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

class UnlockrService : Service() {
    companion object {
        private const val CHANNEL_ID = "unlockr_service"
        private const val NOTIFICATION_ID = 1001
    }

    private val binder = UnlockrBinder()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) =
        START_STICKY

    override fun onBind(intent: Intent?): IBinder = binder

    private fun buildNotification(): Notification =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("unlockr")
                .setContentText("unlockr service is running")
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setOngoing(true)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("unlockr")
                .setContentText("unlockr service is running")
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setOngoing(true)
                .build()
        }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        "Unlockr service",
                        NotificationManager.IMPORTANCE_LOW
                    )
                )
        }
    }
}
