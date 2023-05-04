package com.example.archer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    /**
     * This function is called when a broadcast Intent is received.
     * It sends a notification to the user's device with the provided title and body.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val body = intent.getStringExtra("body")
        NotificationsActivity.sendNotification(context, title ?: "", body ?: "")
    }

}
