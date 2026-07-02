package edu.cit.lim.gymtrack.mobile.ui.components.dashboard

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AccountFieldValues
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AccountFields
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton

@Composable
fun CreateStaffForm(
    values: AccountFieldValues,
    onFieldChange: (String, String) -> Unit,
    onSubmit: () -> Unit,
    loading: Boolean,
    statusMessage: String?
) {
    DashboardSectionCard(title = "Create Staff Account") {
        AccountFields(
            values = values,
            onFieldChange = onFieldChange,
            passwordPlaceholder = "Staff password"
        )
        AuthSubmitButton(
            text = if (loading) "Creating Staff..." else "Create Staff",
            onClick = onSubmit,
            enabled = !loading &&
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
