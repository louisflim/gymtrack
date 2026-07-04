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
import edu.cit.lim.gymtrack.mobile.data.model.StaffResponse
import edu.cit.lim.gymtrack.mobile.data.model.StaffUpdateRequest
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary

@Composable
fun StaffListCard(
    staff: List<StaffResponse>,
    onUpdate: (Long, StaffUpdateRequest) -> Unit
) {
    var editing by remember { mutableStateOf<StaffResponse?>(null) }

    DashboardSectionCard(title = "Staff Accounts") {
        if (staff.isEmpty()) {
            Text(text = "No staff accounts yet. Create one from the Overview tab.", color = GymTrackAccent)
        } else {
            staff.forEach { member ->
                Column {
                    Text(
                        text = "${member.firstName} ${member.lastName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = GymTrackTextPrimary
                    )
                    Text(text = member.email, color = GymTrackAccent)
                    Text(
                        text = if (member.active) "Active" else "Deactivated",
                        color = GymTrackAccent
                    )
                    TextButton(onClick = { editing = member }) {
                        Text("Edit", color = GymTrackAccent)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    editing?.let { staffMember ->
        StaffEditDialog(
            staff = staffMember,
            onDismiss = { editing = null },
            onSave = { request ->
                onUpdate(staffMember.id, request)
                editing = null
            }
        )
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun StaffEditDialog(
    staff: StaffResponse,
    onDismiss: () -> Unit,
    onSave: (StaffUpdateRequest) -> Unit
) {
    var firstName by remember(staff.id) { mutableStateOf(staff.firstName) }
    var lastName by remember(staff.id) { mutableStateOf(staff.lastName) }
    var email by remember(staff.id) { mutableStateOf(staff.email) }
    var active by remember(staff.id) { mutableStateOf(staff.active) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Staff") },
        text = {
            Column {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Switch(checked = active, onCheckedChange = { active = it })
                Text(if (active) "Active account" else "Deactivated", color = GymTrackAccent)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(StaffUpdateRequest(firstName, lastName, email, active))
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
