package edu.cit.lim.gymtrack.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.cit.lim.gymtrack.mobile.data.model.StaffResponse
import edu.cit.lim.gymtrack.mobile.databinding.ItemStaffBinding

class StaffAdapter(
    private val onEdit: (StaffResponse) -> Unit
) : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

    private val items = mutableListOf<StaffResponse>()

    fun submitList(staff: List<StaffResponse>) {
        items.clear()
        items.addAll(staff)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemStaffBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(staff: StaffResponse) {
            binding.staffName.text = "${staff.firstName} ${staff.lastName}"
            binding.staffEmail.text = staff.email
            binding.staffActiveLabel.text = if (staff.active) "Active" else "Deactivated"
            binding.editStaffButton.setOnClickListener { onEdit(staff) }
        }
    }
}
