package edu.cit.lim.gymtrack.mobile.ui.components.admin

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthTextField
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardStatusText
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.PlanFormState

@Composable
fun PlanFormCard(
    form: PlanFormState,
    editing: Boolean,
    statusMessage: String?,
    onFieldChange: (String, String) -> Unit,
    onSave: () -> Unit
) {
    DashboardSectionCard(title = if (editing) "Edit Plan" else "Create Plan") {
        AuthTextField("Plan Name", form.name, { onFieldChange("name", it) }, "Monthly Basic")
        AuthTextField("Duration (days)", form.durationDays, { onFieldChange("durationDays", it) }, "30")
        AuthTextField("Price (PHP)", form.price, { onFieldChange("price", it) }, "999")
        AuthSubmitButton(text = if (editing) "Update Plan" else "Create Plan", onClick = onSave, enabled = true)
        DashboardStatusText(message = statusMessage)
    }
    Spacer(modifier = Modifier.height(20.dp))
}
