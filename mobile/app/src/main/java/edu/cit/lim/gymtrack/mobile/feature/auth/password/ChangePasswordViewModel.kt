package edu.cit.lim.gymtrack.mobile.feature.auth.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChangePasswordViewModel(
    private val changePasswordRepository: ChangePasswordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        if (newPassword != confirmPassword) {
            _uiState.value = ChangePasswordUiState(error = "Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState(isLoading = true)
            try {
                changePasswordRepository.changePassword(currentPassword, newPassword, confirmPassword)
                _uiState.value = ChangePasswordUiState()
                onSuccess()
            } catch (e: AuthException) {
                _uiState.value = ChangePasswordUiState(error = e.message)
            } catch (e: Exception) {
                val networkMessage = ApiErrorParser.networkMessage(e)
                _uiState.value = ChangePasswordUiState(
                    error = networkMessage ?: ApiErrorParser.GENERIC
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
