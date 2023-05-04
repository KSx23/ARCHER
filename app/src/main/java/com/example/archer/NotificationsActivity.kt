package com.example.archer

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.archer.Model.DBHelper
import com.example.archer.Model.Notification
import com.example.archer.R
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*


class NotificationsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var notificationsListView: ListView
    private lateinit var addNotificationButton: Button
    private lateinit var selectedNotification: Notification

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Initialize DBHelper
        dbHelper = DBHelper(this)

        // Get references to the views
        notificationsListView = findViewById(R.id.notificationsListView)
        addNotificationButton = findViewById(R.id.addNotificationButton)

        // Load notifications into the list view
        loadNotifications()

        // Handle the click event for list items
        notificationsListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedNotification = parent.getItemAtPosition(position) as Notification
                // Ask the user if they want to send the notification
                val builder = AlertDialog.Builder(this@NotificationsActivity)
                builder.setTitle("Send Notification")
                builder.setMessage("Do you want to send this notification?")
                builder.setPositiveButton("Yes") { _, _ ->
                    // Send the notification
                    sendNotification(selectedNotification.message, selectedNotification.status)
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }

        // Handle the click event for the "Add Notification" button
        addNotificationButton.setOnClickListener {
            showAddNotificationDialog()
        }

        // Handle the long click event for list items
        notificationsListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, _, position, _ ->
            selectedNotification = parent.getItemAtPosition(position) as Notification
            val options = arrayOf("Edit", "Delete")
            val builder = AlertDialog.Builder(this@NotificationsActivity)
            builder.setTitle("Choose an option")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> showEditNotificationDialog(selectedNotification)
                    1 -> deleteNotification(selectedNotification)
                }
            }
            builder.create().show()
            true
        }
    }

    /**
     * Load notifications into the list view.
     */
    private fun loadNotifications() {
        val notifications = dbHelper.getAllNotifications()
        val adapter = NotificationsAdapter(this, notifications)
        notificationsListView.adapter = adapter
    }

    /**
     * Displays a dialog for adding a new notification.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showAddNotificationDialog() {
        // Create an AlertDialog builder
        val builder = AlertDialog.Builder(this)

        // Get an instance of the LayoutInflater service
        val inflater = LayoutInflater.from(this)

        // Inflate the dialog's layout
        val dialogView = inflater.inflate(R.layout.add_notification_dialog, null)

        // Set the builder's view to the inflated layout
        builder.setView(dialogView)

        // Get references to the EditText views and the DatePicker and TimePicker widgets
        val messageEditText = dialogView.findViewById<EditText>(R.id.messageEditText)
        val statusEditText = dialogView.findViewById<EditText>(R.id.statusEditText)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)

        // Set the builder's title and positive button text
        builder.setTitle("Add Notification")
        builder.setPositiveButton("Add") { dialog, _ ->
            // Retrieve the values entered by the user
            val message = messageEditText.text.toString()
            val status = statusEditText.text.toString()

            // Create a Calendar instance and set it to the selected date and time
            val calendar = Calendar.getInstance()
            calendar.set(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timePicker.hour,
                timePicker.minute
            )

            // Add the notification to the database
            dbHelper.addNotification(message, status)

            // Reload the notifications and update the UI
            loadNotifications()

            // Schedule an alarm to send the notification at the selected date and time
            scheduleNotificationAlarm(message, status)

            // Dismiss the dialog
            dialog.dismiss()
        }

        // Set the builder's negative button text and functionality
        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Dismiss the dialog
            dialog.dismiss()
        }

        // Create an AlertDialog instance and show it
        val alertDialog = builder.create()
        alertDialog.show()
    }

    /**
     * Displays a dialog for editing an existing notification.
     *
     * @param notification The notification to edit.
     */
    private fun showEditNotificationDialog(notification: Notification) {
        // Create an AlertDialog builder
        val builder = AlertDialog.Builder(this)

        // Get an instance of the LayoutInflater service
        val inflater = LayoutInflater.from(this)

        // Inflate the dialog's layout
        val dialogView = inflater.inflate(R.layout.add_notification_dialog, null)

        // Set the builder's view to the inflated layout
        builder.setView(dialogView)

        // Get references to the EditText views and set their initial values to the notification's values
        val messageEditText = dialogView.findViewById<EditText>(R.id.messageEditText)
        val statusEditText = dialogView.findViewById<EditText>(R.id.statusEditText)
        messageEditText.setText(notification.message)
        statusEditText.setText(notification.status)

        // Set the builder's title and positive button text
        builder.setTitle("Edit Notification")
        builder.setPositiveButton("Update") { dialog, _ ->
            // Retrieve the updated values
            val message = messageEditText.text.toString()
            val status = statusEditText.text.toString()

            // Update the notification in the database
            dbHelper.updateNotification(notification.id, message, status)

            // Reload the notifications and update the UI
            loadNotifications()

            // Dismiss the dialog
            dialog.dismiss()
        }

        // Set the builder's negative button text and functionality
        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Dismiss the dialog
            dialog.dismiss()
        }

        // Create an AlertDialog instance and show it
        val alertDialog = builder.create()
        alertDialog.show()
    }

    /**
     * Deletes a notification from the database and updates the UI.
     *
     * @param notification The notification to delete.
     */
    private fun deleteNotification(notification: Notification) {
        // Delete the notification from the database
        dbHelper.deleteNotification(notification.id)

        // Reload the notifications and update the UI
        loadNotifications()
    }

    /**
     * Sends a notification to the user's device.
     *
     * @param context The context from which the notification is being sent.
     * @param title The title of the notification.
     * @param body The body of the notification.
     */
    companion object {
        fun sendNotification(context: Context, title: String, body: String) {
            // Create an intent to open the app's main activity when the notification is clicked
            val intent = Intent(context, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            // Create a pending intent that will open the app's main activity when the notification is clicked
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            // Define the notification's channel ID
            val channelId = "my_channel_id"

            // Build the notification using a NotificationCompat.Builder object
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            // Get an instance of the NotificationManager service
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // If the device is running Android 8.0 or higher, create a notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            // Send the notification
            notificationManager.notify(0, notificationBuilder.build())
        }
    }

    /**
     * Sends a local notification to the user's device and a remote notification using Firebase Cloud Messaging.
     *
     * @param title The title of the notification.
     * @param body The body of the notification.
     */
    private fun sendNotification(title: String, body: String) {
        // Create an intent to open the app's main activity when the notification is clicked
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // Create a pending intent that will open the app's main activity when the notification is clicked
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // Define the notification's channel ID
        val channelId = "my_channel_id"

        // Build the local notification using a NotificationCompat.Builder object
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Get an instance of the NotificationManager service
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // If the device is running Android 8.0 or higher, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Send the local notification
        notificationManager.notify(0, notificationBuilder.build())

        // Subscribe to the Firebase Cloud Messaging topic for notifications
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/myTopic")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // Display an error message if the subscription was not successful
                    Toast.makeText(this, "Failed to subscribe to topic", Toast.LENGTH_SHORT).show()
                }

                // Format the message and status into a single string for the push notification body
                val notificationBody = "Message: $title\nStatus: $body"

                // Use Firebase Cloud Messaging REST API to send the notification
                val json = """
                {
                  "to": "/topics/myTopic",
                  "notification": {
                    "title": "New Notification",
                    "body": "$notificationBody"
                  }
                }
            """.trimIndent()

                val client = OkHttpClient()

                val requestBody =
                    json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(requestBody)
                    .build()

                // Send the HTTP request asynchronously
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Display an error message if the notification was not sent
                        runOnUiThread {
                            Toast.makeText(
                                this@NotificationsActivity,
                                "Failed to send notification",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        // Display a success or error message depending on the response
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@NotificationsActivity,
                                    "Notification sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@NotificationsActivity,
                                    "Failed to send notification",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            }
    }

    /**
     * Schedules a local notification to be sent at a specific time using an alarm.
     *
     * @param title The title of the notification.
     * @param body The body of the notification.
     */
    private fun scheduleNotificationAlarm(title: String, body: String) {
        // Get an instance of the AlarmManager service
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create an intent to broadcast the alarm
        val alarmIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
        }

        // Set the time at which the alarm should be triggered (e.g., 1 minute from now)
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
        }

        // Create a pending intent to be triggered when the alarm goes off
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Set the alarm to trigger at the specified time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }


}

