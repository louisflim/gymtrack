package edu.cit.lim.gymtrack.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.cit.lim.gymtrack.mobile.databinding.ItemKpiBinding
import edu.cit.lim.gymtrack.mobile.ui.model.KpiItem

class KpiAdapter : RecyclerView.Adapter<KpiAdapter.ViewHolder>() {

    private val items = mutableListOf<KpiItem>()

    fun submitList(kpis: List<KpiItem>) {
        items.clear()
        items.addAll(kpis)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKpiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemKpiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KpiItem) {
            binding.kpiLabel.text = item.label.uppercase()
            binding.kpiValue.text = item.value
        }
    }
}
