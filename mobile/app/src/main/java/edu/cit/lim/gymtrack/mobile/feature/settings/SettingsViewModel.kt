package edu.cit.lim.gymtrack.mobile.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val passwordLoading: Boolean = false,
    val passwordError: String? = null,
    val passwordSuccess: String? = null,
    val deleteLoading: Boolean = false,
    val deleteError: String? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: (() -> Unit)? = null
    ) {
        if (newPassword != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                passwordError = "Passwords do not match.",
                passwordSuccess = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                passwordLoading = true,
                passwordError = null,
                passwordSuccess = null
            )
            try {
                settingsRepository.changePassword(currentPassword, newPassword, confirmPassword)
                _uiState.value = _uiState.value.copy(
                    passwordLoading = false,
                    passwordSuccess = "Password updated."
                )
                onSuccess?.invoke()
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(
                    passwordLoading = false,
                    passwordError = e.message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    passwordLoading = false,
                    passwordError = ApiErrorParser.networkMessage(e) ?: ApiErrorParser.GENERIC
                )
            }
        }
    }

    fun deleteAccount(password: String, onSuccess: () -> Unit) {
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                deleteError = "Please enter your password to delete your account."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(deleteLoading = true, deleteError = null)
            try {
                settingsRepository.deleteAccount(password)
                _uiState.value = _uiState.value.copy(deleteLoading = false)
                onSuccess()
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(
                    deleteLoading = false,
                    deleteError = e.message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    deleteLoading = false,
                    deleteError = ApiErrorParser.networkMessage(e) ?: ApiErrorParser.GENERIC
                )
            }
        }
    }

    fun clearPasswordMessages() {
        _uiState.value = _uiState.value.copy(passwordError = null, passwordSuccess = null)
    }

    fun clearDeleteError() {
        _uiState.value = _uiState.value.copy(deleteError = null)
    }
}
