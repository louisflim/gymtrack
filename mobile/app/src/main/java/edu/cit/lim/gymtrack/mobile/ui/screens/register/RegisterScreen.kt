package edu.cit.lim.gymtrack.mobile.ui.screens.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import edu.cit.lim.gymtrack.mobile.ui.auth.AuthViewModel
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AccountFieldValues
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AccountFields
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthErrorBanner
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthFooterText
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthRoleDropdown
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthShell
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthTextField
import edu.cit.lim.gymtrack.mobile.ui.components.auth.BrandStat

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
    var gymName by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.registerState.collectAsState()

    val form = AccountFieldValues(firstName, lastName, email, password, confirmPassword)

    fun onFieldChange(field: String, value: String) {
        when (field) {
            "firstName" -> firstName = value
            "lastName" -> lastName = value
            "email" -> email = value
            "password" -> password = value
            "confirmPassword" -> confirmPassword = value
        }
        viewModel.clearRegisterError()
    }

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

        AccountFields(values = form, onFieldChange = ::onFieldChange)

        AuthRoleDropdown(
            selectedRole = role,
            onRoleSelected = {
                role = it
                viewModel.clearRegisterError()
            }
        )

        if (role == "owner") {
            AuthTextField(
                label = "Gym Name",
                value = gymName,
                onValueChange = {
                    gymName = it
                    viewModel.clearRegisterError()
                },
                placeholder = "e.g. Chuchu Gym"
            )
        }

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
                    gymName = gymName,
                    onSuccess = onRegisterSuccess
                )
            },
            enabled = !uiState.isLoading &&
                firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                (role != "owner" || gymName.isNotBlank())
        )

        AuthFooterText(
            message = "Already have an account?",
            actionLabel = "Sign In",
            onActionClick = onNavigateToLogin
        )
    }
}
