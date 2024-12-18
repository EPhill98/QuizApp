package com.example.cwquizapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Get the notification data from the Intent
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val title = intent.getStringExtra("NOTIFICATION_TITLE") ?: "Scheduled Notification"
        val message = intent.getStringExtra("NOTIFICATION_MESSAGE") ?: "This is your scheduled notification."

        // Log to check if the notification data is being received correctly
        Log.d("NotificationReceiver", "Received notification with ID: $notificationId, Title: $title, Message: $message")

        // Create and show the notification
        val notificationHelper = NotificationHelper(context)
        val notificationBuilder = notificationHelper.getNotification(title, message)
        notificationHelper.notifyUser(notificationId, notificationBuilder)
    }
}