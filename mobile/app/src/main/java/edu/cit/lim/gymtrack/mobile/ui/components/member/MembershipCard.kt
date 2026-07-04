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
import edu.cit.lim.gymtrack.mobile.data.model.MembershipResponse
import edu.cit.lim.gymtrack.mobile.ui.components.common.StatusBadge
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary

@Composable
fun MembershipCard(membership: MembershipResponse?) {
    DashboardSectionCard(title = "My Membership") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = membership?.planName ?: "No plan", style = MaterialTheme.typography.bodyLarge, color = GymTrackTextPrimary)
            StatusBadge(membership?.status)
            Text(text = "Valid until: ${membership?.endDate ?: "—"}", style = MaterialTheme.typography.bodyMedium, color = GymTrackTextMuted)
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}
