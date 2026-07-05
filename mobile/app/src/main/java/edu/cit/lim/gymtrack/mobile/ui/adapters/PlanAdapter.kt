package edu.cit.lim.gymtrack.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.cit.lim.gymtrack.mobile.data.model.PlanResponse
import edu.cit.lim.gymtrack.mobile.databinding.ItemPlanBinding

class PlanAdapter(
    private val subscribing: Boolean,
    private val onSubscribe: (Long) -> Unit
) : RecyclerView.Adapter<PlanAdapter.ViewHolder>() {

    private val items = mutableListOf<PlanResponse>()

    fun submitList(plans: List<PlanResponse>) {
        items.clear()
        items.addAll(plans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: PlanResponse) {
            binding.planName.text = plan.name
            binding.planDetails.text = "₱${plan.price} • ${plan.durationDays} days"
            binding.subscribeButton.isEnabled = !subscribing
            binding.subscribeButton.text = if (subscribing) "Processing..." else "Subscribe & Pay"
            binding.subscribeButton.setOnClickListener { onSubscribe(plan.id) }
        }
    }
}
