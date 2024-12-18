package com.example.cwquizapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import java.util.*

class NotificationActivity : AppCompatActivity() {
    private lateinit var currentUserID: String

    private lateinit var timePicker: TimePicker
    private val notificationId = 101 // Notification ID for scheduling

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notificaton_settings)

        currentUserID = intent.getStringExtra("CURRENT_USER_ID").toString()

        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        // Initialize the TimePicker
        timePicker = findViewById(R.id.timePicker)

        // Button to set notification
        val setNotificationButton: Button = findViewById(R.id.setNotificationBtn)
        setNotificationButton.setOnClickListener {
            // Get selected time from TimePicker
            val hour =
                timePicker.hour
            val minute =
                timePicker.minute

            // Check and request permission if necessary
            if (isExactAlarmPermissionGranted()) {
                scheduleNotification(hour, minute)
            } else {
                requestExactAlarmPermission()
            }
        }
    }

    // Function to check if the app has exact alarm permission
    private fun isExactAlarmPermissionGranted(): Boolean {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    // Function to request the exact alarm permission from the user
    private fun requestExactAlarmPermission() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        startActivity(intent)
    }

    // Function to schedule the notification
    private fun scheduleNotification(hour: Int, minute: Int) {
        val currentTime = System.currentTimeMillis()

        // Set the time for the notification
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // If the selected time is in the past, schedule it for the next day
        if (calendar.timeInMillis < currentTime) {
            calendar.add(Calendar.DATE, 1)
        }

        // Schedule the notification
        val timeForNotification = calendar.timeInMillis
        scheduleNotificationForTime(timeForNotification)
    }

    // Use AlarmManager to schedule the notification
    private fun scheduleNotificationForTime(timeInMillis: Long) {
        // Define the calendar to get the time for the notification
        val calendar = Calendar.getInstance().apply {
            timeInMillis // Use the time passed to this function
        }

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("NOTIFICATION_ID", notificationId)
            putExtra("NOTIFICATION_TITLE", "Scheduled Notification")
            putExtra("NOTIFICATION_MESSAGE", "Time to review your quiz!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
        )

        // Reference the button for Snackbar
        val setNotificationButton: Button = findViewById(R.id.setNotificationBtn)

        // Display Snackbar with the scheduled time
        Snackbar.make(
            setNotificationButton,
            "Notification scheduled for ${calendar.time}", // Display the time the notification is scheduled for
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home_icon -> {
                val newIntent = Intent(this, HomeActivity::class.java)
                newIntent.putExtra("CURRENT_USER_ID", currentUserID)
                startActivity(newIntent)
                return true
            }
            R.id.settings_icon -> {
                val newIntent = Intent(this, SettingActivity::class.java)
                newIntent.putExtra("CURRENT_USER_ID", currentUserID)
                startActivity(newIntent)
                return true
            }
            R.id.stats -> {
                val newIntent = Intent(this, UserStatsActivity::class.java)
                newIntent.putExtra("CURRENT_USER_ID", currentUserID)
                startActivity(newIntent)
                return true
            }
            R.id.notification -> {
                val newIntent = Intent(this, NotificationActivity::class.java)
                newIntent.putExtra("CURRENT_USER_ID", currentUserID)
                startActivity(newIntent)
                return true
            }
            R.id.logOut -> {
                val newIntent = Intent(this, MainActivity::class.java)
                startActivity(newIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}