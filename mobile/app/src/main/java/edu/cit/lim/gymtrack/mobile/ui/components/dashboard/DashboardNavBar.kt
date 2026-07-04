package edu.cit.lim.gymtrack.mobile.ui.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBorder
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary
import edu.cit.lim.gymtrack.mobile.ui.util.DashboardUiCopy

data class DashboardNavItem(
    val id: String,
    val label: String,
    val icon: String
)

@Composable
fun DashboardNavBar(
    role: String,
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = DashboardUiCopy.navTabsForRole(role)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GymTrackBackground)
            .border(1.dp, GymTrackBorder)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            val selected = activeTab == tab.id
            Column(
                modifier = Modifier
                    .background(if (selected) GymTrackAccent else GymTrackBackground)
                    .border(1.dp, if (selected) GymTrackAccent else GymTrackBorder)
                    .clickable { onTabSelected(tab.id) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab.icon,
                    fontSize = 18.sp,
                    color = if (selected) GymTrackBackground else GymTrackTextPrimary
                )
                Text(
                    text = tab.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) GymTrackBackground else GymTrackTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
