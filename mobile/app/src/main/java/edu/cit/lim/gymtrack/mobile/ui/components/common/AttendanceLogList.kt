package edu.cit.lim.gymtrack.mobile.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.AttendanceLogResponse
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary
import edu.cit.lim.gymtrack.mobile.ui.util.formatDateTime

@Composable
fun AttendanceLogList(
    logs: List<AttendanceLogResponse>,
    showMember: Boolean = false,
    emptyMessage: String = "No attendance records yet."
) {
    if (logs.isEmpty()) {
        Text(text = emptyMessage, color = GymTrackAccent)
        return
    }

    Column {
        logs.forEachIndexed { index, log ->
            if (index > 0) Spacer(modifier = Modifier.height(10.dp))
            Column {
                if (showMember && !log.memberName.isNullOrBlank()) {
                    Text(
                        text = log.memberName,
                        style = MaterialTheme.typography.titleSmall,
                        color = GymTrackTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(text = "Check-in: ${formatDateTime(log.checkInTime)}", color = GymTrackAccent)
                Text(text = "Check-out: ${formatDateTime(log.checkOutTime)}", color = GymTrackAccent)
            }
        }
    }
}
