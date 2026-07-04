package edu.cit.lim.gymtrack.mobile.ui.components.member

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.MembershipResponse
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardStatusText
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted
import edu.cit.lim.gymtrack.mobile.ui.util.DashboardUiCopy

@Composable
fun MemberOnboardingCard(
    membership: MembershipResponse?,
    onOpenScanner: () -> Unit,
    statusMessage: String?
) {
    val step = membership?.nextStep ?: "ENROLL_AT_GYM"
    val (title, body) = DashboardUiCopy.onboardingStep(step)
    val needsScan = step == "ENROLL_AT_GYM"

    DashboardSectionCard(title = title) {
        Text(text = body, style = MaterialTheme.typography.bodyMedium, color = GymTrackTextMuted)
        if (!membership?.gymName.isNullOrBlank() && step != "ENROLL_AT_GYM") {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Gym: ${membership?.gymName}", color = GymTrackTextMuted)
        }
        if (needsScan) {
            Spacer(modifier = Modifier.height(12.dp))
            AuthSubmitButton(text = "Scan Gym QR Code", onClick = onOpenScanner, enabled = true)
        }
        DashboardStatusText(message = statusMessage)
    }
    Spacer(modifier = Modifier.height(20.dp))
}
