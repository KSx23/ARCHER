package com.example.archer

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.archer.Model.DBHelper
import com.example.archer.Model.TimeOffRequest
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeOffActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var addRequestButton: Button
    private lateinit var timeOffRequestsTextView: TextView
    private lateinit var timeOffRequestSpinner: Spinner
    private lateinit var confirmButton: Button
    private lateinit var refuseButton: Button
    private lateinit var timeOffRequestsAdapter: ArrayAdapter<TimeOffRequest>
    private var selectedTimeOffRequest: TimeOffRequest? = null
    private var UserId: Int = 0

    /**
     * This function initializes the TimeOffActivity by setting its content view and database helper.
     * It also finds and assigns references to the relevant views and UI components of the activity.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_off)

        dbHelper = DBHelper(this)

        startDateEditText = findViewById(R.id.startDateEditText)
        endDateEditText = findViewById(R.id.endDateEditText)
        addRequestButton = findViewById(R.id.addRequestButton)
        timeOffRequestsTextView = findViewById(R.id.timeOffRequestsTextView)
        timeOffRequestSpinner = findViewById(R.id.timeOffRequestSpinner)
        confirmButton = findViewById(R.id.confirmButton)
        refuseButton = findViewById(R.id.refuseButton)

        UserId = intent.getIntExtra("userId", 0)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        /**
         * This click listener function handles the event of clicking the addRequestButton to add a new time off request to the database.
         * It attempts to parse the start and end date EditText inputs into a date format, then adds the time off request to the database using the database helper.
         * If the input dates are in an invalid format, it displays an error message to the user.
         */
        addRequestButton.setOnClickListener {
            try {
                val startDate = SimpleDateFormat("dd/MM/yyyy").parse(startDateEditText.text.toString()).time // Parses the start date EditText input into a date format and converts it to a time in milliseconds.
                val endDate = SimpleDateFormat("dd/MM/yyyy").parse(endDateEditText.text.toString()).time // Parses the end date EditText input into a date format and converts it to a time in milliseconds.

                dbHelper.addTimeOffRequest(UserId, startDate, endDate) // Adds the new time off request to the database using the database helper.

                Toast.makeText(this, "Time off request added successfully", Toast.LENGTH_SHORT).show() // Displays a success message to the user.
            } catch (e: ParseException) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show() // Displays an error message to the user if the input dates are in an invalid format.
            }
        }

        /**
         * This adapter is responsible for displaying a list of TimeOffRequest objects in the timeOffRequestSpinner.
         * It overrides the getView function to format and display the time off requests in a readable format.
         */
        timeOffRequestsAdapter = object : ArrayAdapter<TimeOffRequest>(this, android.R.layout.simple_spinner_item, mutableListOf()) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView // Initializes the view and casts it to a TextView.
                val timeOffRequest = getItem(position) // Retrieves the TimeOffRequest object at the specified position in the adapter.
                if (timeOffRequest != null) {
                    val startDate = SimpleDateFormat("dd/MM/yyyy").format(Date(timeOffRequest.startDate)) // Formats the start date of the time off request.
                    val endDate = SimpleDateFormat("dd/MM/yyyy").format(Date(timeOffRequest.endDate)) // Formats the end date of the time off request.
                    view.text = "ID: ${timeOffRequest.id} | User ID: ${timeOffRequest.userId} | Start Date: $startDate | End Date: $endDate | Status: ${timeOffRequest.status}" // Sets the text of the view to a formatted string containing the time off request details.
                }
                return view // Returns the view.
            }
        }

        timeOffRequestsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Sets the drop down view resource for the time off requests adapter.
        timeOffRequestSpinner.adapter = timeOffRequestsAdapter // Sets the adapter for the time off request Spinner.
        timeOffRequestSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener { // Sets an item selected listener for the time off request Spinner.
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedTimeOffRequest = parent.getItemAtPosition(position) as TimeOffRequest // Retrieves the selected TimeOffRequest object and assigns it to the selectedTimeOffRequest variable.
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedTimeOffRequest = null // Sets the selectedTimeOffRequest variable to null if nothing is selected.
            }
        }

        confirmButton.setOnClickListener {
            selectedTimeOffRequest?.let { request -> // Executes the following code if a time off request is selected.
                AlertDialog.Builder(this) // Initializes a new AlertDialog builder.
                    .setTitle("Confirm Time Off Request") // Sets the title of the dialog.
                    .setMessage("Are you sure you want to confirm this time off request?") // Sets the message of the dialog.
                    .setPositiveButton("Confirm") { _, _ -> // Sets a positive button for the dialog that confirms the time off request.
                        dbHelper.updateTimeOffRequestStatus(request.id, "confirmed") // Updates the status of the time off request in the database to "confirmed" using the database helper.
                        Toast.makeText(this, "Time off request confirmed", Toast.LENGTH_SHORT).show() // Displays a success message to the user.
                        displayTimeOffRequests() // Displays the updated list of time off requests.
                    }
                    .setNegativeButton("Cancel", null) // Sets a negative button for the dialog that does nothing.
                    .show() // Shows the dialog.
            } ?: Toast.makeText(this, "No time off request selected", Toast.LENGTH_SHORT).show() // Displays an error message to the user if no time off request is selected.
        }

        refuseButton.setOnClickListener {
            selectedTimeOffRequest?.let { request -> // Executes the following code if a time off request is selected.
                showConfirmationDialog(request, "Refuse") // Shows a confirmation dialog for refusing the time off request.
            } ?: Toast.makeText(this, "No time off request selected", Toast.LENGTH_SHORT).show() // Displays an error message to the user if no time off request is selected.
        }

        displayTimeOffRequests()
    }

    /**
     * This function retrieves all time off requests for the current user and displays them in a RecyclerView.
     * The time off requests are obtained from the database and bound to the view using the timeOffRequestsAdapter.
     */
    private fun displayTimeOffRequests() {
        val timeOffRequests = dbHelper.getTimeOffRequests(UserId) // Retrieves all time off requests for the current user from the database.
        timeOffRequestsAdapter.clear() // Clears the current list of time off requests from the adapter.
        timeOffRequestsAdapter.addAll(timeOffRequests) // Adds the retrieved time off requests to the adapter.
        timeOffRequestsAdapter.notifyDataSetChanged() // Notifies the adapter that the data set has changed and the view should be updated.
    }

    /**
     * This function displays a confirmation dialog for the specified time off request action.
     * @param request the TimeOffRequest object to be confirmed or refused.
     * @param action the action to be taken on the time off request ("Confirm" or "Refuse").
     */
    private fun showConfirmationDialog(request: TimeOffRequest, action: String) {
        val dialog = Dialog(this) // Initializes a new dialog.
        dialog.setContentView(R.layout.dialog_confirmation) // Sets the layout of the dialog.

        val titleTextView = dialog.findViewById<TextView>(R.id.dialogTitleTextView) // Finds the dialog title TextView.
        val messageTextView = dialog.findViewById<TextView>(R.id.dialogMessageTextView) // Finds the dialog message TextView.
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton) // Finds the dialog confirm Button.
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton) // Finds the dialog cancel Button.

        titleTextView.text = "$action Time Off Request" // Sets the title of the dialog to the specified action.
        messageTextView.text = "Are you sure you want to $action this time off request?" // Sets the message of the dialog to a confirmation message for the specified action.

        confirmButton.setOnClickListener {
            if (action == "Confirm") {
                dbHelper.updateTimeOffRequestStatus(request.id, "confirmed") // Updates the status of the specified time off request to "confirmed" in the database.
                Toast.makeText(this, "Time off request confirmed", Toast.LENGTH_SHORT).show() // Displays a confirmation message to the user.
            } else {
                dbHelper.updateTimeOffRequestStatus(request.id, "refused") // Updates the status of the specified time off request to "refused" in the database.
                Toast.makeText(this, "Time off request refused", Toast.LENGTH_SHORT).show() // Displays a refusal message to the user.
            }

            displayTimeOffRequests() // Displays the updated list of time off requests.
            dialog.dismiss() // Dismisses the confirmation dialog.
        }

        cancelButton.setOnClickListener {
            dialog.dismiss() // Dismisses the confirmation dialog.
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ) // Sets the layout parameters of the dialog.
        dialog.show() // Displays the confirmation dialog.
    }

    /**
     * This function handles options menu item clicks.
     * If the home menu item is clicked, the activity is finished.
     * @param item the selected menu item.
     * @return true if the menu item click is handled, false otherwise.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { // Checks if the home menu item is clicked.
            finish() // Finishes the activity.
            return true // Returns true to indicate that the menu item click is handled.
        }
        return super.onOptionsItemSelected(item) // Delegates to the superclass method if the menu item click is not handled.
    }

}
