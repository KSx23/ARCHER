package com.example.archer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.archer.Model.Notification
import com.example.archer.R

class NotificationsAdapter(private val context: Context, private val dataSource: List<Notification>) : BaseAdapter() {

    /**
     * Adapter for displaying a list of notifications in a ListView.
     *
     * @param context The context in which the adapter is used.
     * @param dataSource The list of notifications to display.
     */
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        // Return the number of items in the data source
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        // Return the item at the specified position in the data source
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        // Return a unique identifier for the item at the specified position in the data source
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflate the layout for the list item view if it hasn't been inflated already
        val view = convertView ?: inflater.inflate(R.layout.list_item_notification, parent, false)

        // Get references to the TextViews for the notification message and status
        val notificationMessageTextView = view.findViewById<TextView>(R.id.notificationMessageTextView)
        val notificationStatusTextView = view.findViewById<TextView>(R.id.notificationStatusTextView)

        // Get the notification object for the current item in the data source
        val notification = getItem(position) as Notification

        // Set the text of the notification message and status TextViews
        notificationMessageTextView.text = notification.message
        notificationStatusTextView.text = notification.status

        // Return the list item view
        return view
    }

}
