package com.example.archer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.archer.Model.DBHelper
import com.example.archer.Model.Notification
import com.example.archer.Model.Role
import com.example.archer.Model.User

class RolesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var roleNameEditText: EditText
    private lateinit var roleDescriptionEditText: EditText
    private lateinit var addRoleButton: Button
    private lateinit var deleteRoleButton: Button
    private lateinit var rolesTextView: TextView
    private lateinit var userSpinner: Spinner
    private lateinit var roleSpinner: Spinner
    private lateinit var updateRoleButton: Button
    private lateinit var userListAdapter: ArrayAdapter<User>
    private lateinit var roleListAdapter: ArrayAdapter<Role>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roles)


        // Initialize the DBHelper instance using this Activity as the context
        dbHelper = DBHelper(this)

// Find views by their ID
        roleNameEditText = findViewById(R.id.roleNameEditText)
        roleDescriptionEditText = findViewById(R.id.roleDescriptionEditText)
        addRoleButton = findViewById(R.id.addRoleButton)
        rolesTextView = findViewById(R.id.rolesTextView)
        userSpinner = findViewById(R.id.userSpinner)
        roleSpinner = findViewById(R.id.roleSpinner)
        updateRoleButton = findViewById(R.id.updateRoleButton)
        deleteRoleButton = findViewById(R.id.deleteRoleButton)

// Set up the support action bar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

// Set up the "Add Role" button to add a role to the database when clicked
        addRoleButton.setOnClickListener {
            val roleName = roleNameEditText.text.toString()
            val roleDescription = roleDescriptionEditText.text.toString()

            if (roleName.isNotBlank() && roleDescription.isNotBlank()) {
                dbHelper.addRole(roleName, roleDescription)
                Toast.makeText(this, "Role added successfully", Toast.LENGTH_SHORT).show()
                displayRoles()
            } else {
                Toast.makeText(this, "Please enter both role name and description", Toast.LENGTH_SHORT).show()
            }
        }

// Set up the "Delete Role" button to delete a selected role from the database when clicked
        deleteRoleButton.setOnClickListener {
            val selectedRole = roleSpinner.selectedItem as Role

            dbHelper.deleteRole(selectedRole.id)

            Toast.makeText(this@RolesActivity, "Role deleted successfully", Toast.LENGTH_SHORT).show()

            // Refresh the role list
            roleListAdapter.clear()
            roleListAdapter.addAll(dbHelper.getAllRoles())
        }

// Set up the user spinner with a list of all users in the database
        val users = dbHelper.getAllUsers()
        userListAdapter = object : ArrayAdapter<User>(this, android.R.layout.simple_spinner_item, users) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                val user = getItem(position)
                if (user != null) {
                    view.text = "ID: ${user.id} | Name: ${user.firstName} ${user.lastName} | Role ID: ${user.roleId}"
                }
                return view
            }
        }
        userListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userSpinner.adapter = userListAdapter

// Set up the user spinner to listen for selection events
        userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Do something when a user is selected
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when no user is selected
            }
        }

        // Retrieve all roles from the database
        val roles = dbHelper.getAllRoles()

        // Create a new adapter to display the roles in the roleSpinner
        roleListAdapter = object : ArrayAdapter<Role>(this, android.R.layout.simple_spinner_item, roles) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                // Retrieve the view to be displayed
                val view = super.getView(position, convertView, parent) as TextView
                // Retrieve the role object for the current position
                val role = getItem(position)
                if (role != null) {
                    // Display the role ID and name in the view
                    view.text = "ID: ${role.id} | Name: ${role.name}"
                }
                return view
            }
        }

        // Set the layout for the dropdown view of the roleSpinner
        roleListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the adapter for the roleSpinner
        roleSpinner.adapter = roleListAdapter
        // Set the listener for the roleSpinner
        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Do something when a role is selected
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when no role is selected
            }
        }

        // Set the listener for the updateRoleButton
        updateRoleButton.setOnClickListener {
            // Retrieve the selected user and role objects from the spinners
            val selectedUser = userSpinner.selectedItem as User
            val selectedRole = roleSpinner.selectedItem as Role

            // Update the user's role in the database
            dbHelper.updateUserRole(selectedUser.id, selectedRole.id)

            // Display a success message
            Toast.makeText(this@RolesActivity, "User role updated successfully", Toast.LENGTH_SHORT).show()
        }


        displayRoles()
    }

    /**
     * Function to display all roles in the database in the rolesTextView
     */
    private fun displayRoles() {
        val roles = dbHelper.getAllRoles()  // get all roles from the database
        val rolesText = StringBuilder()  // initialize StringBuilder to store the roles text

        // Loop through all roles and append their ID, name and description to rolesText
        for (role in roles) {
            rolesText.append("ID: ${role.id}\n")
            rolesText.append("Name: ${role.name}\n")
            rolesText.append("Description: ${role.description}\n\n")
        }

        rolesTextView.text = rolesText.toString()  // set the text of rolesTextView to the rolesText StringBuilder
    }

    /**
     * Function to handle when an options menu item is selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {  // if the home button is pressed
            onBackPressed()  // go back to the previous activity
            return true
        }
        return super.onOptionsItemSelected(item)  // handle other menu item selections
    }


}

