package edu.cit.lim.gymtrack.mobile.ui.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import edu.cit.lim.gymtrack.mobile.ui.components.AuthErrorBanner
import edu.cit.lim.gymtrack.mobile.ui.components.AuthFooterText
import edu.cit.lim.gymtrack.mobile.ui.components.AuthRoleDropdown
import edu.cit.lim.gymtrack.mobile.ui.components.AuthShell
import edu.cit.lim.gymtrack.mobile.ui.components.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.AuthTextField
import edu.cit.lim.gymtrack.mobile.ui.components.BrandStat

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("member") }
    val uiState by viewModel.registerState.collectAsState()

    AuthShell(
        brandEyebrow = "New Member",
        brandTitle = "JOIN\nTHE",
        brandHighlight = "FLOOR",
        brandTagline = "Create your account to manage your membership, track attendance, and pay online — all in one place.",
        brandStats = listOf(
            BrandStat("01", "Register"),
            BrandStat("02", "Choose Plan"),
            BrandStat("03", "Get QR Code")
        ),
        formEyebrow = "GymTrack",
        formHeading = "Create Account",
        formSubheading = "Fill in your details to get started."
    ) {
        uiState.error?.let { AuthErrorBanner(message = it) }

        Row(modifier = Modifier.fillMaxWidth()) {
            AuthTextField(
                label = "First Name",
                value = firstName,
                onValueChange = {
                    firstName = it
                    viewModel.clearRegisterError()
                },
                placeholder = "First name",
                modifier = Modifier.weight(1f)
            )
            AuthTextField(
                label = "Last Name",
                value = lastName,
                onValueChange = {
                    lastName = it
                    viewModel.clearRegisterError()
                },
                placeholder = "Last name",
                modifier = Modifier.weight(1f)
            )
        }

        AuthTextField(
            label = "Email",
            value = email,
            onValueChange = {
                email = it
                viewModel.clearRegisterError()
            },
            placeholder = "you@example.com",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            AuthTextField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearRegisterError()
                },
                placeholder = "Create password",
                isPassword = true,
                modifier = Modifier.weight(1f)
            )
            AuthTextField(
                label = "Confirm",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    viewModel.clearRegisterError()
                },
                placeholder = "Repeat password",
                isPassword = true,
                modifier = Modifier.weight(1f)
            )
        }

        AuthRoleDropdown(
            selectedRole = role,
            onRoleSelected = {
                role = it
                viewModel.clearRegisterError()
            }
        )

        AuthSubmitButton(
            text = if (uiState.isLoading) "Creating Account..." else "Create Account",
            onClick = {
                viewModel.register(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    role = role,
                    onSuccess = onRegisterSuccess
                )
            },
            enabled = !uiState.isLoading &&
                firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank()
        )

        AuthFooterText(
            message = "Already have an account?",
            actionLabel = "Sign In",
            onActionClick = onNavigateToLogin
        )
    }
}
