package edu.cit.lim.gymtrack.mobile.feature.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class RegistrationViewModel(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        gymName: String = "",
        onSuccess: () -> Unit
    ) {
        if (password != confirmPassword) {
            _uiState.value = RegistrationUiState(error = "Passwords do not match.")
            return
        }

        if (role == "owner" && gymName.isBlank()) {
            _uiState.value = RegistrationUiState(error = "Gym name is required when registering as gym owner.")
            return
        }

        val backendRole = if (role == "owner") "ADMIN" else "MEMBER"

        viewModelScope.launch {
            _uiState.value = RegistrationUiState(isLoading = true)
            try {
                registrationRepository.register(
                    firstName,
                    lastName,
                    email,
                    password,
                    backendRole,
                    gymName.takeIf { role == "owner" }
                )
                _uiState.value = RegistrationUiState()
                onSuccess()
            } catch (e: AuthException) {
                _uiState.value = RegistrationUiState(error = e.message)
            } catch (e: Exception) {
                val networkMessage = ApiErrorParser.networkMessage(e)
                _uiState.value = RegistrationUiState(
                    error = networkMessage ?: "Something went wrong. Try again."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
