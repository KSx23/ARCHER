package com.example.archer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.archer.Model.DBHelper
import com.example.archer.Model.User
import com.example.archer.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater) // Inflates the layout for the user profile activity.
        val view = binding.root
        setContentView(view)

        dbHelper = DBHelper(this) // Initializes the database helper.

        val userId = intent.getIntExtra("userId", -1) // Retrieves the user ID from the intent.
        if (userId == -1) {
            // Handle error: user ID not found in the intent
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show() // Displays an error message to the user.
            finish() // Finishes the activity.
            return
        }

        user = dbHelper.getUserById(userId)!! // Retrieves the User object for the specified user ID from the database using the database helper.
        if (user != null) {
            displayUserData(user) // Displays the user data in the UI.
        } else {
            // Handle error: user not found in the database
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show() // Displays an error message to the user.
            finish() // Finishes the activity.
        }

        binding.btnBack.setOnClickListener {
            finish() // Closes the activity when the user clicks the back button.
        }

        binding.btnEdit.setOnClickListener {
            showEditProfileDialog() // Shows a dialog for editing the user profile when the user clicks the edit button.
        }
    }

    /**
     * This function displays the user data in the UI.
     */
    private fun displayUserData(user: User) {
        binding.tvUsername.text = user.username // Sets the username TextView to the user's username.
        binding.tvEmail.text = user.email // Sets the email TextView to the user's email.
        binding.tvFirstName.text = user.firstName // Sets the first name TextView to the user's first name.
        binding.tvLastName.text = user.lastName // Sets the last name TextView to the user's last name.
        binding.tvPhone.text = user.phone // Sets the phone TextView to the user's phone number.
    }

    /**
     * This function shows a dialog for editing the user profile.
     */
    private fun showEditProfileDialog() {
        val builder = AlertDialog.Builder(this) // Creates an AlertDialog builder.
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.edit_profile_dialog, null) // Inflates the layout for the edit profile dialog.

        builder.setView(dialogView)

        // Retrieves the EditText views for each field from the dialog layout.
        val firstNameEditText = dialogView.findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = dialogView.findViewById<EditText>(R.id.lastNameEditText)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.phoneEditText)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
        val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)

        // Sets the text of each EditText view to the user's current information.
        firstNameEditText.setText(user.firstName)
        lastNameEditText.setText(user.lastName)
        phoneEditText.setText(user.phone)
        passwordEditText.setText(user.password)
        emailEditText.setText(user.email)

        builder.setTitle("Edit Profile")
        builder.setPositiveButton("Save") { _, _ ->
            // Retrieves the updated information from the EditText views.
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val password = passwordEditText.text.toString()
            val email = emailEditText.text.toString()

            if (firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()) { // Ensures that all required fields have been filled.
                val updatedUser = User(user.id, user.username, password, email, firstName, lastName, phone, user.roleId) // Creates a new User object with the updated information.
                dbHelper.updateUser(updatedUser) // Updates the user information in the database using the database helper.
                displayUserData(updatedUser) // Displays the updated user information in the UI.
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show() // Displays a success message to the user.
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show() // Displays an error message to the user.
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss() // Dismisses the dialog when the user clicks the cancel button.
        }

        val alertDialog = builder.create()
        alertDialog.show() // Shows the dialog.
    }

}
