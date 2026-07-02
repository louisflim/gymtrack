package edu.cit.lim.gymtrack.mobile.ui.components.dashboard

import androidx.compose.runtime.Composable
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton

@Composable
fun AttendanceScannerSection(
    onOpenScanner: () -> Unit,
    statusMessage: String?
) {
    AuthSubmitButton(
        text = "Scan QR with Camera",
        onClick = onOpenScanner,
        enabled = true
    )
    DashboardStatusText(message = statusMessage)
}
