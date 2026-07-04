package edu.cit.lim.gymtrack.mobile.ui.components.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.PlanResponse
import edu.cit.lim.gymtrack.mobile.ui.components.common.StatusBadge
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary

@Composable
fun PlanListCard(plans: List<PlanResponse>, onEdit: (PlanResponse) -> Unit) {
    DashboardSectionCard(title = "Subscription Plans") {
        if (plans.isEmpty()) {
            Text(text = "No plans yet.", color = GymTrackAccent)
        } else {
            plans.forEach { plan ->
                Column {
                    Text(text = plan.name, style = MaterialTheme.typography.titleMedium, color = GymTrackTextPrimary)
                    Text(text = "₱${plan.price} • ${plan.durationDays} days", color = GymTrackAccent)
                    StatusBadge(if (plan.active) "ACTIVE" else "EXPIRED")
                    TextButton(onClick = { onEdit(plan) }) { Text("Edit", color = GymTrackAccent) }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}
