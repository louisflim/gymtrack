package edu.cit.lim.gymtrack.mobile.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import edu.cit.lim.gymtrack.mobile.ui.auth.AuthViewModel
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthErrorBanner
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthFooterText
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthShell
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthTextField
import edu.cit.lim.gymtrack.mobile.ui.components.auth.BrandStat

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.loginState.collectAsState()

    AuthShell(
        brandTitle = "TRAIN\nTRACK",
        brandHighlight = "TRANSFORM",
        brandTagline = "One system for memberships, attendance, and payments. Built for gyms that run on discipline, not paperwork.",
        brandStats = listOf(
            BrandStat("01", "Sign In"),
            BrandStat("02", "Scan QR"),
            BrandStat("03", "Train")
        ),
        formEyebrow = "GymTrack",
        formHeading = "Sign In",
        formSubheading = "Enter your credentials to access your account."
    ) {
        uiState.error?.let { AuthErrorBanner(message = it) }

        AuthTextField(
            label = "Email",
            value = email,
            onValueChange = {
                email = it
                viewModel.clearLoginError()
            },
            placeholder = "you@example.com",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )

        AuthTextField(
            label = "Password",
            value = password,
            onValueChange = {
                password = it
                viewModel.clearLoginError()
            },
            placeholder = "Enter your password",
            isPassword = true
        )

        AuthSubmitButton(
            text = if (uiState.isLoading) "Signing In..." else "Sign In",
            onClick = { viewModel.login(email, password, onLoginSuccess) },
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
        )

        AuthFooterText(
            message = "Don't have an account?",
            actionLabel = "Register",
            onActionClick = onNavigateToRegister
        )
    }
}
