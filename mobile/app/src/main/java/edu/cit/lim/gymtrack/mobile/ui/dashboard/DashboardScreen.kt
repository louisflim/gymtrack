package edu.cit.lim.gymtrack.mobile.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import edu.cit.lim.gymtrack.mobile.ui.components.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.BrandStat
import edu.cit.lim.gymtrack.mobile.ui.components.DashboardBrandPanel
import edu.cit.lim.gymtrack.mobile.ui.components.DashboardSummaryItem
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted

@Composable
fun DashboardScreen(
    session: UserSession,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymTrackBackground)
            .verticalScroll(rememberScrollState())
    ) {
        DashboardBrandPanel(
            eyebrow = "GymTrack Dashboard",
            title = "TRAIN\nTRACK",
            highlight = "THRIVE",
            tagline = "Monitor your gym access, membership details, and activity from one place.",
            stats = listOf(
                BrandStat("01", "Profile"),
                BrandStat("02", "Membership"),
                BrandStat("03", "Access")
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x05FFFFFF), Color.Transparent)
                    )
                )
                .padding(horizontal = 28.dp, vertical = 40.dp)
        ) {
            Text(
                text = "OVERVIEW",
                style = MaterialTheme.typography.titleMedium,
                color = edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "DASHBOARD",
                style = MaterialTheme.typography.headlineLarge,
                color = edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Signed in as ${session.firstName} ${session.lastName}.",
                style = MaterialTheme.typography.bodyMedium,
                color = GymTrackTextMuted
            )

            Spacer(modifier = Modifier.height(28.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardSummaryItem(
                    label = "Name",
                    value = "${session.firstName} ${session.lastName}".trim()
                )
                DashboardSummaryItem(
                    label = "Role",
                    value = session.role
                )
                DashboardSummaryItem(
                    label = "Status",
                    value = "Active"
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            AuthSubmitButton(
                text = "Sign Out",
                onClick = onSignOut,
                enabled = true
            )
        }
    }
}
