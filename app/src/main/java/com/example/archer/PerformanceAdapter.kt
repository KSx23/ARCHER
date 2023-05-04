package com.example.archer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.archer.Model.PerformanceMetric

/**
 * RecyclerView adapter for the list of performance metrics.
 *
 * @property performanceList The list of performance metrics to display.
 * @property onEditClickListener The click listener for the edit button.
 * @property onDeleteClickListener The click listener for the delete button.
 */
class PerformanceAdapter(

    val performanceList: MutableList<PerformanceMetric>,
    private val onEditClickListener: (PerformanceMetric) -> Unit,
    private val onDeleteClickListener: (PerformanceMetric) -> Unit
) : RecyclerView.Adapter<PerformanceAdapter.ViewHolder>() {

    /**
     * View holder for the list items.
     *
     * @property itemView The view for the list item.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val metricName: TextView = itemView.findViewById(R.id.tv_metric_name)
        val metricValue: TextView = itemView.findViewById(R.id.tv_metric_value)
        val goalValue: TextView = itemView.findViewById(R.id.tv_goal_value)
        val editButton: ImageButton = itemView.findViewById(R.id.ib_edit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.ib_delete)

        init {
            // Set click listeners for the edit and delete buttons
            editButton.setOnClickListener {
                onEditClickListener(performanceList[adapterPosition])
            }
            deleteButton.setOnClickListener {
                onDeleteClickListener(performanceList[adapterPosition])
            }
        }
    }

    /**
     * Returns a new instance of ViewHolder class that holds the inflated view of a single item in the RecyclerView.
     *
     * @param parent The parent ViewGroup of the item view.
     * @param viewType The type of view to be inflated.
     * @return The ViewHolder instance that holds the inflated view of the item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.performance_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called by the RecyclerView to display the data at the specified position.
     * This method updates the contents of the ViewHolder to reflect the item at the given position.
     *
     * @param holder The ViewHolder that represents the item at the specified position in the RecyclerView.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = performanceList[position]
        holder.metricName.text = currentItem.metricName
        holder.metricValue.text = currentItem.metricValue.toString()
        holder.goalValue.text = currentItem.goalValue.toString()
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the adapter's data set.
     */
    override fun getItemCount(): Int {
        return performanceList.size
    }

}
