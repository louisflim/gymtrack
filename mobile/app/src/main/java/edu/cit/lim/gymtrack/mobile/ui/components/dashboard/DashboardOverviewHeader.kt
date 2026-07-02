package edu.cit.lim.gymtrack.mobile.ui.components.dashboard

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary

@Composable
fun DashboardOverviewHeader(firstName: String, lastName: String) {
    Text(
        text = "OVERVIEW",
        style = MaterialTheme.typography.titleMedium,
        color = GymTrackAccent
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "DASHBOARD",
        style = MaterialTheme.typography.headlineLarge,
        color = GymTrackTextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Signed in as $firstName $lastName.",
        style = MaterialTheme.typography.bodyMedium,
        color = GymTrackTextMuted
    )
}
