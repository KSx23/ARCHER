package com.example.archer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.NotificationCompat

class HomeActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. Initializes the UI components and creates the notification channel.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle
     * contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Create the notification channel for Android Oreo and higher.
        createNotificationChannel()

        // Retrieve the user ID passed from the previous activity.
        val userId = intent.getIntExtra("userId", -1)

        // Get references to the UI components.
        val userProfileButton = findViewById<Button>(R.id.userProfileButton)
        val shiftsButton = findViewById<Button>(R.id.shiftsButton)
        val performanceButton = findViewById<Button>(R.id.performanceButton)
        val notificationsButton = findViewById<Button>(R.id.notificationsButton)
        val roleButton = findViewById<Button>(R.id.roleButton)
        val timeButton = findViewById<Button>(R.id.timeButton)
        val sendNotificationButton = findViewById<Button>(R.id.sendNotificationButton)

        // Set click listeners for the UI components.
        userProfileButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        shiftsButton.setOnClickListener {
            val intent = Intent(this, ShiftsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        performanceButton.setOnClickListener {
            val intent = Intent(this, PerformanceActivity::class.java)
            startActivity(intent)
        }

        notificationsButton.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        roleButton.setOnClickListener {
            val intent = Intent(this, RolesActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        timeButton.setOnClickListener {
            val intent = Intent(this, TimeOffActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        sendNotificationButton.setOnClickListener {
            sendNotification()
        }
    }

    /**
     * Creates a notification channel for devices running Android Oreo and higher.
     * A notification channel is required for displaying notifications on Android Oreo and higher.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the name, description, and importance of the notification channel.
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // Create the notification channel.
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }

            // Register the notification channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Sends a notification with the specified title and body text to the user's device.
     * FOR TESTING PURPOSES ONLY
     */
    private fun sendNotification() {
        // Get the ID of the notification channel.
        val channelId = getString(R.string.channel_id)

        // Build the notification using a NotificationCompat.Builder object.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Set the icon for the notification
            .setContentTitle("My First Notification") // Set the title of the notification
            .setContentText("This is a sample notification") // Set the text of the notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the priority of the notification

        // Get an instance of the NotificationManager system service.
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Send the notification to the user's device.
        notificationManager.notify(0, notificationBuilder.build())
    }

}
