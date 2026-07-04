package edu.cit.lim.gymtrack.mobile.ui.components.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.PlanResponse
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardStatusText
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted

@Composable
fun PlanPickerCard(
    plans: List<PlanResponse>,
    subscribing: Boolean,
    statusMessage: String?,
    enrolled: Boolean,
    onSubscribe: (Long) -> Unit
) {
    DashboardSectionCard(title = "Choose a Plan") {
        when {
            !enrolled -> Text(
                text = "Scan the gym QR code at the front desk first to see available subscription plans.",
                color = GymTrackTextMuted
            )
            plans.isEmpty() -> Text(text = "No active plans available at your gym.", color = GymTrackTextMuted)
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                plans.forEach { plan ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = plan.name, style = MaterialTheme.typography.titleMedium, color = GymTrackAccent)
                        Text(text = "₱${plan.price} • ${plan.durationDays} days", color = GymTrackTextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        AuthSubmitButton(
                            text = if (subscribing) "Processing..." else "Subscribe & Pay",
                            onClick = { onSubscribe(plan.id) },
                            enabled = !subscribing
                        )
                    }
                }
            }
        }
        DashboardStatusText(message = statusMessage)
    }
    Spacer(modifier = Modifier.height(20.dp))
}
