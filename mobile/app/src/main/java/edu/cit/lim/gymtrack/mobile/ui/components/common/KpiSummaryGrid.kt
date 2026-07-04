package edu.cit.lim.gymtrack.mobile.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSummaryItem

data class KpiItem(val label: String, val value: String)

@Composable
fun KpiSummaryGrid(items: List<KpiItem>, modifier: Modifier = Modifier) {
    if (items.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { item ->
            DashboardSummaryItem(label = item.label, value = item.value)
        }
    }
}
