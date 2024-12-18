package com.example.cwquizapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import androidx.core.app.NotificationCompat

internal class NotificationHelper(base: Context) : ContextWrapper(base) {
    private var notifManager: NotificationManager? = null

    private val manager: NotificationManager?
        get() {
            if (notifManager == null) {
                notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return notifManager
        }

    init {
        createChannels() // Ensure notification channels are created during app initialization
    }

    private fun createChannels() {
        // Creating a notification channel if not already created (for Android O and above)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                lightColor = android.graphics.Color.RED
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(true)
            }
            manager?.createNotificationChannel(notificationChannel)
        }
    }

    fun getNotification(title: String, content: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Set appropriate icon for your notification
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically dismiss notification when tapped
    }

    fun notifyUser(id: Int, builder: NotificationCompat.Builder) {
        manager?.notify(id, builder.build())
    }

    companion object {
        const val CHANNEL_ID = "channelID"
        const val CHANNEL_NAME = "channelName"
    }
}
