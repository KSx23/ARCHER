package com.example.archer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.archer.Model.DBHelper
import com.example.archer.Model.Shift
import com.example.archer.databinding.ActivityShiftsBinding

class ShiftsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShiftsBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var shiftsAdapter: ShiftsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout and set as content view
        binding = ActivityShiftsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize database helper
        dbHelper = DBHelper(this)

        // Get user ID from intent
        val userId = intent.getIntExtra("userId", -1)
        val currentUserId = userId

        // Check if user ID is present in the intent
        if (userId == -1) {
            // Handle error: user ID not found in the intent
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get all shifts from the database
        val shiftsList = dbHelper.getAllShifts()

        // Initialize the adapter for the RecyclerView
        shiftsAdapter = ShiftsAdapter(shiftsList, currentUserId,
            onEditClickListener = { shift ->
                // Handle edit click
                // Show edit dialog or navigate to edit shift activity
            },
            onDeleteClickListener = { shift ->
                // Handle delete click
                AlertDialog.Builder(this)
                    .setTitle("Delete Shift")
                    .setMessage("Are you sure you want to delete this shift?")
                    .setPositiveButton("Yes") { _, _ ->
                        dbHelper.deleteShift(shift.id)
                        shiftsList.remove(shift)
                        shiftsAdapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("No", null)
                    .show()
            },
            onBookClickListener = { shift ->
                // Handle book click
                val newUserId = if (shift.userId == -1) currentUserId else -1
                val newRoleId = if (shift.roleId == 2) 1 else 2

                // Update the shift with the new user ID and role ID
                if (dbHelper.updateShiftUserAndRole(shift.id, newUserId, newRoleId)) {
                    shift.userId = newUserId
                    shift.roleId = newRoleId
                    shiftsAdapter.notifyItemChanged(shiftsList.indexOf(shift))

                    if (shift.userId == -1 ) { // User is canceling a booked shift
                        sendNotification() // Send notification to other user who booked the shift
                    }
                }
            })

        // Set the adapter for the RecyclerView
        binding.shiftsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.shiftsRecyclerView.adapter = shiftsAdapter

        // Set listener for "Add Shift" button
        binding.btnAddShift.setOnClickListener {
            // Get shift details from user input
            val location = binding.etShiftLocation.text.toString().trim()
            val startTime = binding.etShiftStartTime.text.toString().trim().toDouble()
            val endTime = binding.etShiftEndTime.text.toString().trim().toDouble()
            val roleId = 1 // Default role for a new shift

            // Add the new shift to the database
            val result = dbHelper.addShift(userId, location, startTime, endTime, roleId)

            if (result > 0) {
                // Add the new shift to the RecyclerView
                val newShift = Shift(result.toInt(), userId, startTime, endTime, location, roleId)
                shiftsList.add(newShift)
                shiftsAdapter.notifyDataSetChanged()
            }
        }

        // Set listener for "Back" button
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * Creates a notification channel for sending shift booking notifications.
     * TO DO
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name) // The name of the notification channel
            val descriptionText = getString(R.string.channel_description) // The description of the notification channel
            val importance = NotificationManager.IMPORTANCE_DEFAULT // The level of importance for the notification channel
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Sends a notification to the user when a shift becomes available for booking.
     */
    private fun sendNotification() {
        val channelId = getString(R.string.channel_id) // The ID of the notification channel
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // The icon for the notification
            .setContentTitle("Important Message") // The title of the notification
            .setContentText("Shift is available for bookings!") // The text of the notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // The priority of the notification

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build()) // Sends the notification
    }

}
