package edu.cit.lim.gymtrack.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.cit.lim.gymtrack.mobile.data.model.AttendanceLogResponse
import edu.cit.lim.gymtrack.mobile.databinding.ItemAttendanceLogBinding
import edu.cit.lim.gymtrack.mobile.ui.util.formatDateTime

class AttendanceLogAdapter : RecyclerView.Adapter<AttendanceLogAdapter.ViewHolder>() {

    private val items = mutableListOf<AttendanceLogResponse>()

    fun submitList(logs: List<AttendanceLogResponse>) {
        items.clear()
        items.addAll(logs)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemAttendanceLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log: AttendanceLogResponse) {
            binding.logMemberName.text = log.memberName ?: "My visit"
            binding.logAction.text = "Check-in: ${formatDateTime(log.checkInTime)}"
            binding.logTimestamp.text = "Check-out: ${formatDateTime(log.checkOutTime)}"
        }
    }
}
