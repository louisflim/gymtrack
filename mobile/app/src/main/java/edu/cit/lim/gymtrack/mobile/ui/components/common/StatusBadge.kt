package edu.cit.lim.gymtrack.mobile.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBorder
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted

@Composable
fun StatusBadge(status: String?) {
    val key = status ?: "NONE"
    val (bg, fg) = when (key) {
        "ACTIVE", "PAID" -> Color(0x26C8FF3D) to GymTrackAccent
        "EXPIRING_SOON", "PENDING" -> Color(0x26FFC43D) to Color(0xFFFFC43D)
        "EXPIRED", "FAILED" -> Color(0x26FF8A8A) to Color(0xFFFF8A8A)
        else -> Color(0x266B6B70) to GymTrackTextMuted
    }
    Box(
        modifier = Modifier
            .background(bg)
            .border(1.dp, GymTrackBorder)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = key.replace('_', ' '), style = MaterialTheme.typography.labelSmall, color = fg)
    }
}
