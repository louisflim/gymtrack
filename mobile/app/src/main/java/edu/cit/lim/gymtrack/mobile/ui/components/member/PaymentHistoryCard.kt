package edu.cit.lim.gymtrack.mobile.ui.components.member

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.PaymentResponse
import edu.cit.lim.gymtrack.mobile.ui.components.common.StatusBadge
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSectionCard
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary

@Composable
fun PaymentHistoryCard(payments: List<PaymentResponse>, title: String = "Payment History") {
    DashboardSectionCard(title = title) {
        if (payments.isEmpty()) {
            Text(text = "No payments yet.", color = GymTrackTextMuted)
        } else {
            Column {
                payments.take(10).forEach { payment ->
                    Text(
                        text = "${payment.planName} — ₱${payment.amount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GymTrackTextPrimary
                    )
                    StatusBadge(payment.status)
                    Text(
                        text = payment.paidAt ?: payment.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = GymTrackTextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}
