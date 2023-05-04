package com.example.archer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.archer.Model.DBHelper
import com.example.archer.Model.User
import com.example.archer.Model.*

class LoginActivity : AppCompatActivity() {

    // A reference to the DBHelper object that manages the SQLite database.
    private lateinit var dbHelper: DBHelper

    /**
     * Called when the activity is starting. Initializes the UI components and sets click listeners for the login and back
     * buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle
     * contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Instantiate the DBHelper object.
        dbHelper = DBHelper(this)

        // Get references to the UI components.
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val backButton = findViewById<Button>(R.id.backButton)

        // Set a click listener for the login button.
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                // Display an error message if the user did not fill in all fields.
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Attempt to retrieve the user's account from the database.
                val user = dbHelper.getUserByUsernameAndPassword(username, password)

                if (user != null) {
                    // If the user's account is found in the database, start the HomeActivity and pass the user's ID.
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("userId", user.id)
                    startActivity(intent)
                    finish()
                } else {
                    // If the user's account is not found in the database, display an error message.
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set a click listener for the back button.
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
