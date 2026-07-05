package edu.cit.lim.gymtrack.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.cit.lim.gymtrack.mobile.data.model.MemberResponse
import edu.cit.lim.gymtrack.mobile.databinding.ItemMemberBinding
import edu.cit.lim.gymtrack.mobile.ui.util.applyStatusBadge

class MemberAdapter(
    private val onEdit: (MemberResponse) -> Unit,
    private val onAssignPlan: (MemberResponse) -> Unit
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    private val items = mutableListOf<MemberResponse>()

    fun submitList(members: List<MemberResponse>) {
        items.clear()
        items.addAll(members)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: MemberResponse) {
            binding.memberName.text = "${member.firstName} ${member.lastName}"
            binding.memberEmail.text = member.email
            binding.memberPlan.text = member.planName ?: "No plan"
            binding.memberStatus.applyStatusBadge(member.membershipStatus)
            binding.editMemberButton.setOnClickListener { onEdit(member) }
            binding.assignPlanButton.setOnClickListener { onAssignPlan(member) }
        }
    }
}
