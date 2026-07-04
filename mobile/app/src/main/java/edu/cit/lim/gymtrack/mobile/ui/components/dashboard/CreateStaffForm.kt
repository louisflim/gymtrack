package edu.cit.lim.gymtrack.mobile.ui.components.dashboard

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AccountFieldValues
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AccountFields
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackError

@Composable
fun CreateStaffForm(
    values: AccountFieldValues,
    onFieldChange: (String, String) -> Unit,
    onSubmit: () -> Unit,
    loading: Boolean,
    statusMessage: String?,
    adminGymName: String? = null
) {
    val missingGym = adminGymName.isNullOrBlank()

    DashboardSectionCard(title = "Create Staff Account") {
        if (missingGym) {
            Text(
                text = "Your admin account is not linked to a gym. Sign out and register again as Gym Owner with a gym name.",
                style = MaterialTheme.typography.bodyMedium,
                color = GymTrackError
            )
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Text(
                text = "Gym: ${adminGymName} — new staff will belong to this gym.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        AccountFields(
            values = values,
            onFieldChange = onFieldChange,
            passwordPlaceholder = "Staff password"
        )
        AuthSubmitButton(
            text = if (loading) "Creating Staff..." else "Create Staff",
            onClick = onSubmit,
            enabled = !loading && !missingGym &&
                values.firstName.isNotBlank() &&
                values.lastName.isNotBlank() &&
                values.email.isNotBlank() &&
                values.password.isNotBlank() &&
                values.confirmPassword.isNotBlank()
        )
        DashboardStatusText(message = statusMessage)
    }
    Spacer(modifier = Modifier.height(20.dp))
}
