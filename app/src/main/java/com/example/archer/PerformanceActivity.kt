package com.example.archer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.archer.Model.DBHelper
import com.example.archer.Model.PerformanceMetric
import com.example.archer.databinding.ActivityPerformanceBinding

class PerformanceActivity : AppCompatActivity() {

    /**
     * Activity for displaying a list of performance metrics and allowing the user to add, edit,
     * and delete metrics.
     */
    private lateinit var binding: ActivityPerformanceBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var performanceAdapter: PerformanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout for the activity and set it as the content view
        binding = ActivityPerformanceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize the DBHelper and get the performance data
        dbHelper = DBHelper(this)
        val performanceData = dbHelper.getAllPerformanceMetrics()

        // Initialize the performance adapter with the performance data and click listeners for the edit and delete buttons
        performanceAdapter = PerformanceAdapter(
            performanceData,
            onEditClickListener = { performanceMetric ->
                // Handle edit button click here
                showEditDialog(performanceMetric)
            },
            onDeleteClickListener = { performanceMetric ->
                // Handle delete button click here
                AlertDialog.Builder(this)
                    .setTitle("Delete Metric")
                    .setMessage("Are you sure you want to delete this metric?")
                    .setPositiveButton("Yes") { _, _ ->
                        dbHelper.deletePerformanceMetric(performanceMetric.id)
                        performanceAdapter.performanceList.remove(performanceMetric)
                        performanceAdapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        // Set the layout manager and adapter for the RecyclerView
        binding.performanceRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.performanceRecyclerView.adapter = performanceAdapter

        // Set the click listener for the "Add Metric" button
        binding.btnAddMetric.setOnClickListener {
            val metricName = binding.etMetricName.text.toString().trim()
            val metricValue = binding.etMetricValue.text.toString().trim().toDouble()
            val goalValue = binding.etGoalValue.text.toString().trim().toDouble()

            // Replace with the actual user ID
            val userId = 1

            // Add the performance metric to the database and the adapter if successful
            val result = dbHelper.addPerformanceMetric(userId, metricName, metricValue, goalValue)

            if (result > 0) {
                val newPerformanceMetric = PerformanceMetric(result.toInt(), userId, metricName, metricValue, goalValue)
                addPerformanceMetric(newPerformanceMetric)
            }
        }

        // Set the click listener for the "Back" button
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * Adds a performance metric to the list and updates the adapter
     *
     * @param performanceMetric the performance metric to be added
     */
    private fun addPerformanceMetric(performanceMetric: PerformanceMetric) {
        performanceAdapter.performanceList.add(performanceMetric)
        performanceAdapter.notifyDataSetChanged()
    }

    /**
     * Shows a dialog for editing the given performance metric and updates the adapter
     *
     * @param performanceMetric the performance metric to be edited
     */
    private fun showEditDialog(performanceMetric: PerformanceMetric) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.edit_performance_metric_dialog, null)

        val etMetricName = view.findViewById<EditText>(R.id.et_edit_metric_name)
        val etMetricValue = view.findViewById<EditText>(R.id.et_edit_metric_value)
        val etGoalValue = view.findViewById<EditText>(R.id.et_edit_goal_value)

        etMetricName.setText(performanceMetric.metricName)
        etMetricValue.setText(performanceMetric.metricValue.toString())
        etGoalValue.setText(performanceMetric.goalValue.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Metric")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val updatedMetricName = etMetricName.text.toString().trim()
                val updatedMetricValue = etMetricValue.text.toString().trim().toDouble()
                val updatedGoalValue = etGoalValue.text.toString().trim().toDouble()

                dbHelper.updatePerformanceMetric(performanceMetric.id, updatedMetricName, updatedMetricValue, updatedGoalValue)

                performanceMetric.metricName = updatedMetricName
                performanceMetric.metricValue = updatedMetricValue
                performanceMetric.goalValue = updatedGoalValue
                performanceAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
