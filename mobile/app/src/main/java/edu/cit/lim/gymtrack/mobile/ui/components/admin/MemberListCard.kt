package edu.cit.lim.gymtrack.mobile.ui.components.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.MemberResponse
import edu.cit.lim.gymtrack.mobile.data.model.MemberUpdateRequest
import edu.cit.lim.gymtrack.mobile.data.model.PlanResponse
import edu.cit.lim.gymtrack.mobile.ui.components.common.FilterBar
import edu.cit.lim.gymtrack.mobile.ui.components.common.StatusBadge
import edu.cit.lim.gymtrack.mobile.ui.components.common.StatusFilterChips
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary

@Composable
fun MemberListCard(
    members: List<MemberResponse>,
    plans: List<PlanResponse>,
    search: String,
    onSearchChange: (String) -> Unit,
    status: String,
    onStatusChange: (String) -> Unit,
    onUpdate: (Long, MemberUpdateRequest) -> Unit,
    onAssignPlan: (Long, Long) -> Unit
) {
    var editing by remember { mutableStateOf<MemberResponse?>(null) }
    var assigning by remember { mutableStateOf<MemberResponse?>(null) }

    val filtered = members.filter { member ->
        val term = search.trim().lowercase()
        val matchesSearch = term.isEmpty() ||
            "${member.firstName} ${member.lastName} ${member.email}".lowercase().contains(term)
        val matchesStatus = status == "ALL" || member.membershipStatus == status
        matchesSearch && matchesStatus
    }

    DashboardSectionCard(title = "Member Management") {
        FilterBar(
            search = search,
            onSearchChange = onSearchChange,
            searchPlaceholder = "Search members..."
        )
        Spacer(modifier = Modifier.height(10.dp))
        StatusFilterChips(
            selected = status,
            options = listOf(
                "ALL" to "All",
                "ACTIVE" to "Active",
                "EXPIRING_SOON" to "Expiring",
                "EXPIRED" to "Expired",
                "NONE" to "None"
            ),
            onSelected = onStatusChange
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Text(
                text = if (members.isEmpty()) {
                    "No members enrolled at your gym yet. Ask new members to scan your gym QR code."
                } else {
                    "No members match your search or filter."
                },
                color = GymTrackAccent
            )
        } else {
            filtered.forEach { member ->
                Column {
                    Text(
                        text = "${member.firstName} ${member.lastName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = GymTrackTextPrimary
                    )
                    Text(text = member.email, color = GymTrackAccent)
                    Text(text = member.planName ?: "No plan", color = GymTrackAccent)
                    StatusBadge(member.membershipStatus)
                    TextButton(onClick = { editing = member }) { Text("Edit", color = GymTrackAccent) }
                    TextButton(onClick = { assigning = member }) { Text("Assign Plan", color = GymTrackAccent) }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    editing?.let { member ->
        MemberEditDialog(
            member = member,
            onDismiss = { editing = null },
            onSave = { request ->
                onUpdate(member.id, request)
                editing = null
            }
        )
    }

    assigning?.let { member ->
        AssignPlanDialog(
            member = member,
            plans = plans,
            onDismiss = { assigning = null },
            onAssign = { planId ->
                onAssignPlan(member.id, planId)
                assigning = null
            }
        )
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun MemberEditDialog(
    member: MemberResponse,
    onDismiss: () -> Unit,
    onSave: (MemberUpdateRequest) -> Unit
) {
    var firstName by remember(member.id) { mutableStateOf(member.firstName) }
    var lastName by remember(member.id) { mutableStateOf(member.lastName) }
    var email by remember(member.id) { mutableStateOf(member.email) }
    var active by remember(member.id) { mutableStateOf(member.active) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Member") },
        text = {
            Column {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Switch(checked = active, onCheckedChange = { active = it })
                Text(if (active) "Active account" else "Deactivated", color = GymTrackAccent)
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(MemberUpdateRequest(firstName, lastName, email, active)) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun AssignPlanDialog(
    member: MemberResponse,
    plans: List<PlanResponse>,
    onDismiss: () -> Unit,
    onAssign: (Long) -> Unit
) {
    var selectedPlanId by remember(plans) { mutableStateOf(plans.firstOrNull()?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Plan to ${member.firstName}") },
        text = {
            Column {
                plans.forEach { plan ->
                    TextButton(onClick = { selectedPlanId = plan.id }) {
                        Text(
                            text = if (selectedPlanId == plan.id) "• ${plan.name}" else plan.name,
                            color = GymTrackAccent
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedPlanId?.let(onAssign) },
                enabled = selectedPlanId != null
            ) { Text("Assign") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
