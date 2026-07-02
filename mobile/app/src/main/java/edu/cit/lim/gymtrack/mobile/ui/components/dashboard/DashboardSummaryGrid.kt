package edu.cit.lim.gymtrack.mobile.ui.components.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardSummaryGrid(firstName: String, lastName: String, role: String) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DashboardSummaryItem(label = "Name", value = "$firstName $lastName".trim())
        DashboardSummaryItem(label = "Role", value = role)
        DashboardSummaryItem(label = "Status", value = "Active")
    }
}
