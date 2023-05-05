package com.example.archer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.archer.Model.Shift
import com.example.archer.databinding.ShiftItemBinding

class ShiftsAdapter(
    private val shiftsList: MutableList<Shift>,
    private val currentUserId: Int,
    private val onEditClickListener: (Shift) -> Unit,
    private val onDeleteClickListener: (Shift) -> Unit,
    private val onBookClickListener: (Shift) -> Unit
) : RecyclerView.Adapter<ShiftsAdapter.ShiftViewHolder>() {
    /**
     * This inner class is a RecyclerView ViewHolder that binds a Shift object to a view item in the list.
     * @param binding the view binding for the Shift item layout.
     */
    inner class ShiftViewHolder(val binding: ShiftItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Initializes the view holder by attaching click listeners to the edit, delete, and book shift buttons.
         */

        init {
            if (currentUserId != 1) {
                binding.btnEditShift.visibility = View.GONE
            } else {
                binding.btnEditShift.setOnClickListener {
                    onEditClickListener(shiftsList[adapterPosition])
                }
            }
            if (currentUserId != 1) {
                binding.btnDeleteShift.visibility = View.GONE
            } else {
                binding.btnDeleteShift.setOnClickListener {
                    onDeleteClickListener(shiftsList[adapterPosition])
                }
            }
            binding.btnBookShift.setOnClickListener {
                onBookClickListener(shiftsList[adapterPosition])
            }
        }
        /**
         * Binds the properties of a Shift object to the views in the layout.
         * @param shift the Shift object to be bound to the view holder.
         */
        fun bind(shift: Shift) {
            binding.tvShiftLocation.text = shift.location
            binding.tvStartTime.text = shift.startTime.toString()
            binding.tvEndTime.text = shift.endTime.toString()

            binding.tvShiftStatus.text = if (shift.roleId == 1) "Available" else "Booked"
        }
    }

    /**
     * This function creates and returns a new ShiftViewHolder that will be used to display shift items in the RecyclerView.
     *
     * @param parent The parent ViewGroup that will contain the newly created view.
     * @param viewType An integer value that specifies the type of the view to be created.
     * @return A new ShiftViewHolder object that contains a view for a single shift item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        // Inflate the layout for a single shift item using the ShiftItemBinding class.
        val binding = ShiftItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        // Create a new ShiftViewHolder and pass in the inflated view.
        return ShiftViewHolder(binding)
    }

    /**
     * This function is called for each shift item in the RecyclerView to bind the shift data to the view holder.
     *
     * @param holder The ShiftViewHolder that should be updated with the new data.
     * @param position The index of the Shift object in the shiftsList that should be bound to the view holder.
     */
    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        // Get the Shift object at the current position in the list.
        val shift = shiftsList[position]
        // Bind the shift data to the view holder.
        holder.bind(shift)

        // Show the "book" button only if the shift is available or booked by the current user.
        // The visibility of the button is set based on the user ID stored in the Shift object.
        holder.binding.btnBookShift.visibility = if (shift.userId == -1 || shift.userId == currentUserId) View.VISIBLE else View.GONE
    }

    /**
     * This function returns the number of items in the shiftsList.
     *
     * @return The number of items in the shiftsList.
     */
    override fun getItemCount(): Int {
        return shiftsList.size
    }


}
