package com.example.archer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when a new message is received from the server. Overrides the default implementation to extract the message
     * title and body from the RemoteMessage object and call sendNotification().
     *
     * @param remoteMessage The message received from the server.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extract the message title and body from the RemoteMessage object.
        val title = remoteMessage.notification?.title ?: "Default Title"
        val body = remoteMessage.notification?.body ?: "Default Body"

        // Call sendNotification() with the message title and body.
        sendNotification(title, body)
    }

    /**
     * Sends a notification to the user with the specified title and body.
     *
     * @param title The title of the notification.
     * @param body The body of the notification.
     */
    private fun sendNotification(title: String, body: String) {
        // Create an intent to launch the ShiftsActivity when the user taps the notification.
        val intent = Intent(this, ShiftsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // Define the ID of the notification channel.
        val channelId = "my_channel_id"

        // Build the notification with the specified title, body, and pending intent.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Get a reference to the system NotificationManager.
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel for Android Oreo and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Send the notification to the user.
        notificationManager.notify(0, notificationBuilder.build())
    }

}
