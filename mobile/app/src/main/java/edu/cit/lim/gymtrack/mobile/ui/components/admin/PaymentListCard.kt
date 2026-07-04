package edu.cit.lim.gymtrack.mobile.ui.components.admin

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
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary

@Composable
fun PaymentListCard(payments: List<PaymentResponse>) {
    DashboardSectionCard(title = "All Payments") {
        if (payments.isEmpty()) {
            Text(text = "No payments yet.")
        } else {
            payments.take(15).forEach { payment ->
                Column {
                    Text(
                        text = "${payment.memberName ?: "Member"} — ${payment.planName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GymTrackTextPrimary
                    )
                    Text(text = "₱${payment.amount}")
                    StatusBadge(payment.status)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}
