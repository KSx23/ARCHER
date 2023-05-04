package com.example.archer.Model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ArcherDataBase.db"
        private const val DATABASE_VERSION = 1
    }
    override fun onCreate(db: SQLiteDatabase) {
        // Create users table
        val createUsersTable = """
            CREATE TABLE IF NOT EXISTS "users" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "username" TEXT UNIQUE,
                "password" TEXT,
                "email" TEXT UNIQUE,
                "first_name" TEXT,
                "last_name" TEXT,
                "phone" TEXT,
                "role_id" INTEGER,
                FOREIGN KEY("role_id") REFERENCES "roles"("id")
            )
        """.trimIndent()
        db.execSQL(createUsersTable)
        // Create roles table
        val createRolesTable = """
            CREATE TABLE IF NOT EXISTS "roles" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "name" TEXT,
                "description" TEXT
            )
        """.trimIndent()
        db.execSQL(createRolesTable)
        // Create shifts table
        val createShiftsTable = """
            CREATE TABLE IF NOT EXISTS "shifts" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
              "user_id" INTEGER,
             "start_time" REAL,
             "end_time" REAL,
                "location" TEXT,
              "role_id" INTEGER,
                 FOREIGN KEY("user_id") REFERENCES "users"("id"),
                FOREIGN KEY("role_id") REFERENCES "roles"("id")
           )
        """.trimIndent()
        db.execSQL(createShiftsTable)
        // Create time_off_requests table
        val createTimeOffRequestsTable = """
            CREATE TABLE IF NOT EXISTS "time_off_request" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "user_id" INTEGER,
                "start_date" REAL,
                "end_date" REAL,
                "status" TEXT,
                FOREIGN KEY("user_id") REFERENCES "users"("id")
            )
        """.trimIndent()
        db.execSQL(createTimeOffRequestsTable)
        // Create performance_metrics table
        val createPerformanceMetricsTable = """
            CREATE TABLE IF NOT EXISTS "performance_metrics" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "user_id" INTEGER,
                "metric_name" TEXT,
                "metric_value" REAL,
                "goal_value" REAL,
                FOREIGN KEY("user_id") REFERENCES "users"("id")
            )
        """.trimIndent()
        db.execSQL(createPerformanceMetricsTable)
        // Create notifications table
        val createNotificationsTable = """
            CREATE TABLE IF NOT EXISTS "notifications" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "user_id" INTEGER,
                "message" TEXT,
                "status" TEXT,
                FOREIGN KEY("user_id") REFERENCES "users"("id")
            )
        """.trimIndent()
        db.execSQL(createNotificationsTable)
    }
    /**
     * This function is called when the database needs to be upgraded to a newer version.
     * The parameters oldVersion and newVersion indicate the previous and new version numbers of the database, respectively.
     *
     * @param db The SQLiteDatabase object representing the database being upgraded.
     * @param oldVersion The previous version number of the database.
     * @param newVersion The new version number of the database.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO: Handle database upgrade, if needed.
    }

    /**
     * Adds a new user to the database and returns the ID of the newly inserted row.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param email The email address of the new user.
     * @param firstName The first name of the new user.
     * @param lastName The last name of the new user.
     * @param phone The phone number of the new user.
     * @param roleId The ID of the role of the new user.
     *
     * @return The ID of the newly inserted row.
     */
    fun addUser(username: String, password: String, email: String,
                firstName: String, lastName: String, phone: String, roleId: Int): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put("username", username)
        values.put("password", password)
        values.put("email", email)
        values.put("first_name", firstName)
        values.put("last_name", lastName)
        values.put("phone", phone)
        values.put("role_id", roleId)
        return db.insert("users", null, values)
    }

    /**
     * Adds a new shift to the database and returns the ID of the newly inserted row.
     *
     * @param userId The ID of the user who worked the shift.
     * @param location The location where the shift was worked.
     * @param startTime The start time of the shift in decimal hours.
     * @param endTime The end time of the shift in decimal hours.
     * @param roleId The ID of the role associated with the shift.
     *
     * @return The ID of the newly inserted row.
     */
    fun addShift(userId: Int, location: String, startTime: Double, endTime: Double, roleId: Int): Long {
        val values = ContentValues()
        values.put("user_id", -1) // hardcoded data
        values.put("location", location)
        values.put("start_time", startTime)
        values.put("end_time", endTime)
        values.put("role_id", roleId)
        return writableDatabase.insert("shifts", null, values)
    }

    /**
     * Deletes a shift from the database based on its ID and returns the number of affected rows.
     *
     * @param id The ID of the shift to be deleted.
     *
     * @return The number of affected rows.
     */
    fun deleteShift(id: Int): Int {
        val db = writableDatabase
        return db.delete("shifts", "id = ?", arrayOf(id.toString()))
    }

    /**
     * Adds a new performance metric to the database and returns the ID of the newly inserted row.
     *
     * @param userId The ID of the user for whom the metric is being added.
     * @param metricName The name of the performance metric.
     * @param metricValue The value of the performance metric.
     * @param goalValue The goal value of the performance metric.
     *
     * @return The ID of the newly inserted row.
     */
    fun addPerformanceMetric(userId: Int, metricName: String, metricValue: Double, goalValue: Double): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put("user_id", userId)
        values.put("metric_name", metricName)
        values.put("metric_value", metricValue)
        values.put("goal_value", goalValue)

        return db.insert("performance_metrics", null, values)
    }

    /**
     * Retrieves all performance metrics from the database and returns them as a mutable list of PerformanceMetric objects.
     *
     * @return The list of PerformanceMetric objects.
     */
    @SuppressLint("Range")
    fun getAllPerformanceMetrics(): MutableList<PerformanceMetric> {
        val performanceData = mutableListOf<PerformanceMetric>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM performance_metrics", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val userId = cursor.getInt(cursor.getColumnIndex("user_id"))
                val metricName = cursor.getString(cursor.getColumnIndex("metric_name"))
                val metricValue = cursor.getDouble(cursor.getColumnIndex("metric_value"))
                val goalValue = cursor.getDouble(cursor.getColumnIndex("goal_value"))

                val performanceMetric = PerformanceMetric(id, userId, metricName, metricValue, goalValue)
                performanceData.add(performanceMetric)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return performanceData
    }

    /**
     * Retrieves all notifications from the database and returns them as a list of Notification objects.
     *
     * @return The list of Notification objects.
     */
    @SuppressLint("Range")
    fun getAllNotifications(): List<Notification> {
        val notifications = ArrayList<Notification>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM notifications", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val userId = cursor.getInt(cursor.getColumnIndex("user_id"))
                val message = cursor.getString(cursor.getColumnIndex("message"))
                val status = cursor.getString(cursor.getColumnIndex("status"))

                val notification = Notification(id, userId, message, status)
                notifications.add(notification)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notifications
    }

    /**
     * Retrieves a user from the database by their username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     *
     * @return The User object if found, otherwise null.
     */
    @SuppressLint("Range")
    fun getUserByUsernameAndPassword(username: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            "users",
            arrayOf("id", "username", "password", "email", "first_name", "last_name", "phone", "role_id"),
            "username = ? AND password = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                username = cursor.getString(cursor.getColumnIndex("username")),
                password = cursor.getString(cursor.getColumnIndex("password")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                firstName = cursor.getString(cursor.getColumnIndex("first_name")),
                lastName = cursor.getString(cursor.getColumnIndex("last_name")),
                phone = cursor.getString(cursor.getColumnIndex("phone")),
                roleId = cursor.getInt(cursor.getColumnIndex("role_id"))
            )
        }

        cursor.close()
        return user
    }

    /**
     * Adds a new notification to the database and returns the ID of the newly inserted row.
     *
     * @param message The message of the notification.
     * @param status The status of the notification.
     *
     * @return The ID of the newly inserted row.
     */
    fun addNotification(message: String, status: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put("message", message)
        values.put("status", status)

        return db.insert("notifications", null, values)
    }

    /**
     * Retrieves a user from the database by their ID.
     *
     * @param id The ID of the user.
     *
     * @return The User object if found, otherwise null.
     */
    @SuppressLint("Range")
    fun getUserById(id: Int): User? {
        val db = readableDatabase
        val cursor = db.query(
            "users",
            arrayOf("id", "username", "password", "email", "first_name", "last_name", "phone", "role_id"),
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                username = cursor.getString(cursor.getColumnIndex("username")),
                password = cursor.getString(cursor.getColumnIndex("password")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                firstName = cursor.getString(cursor.getColumnIndex("first_name")),
                lastName = cursor.getString(cursor.getColumnIndex("last_name")),
                phone = cursor.getString(cursor.getColumnIndex("phone")),
                roleId = cursor.getInt(cursor.getColumnIndex("role_id"))
            )
        }
        cursor.close()
        return user
    }

    /**
     * Updates a notification in the database with the given ID and returns the number of affected rows.
     *
     * @param id The ID of the notification to update.
     * @param message The updated message of the notification.
     * @param status The updated status of the notification.
     *
     * @return The number of affected rows.
     */
    fun updateNotification(id: Int, message: String, status: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("message", message)
        contentValues.put("status", status)

        return db.update("notifications", contentValues, "id = ?", arrayOf(id.toString()))
    }

    /**
     * Deletes a notification from the database with the given ID and returns the number of affected rows.
     *
     * @param id The ID of the notification to delete.
     *
     * @return The number of affected rows.
     */
    fun deleteNotification(id: Int): Int {
        val db = this.writableDatabase
        return db.delete("notifications", "id = ?", arrayOf(id.toString()))
    }

    /**
     * Updates a user in the database and returns the number of affected rows.
     *
     * @param user The User object containing the updated user data.
     *
     * @return The number of affected rows.
     */
    fun updateUser(user: User): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", user.username)
        contentValues.put("password", user.password)
        contentValues.put("email", user.email)
        contentValues.put("first_name", user.firstName)
        contentValues.put("last_name", user.lastName)
        contentValues.put("phone", user.phone)
        contentValues.put("role_id", user.roleId)

        return db.update("users", contentValues, "id = ?", arrayOf(user.id.toString()))
    }

    /**
     * Updates a performance metric in the database with the given ID and returns the number of affected rows.
     *
     * @param performanceMetricId The ID of the performance metric to update.
     * @param metricName The updated name of the performance metric.
     * @param metricValue The updated value of the performance metric.
     * @param goalValue The updated goal value of the performance metric.
     *
     * @return The number of affected rows.
     */
    fun updatePerformanceMetric(performanceMetricId: Int, metricName: String, metricValue: Double, goalValue: Double): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("metric_name", metricName)
        contentValues.put("metric_value", metricValue)
        contentValues.put("goal_value", goalValue)

        val whereClause = "id = ?"
        val whereArgs = arrayOf(performanceMetricId.toString())

        return db.update("performance_metrics", contentValues, whereClause, whereArgs)
    }

    /**
     * Deletes a performance metric from the database with the given ID and returns the number of affected rows.
     *
     * @param performanceMetricId The ID of the performance metric to delete.
     *
     * @return The number of affected rows.
     */
    fun deletePerformanceMetric(performanceMetricId: Int): Int {
        val db = this.writableDatabase
        val whereClause = "id = ?"
        val whereArgs = arrayOf(performanceMetricId.toString())

        return db.delete("performance_metrics", whereClause, whereArgs)
    }

    /**
     * Returns all shifts from the database.
     *
     * @return A MutableList of Shift objects representing all shifts in the database.
     */
    fun getAllShifts(): MutableList<Shift> {
        val shiftsList = mutableListOf<Shift>()

        // Define a projection that specifies the columns from the table we care about.
        val projection = arrayOf(
            "id",
            "user_id",
            "start_time",
            "end_time",
            "location",
            "role_id"
        )

        // Sort the results by start_time in descending order (most recent first)
        val sortOrder = "start_time DESC"

        val db = this.readableDatabase
        val cursor = db.query(
            "shifts",   // The table to query
            projection,  // The columns to return
            null,  // The columns for the WHERE clause
            null,  // The values for the WHERE clause
            null, null, sortOrder // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val userId = getInt(getColumnIndexOrThrow("user_id"))
                val startTime = getDouble(getColumnIndexOrThrow("start_time"))
                val endTime = getDouble(getColumnIndexOrThrow("end_time"))
                val location = getString(getColumnIndexOrThrow("location"))
                val roleId = getInt(getColumnIndexOrThrow("role_id"))
                shiftsList.add(Shift(id, userId, startTime, endTime, location, roleId))
            }
        }

        cursor.close()

        return shiftsList
    }

    /**
     * Updates the user and role of a shift with the given ID and returns a Boolean indicating success or failure.
     *
     * @param shiftId The ID of the shift to update.
     * @param userId The ID of the user to assign to the shift.
     * @param roleId The ID of the role to assign to the shift.
     *
     * @return true if the update was successful, false otherwise.
     */
    fun updateShiftUserAndRole(shiftId: Int, userId: Int, roleId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("user_id", userId)
        values.put("role_id", roleId)

        val result = db.update("shifts", values, "id=?", arrayOf(shiftId.toString()))

        return result != 0
    }

    /**
     * Adds a role to the database and returns the ID of the newly added role.
     *
     * @param name The name of the role.
     * @param description The description of the role.
     *
     * @return The ID of the newly added role.
     */
    fun addRole(name: String, description: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", name)
        values.put("description", description)

        return db.insert("roles", null, values)
    }

    /**
     * Returns all roles from the database.
     *
     * @return A MutableList of Role objects representing all roles in the database.
     */
    @SuppressLint("Range")
    fun getAllRoles(): MutableList<Role> {
        val rolesList = mutableListOf<Role>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM roles", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val description = cursor.getString(cursor.getColumnIndex("description"))

                val role = Role(id, name, description)
                rolesList.add(role)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return rolesList
    }

    /**
     * Updates a user's role in the database.
     *
     * @param userId The ID of the user to update.
     * @param newRoleId The new role ID to assign to the user.
     * @return The number of rows affected by the update operation.
     */
    fun updateUserRole(userId: Int, newRoleId: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("role_id", newRoleId)

        return db.update("users", contentValues, "id = ?", arrayOf(userId.toString()))
    }

    /**
     * Deletes a role from the database.
     *
     * @param roleId The ID of the role to delete.
     * @return The number of rows affected by the delete operation.
     */
    fun deleteRole(roleId: Int): Int {
        val db = writableDatabase
        return db.delete("roles", "id = ?", arrayOf(roleId.toString()))
    }

    /**
     * Deletes a time off request from the database.
     *
     * @param id The ID of the time off request to delete.
     * @return The number of rows affected by the delete operation.
     */
    fun deleteTimeOffRequest(id: Int): Int {
        val db = writableDatabase
        return db.delete("time_off_request", "id = ?", arrayOf(id.toString()))
    }

    /**
     * Updates the status of a time off request in the database.
     *
     * @param requestId The ID of the time off request to update.
     * @param status The new status to assign to the time off request.
     */
    fun updateTimeOffRequestStatus(requestId: Int, status: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("status", status)
        db.update("time_off_request", contentValues, "id=?", arrayOf(requestId.toString()))
        db.close()
    }

    /**
     * Retrieves a list of time off requests for a given user from the database.
     *
     * @param userId The ID of the user whose time off requests to retrieve.
     * @return A list of TimeOffRequest objects.
     */
    @SuppressLint("Range")
    fun getTimeOffRequests(userId: Int): List<TimeOffRequest> {
        val timeOffRequests = mutableListOf<TimeOffRequest>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM time_off_request WHERE user_id = ?", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val requestUserId = cursor.getInt(cursor.getColumnIndex("user_id"))
                val startDate = cursor.getLong(cursor.getColumnIndex("start_date"))
                val endDate = cursor.getLong(cursor.getColumnIndex("end_date"))
                val status = cursor.getString(cursor.getColumnIndex("status"))

                timeOffRequests.add(TimeOffRequest(id, requestUserId, startDate, endDate, status))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return timeOffRequests
    }

    /**
     * Adds a time off request for a user to the database.
     *
     * @param userId The ID of the user submitting the time off request.
     * @param startDate The start date of the time off request.
     * @param endDate The end date of the time off request.
     */
    fun addTimeOffRequest(userId: Int, startDate: Long, endDate: Long) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("user_id", userId)
        contentValues.put("start_date", startDate)
        contentValues.put("end_date", endDate)
        contentValues.put("status", "PENDING")

        db.insert("time_off_request", null, contentValues)
        db.close()
    }

    /**
     * Retrieves a list of all users from the database.
     *
     * @return A list of User objects.
     */
    @SuppressLint("Range")
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val username = cursor.getString(cursor.getColumnIndex("username"))
                val password = cursor.getString(cursor.getColumnIndex("password"))
                val email = cursor.getString(cursor.getColumnIndex("email"))
                val firstName = cursor.getString(cursor.getColumnIndex("first_name"))
                val lastName = cursor.getString(cursor.getColumnIndex("last_name"))
                val phone = cursor.getString(cursor.getColumnIndex("phone"))
                val roleId = cursor.getInt(cursor.getColumnIndex("role_id"))

                users.add(User(id, username, password, email, firstName, lastName, phone, roleId))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return users
    }

}
