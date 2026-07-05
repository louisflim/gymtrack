package edu.cit.lim.gymtrack.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.cit.lim.gymtrack.mobile.data.model.PlanResponse
import edu.cit.lim.gymtrack.mobile.databinding.ItemPlanAdminBinding
import edu.cit.lim.gymtrack.mobile.ui.util.applyStatusBadge

class PlanAdminAdapter(
    private val onEdit: (PlanResponse) -> Unit
) : RecyclerView.Adapter<PlanAdminAdapter.ViewHolder>() {

    private val items = mutableListOf<PlanResponse>()

    fun submitList(plans: List<PlanResponse>) {
        items.clear()
        items.addAll(plans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlanAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemPlanAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: PlanResponse) {
            binding.planName.text = plan.name
            binding.planDetails.text = "₱${plan.price} • ${plan.durationDays} days"
            binding.planStatus.applyStatusBadge(if (plan.active) "ACTIVE" else "EXPIRED")
            binding.editPlanButton.setOnClickListener { onEdit(plan) }
        }
    }
}
