package edu.cit.lim.gymtrack.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.cit.lim.gymtrack.mobile.data.model.PaymentResponse
import edu.cit.lim.gymtrack.mobile.databinding.ItemPaymentBinding
import edu.cit.lim.gymtrack.mobile.ui.util.applyStatusBadge
import edu.cit.lim.gymtrack.mobile.ui.util.formatDateTime

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    private val items = mutableListOf<PaymentResponse>()

    fun submitList(payments: List<PaymentResponse>) {
        items.clear()
        items.addAll(payments.take(10))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(payment: PaymentResponse) {
            binding.paymentTitle.text = "${payment.planName} — ₱${payment.amount}"
            binding.paymentStatus.applyStatusBadge(payment.status)
            binding.paymentDate.text = formatDateTime(payment.paidAt ?: payment.createdAt)
        }
    }
}
