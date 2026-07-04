package edu.cit.lim.gymtrack.mobile.ui.components.admin

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.AttendanceLogResponse
import edu.cit.lim.gymtrack.mobile.ui.components.common.AttendanceLogList
import edu.cit.lim.gymtrack.mobile.ui.components.common.FilterBar
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard

@Composable
fun AttendanceLogCard(
    logs: List<AttendanceLogResponse>,
    search: String,
    onSearchChange: (String) -> Unit,
    date: String,
    onDateChange: (String) -> Unit
) {
    DashboardSectionCard(title = "Attendance Logs") {
        FilterBar(
            search = search,
            onSearchChange = onSearchChange,
            searchPlaceholder = "Search by member name or email...",
            date = date,
            onDateChange = onDateChange
        )
        Spacer(modifier = Modifier.height(12.dp))
        AttendanceLogList(
            logs = logs,
            showMember = true,
            emptyMessage = "No attendance logs match your filters."
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
}
