package edu.cit.lim.gymtrack.mobile.ui.components.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

data class AccountFieldValues(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

@Composable
fun AccountFields(
    values: AccountFieldValues,
    onFieldChange: (String, String) -> Unit,
    passwordPlaceholder: String = "Create password"
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AuthTextField(
            label = "First Name",
            value = values.firstName,
            onValueChange = { onFieldChange("firstName", it) },
            placeholder = "First name",
            modifier = Modifier.weight(1f)
        )
        AuthTextField(
            label = "Last Name",
            value = values.lastName,
            onValueChange = { onFieldChange("lastName", it) },
            placeholder = "Last name",
            modifier = Modifier.weight(1f)
        )
    }

    AuthTextField(
        label = "Email",
        value = values.email,
        onValueChange = { onFieldChange("email", it) },
        placeholder = "you@example.com",
        keyboardType = KeyboardType.Email
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        AuthTextField(
            label = "Password",
            value = values.password,
            onValueChange = { onFieldChange("password", it) },
            placeholder = passwordPlaceholder,
            isPassword = true,
            modifier = Modifier.weight(1f)
        )
        AuthTextField(
            label = "Confirm",
            value = values.confirmPassword,
            onValueChange = { onFieldChange("confirmPassword", it) },
            placeholder = "Repeat password",
            isPassword = true,
            modifier = Modifier.weight(1f)
        )
    }
}
