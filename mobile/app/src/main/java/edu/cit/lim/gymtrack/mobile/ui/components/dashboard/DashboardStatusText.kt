package edu.cit.lim.gymtrack.mobile.ui.components.dashboard

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent

@Composable
fun DashboardStatusText(message: String?) {
    if (message.isNullOrBlank()) return
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = GymTrackAccent
    )
}
