package edu.cit.lim.gymtrack.mobile.ui.components.member

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.AttendanceLogResponse
import edu.cit.lim.gymtrack.mobile.ui.components.common.AttendanceLogList
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard

@Composable
fun AttendanceHistoryCard(
    logs: List<AttendanceLogResponse>,
    loading: Boolean = false,
    title: String = "Attendance History"
) {
    DashboardSectionCard(title = title) {
        if (loading) {
            AttendanceLogList(logs = emptyList(), emptyMessage = "Loading attendance...")
        } else {
            AttendanceLogList(logs = logs)
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}
