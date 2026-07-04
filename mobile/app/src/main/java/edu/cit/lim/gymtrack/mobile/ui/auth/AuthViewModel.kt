package edu.cit.lim.gymtrack.mobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import edu.cit.lim.gymtrack.mobile.data.repository.AuthRepository
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val session = authRepository.sessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), edu.cit.lim.gymtrack.mobile.data.model.UserSession())

    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(AuthUiState())
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginState.value = AuthUiState(isLoading = true)
            try {
                authRepository.login(email, password)
                _loginState.value = AuthUiState()
                onSuccess()
            } catch (e: AuthException) {
                _loginState.value = AuthUiState(error = e.message)
            } catch (e: Exception) {
                val networkMessage = ApiErrorParser.networkMessage(e)
                _loginState.value = AuthUiState(error = networkMessage ?: "Something went wrong. Try again.")
            }
        }
    }

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
            _registerState.value = AuthUiState(error = "Passwords do not match.")
            return
        }

        if (role == "owner" && gymName.isBlank()) {
            _registerState.value = AuthUiState(error = "Gym name is required when registering as gym owner.")
            return
        }

        val backendRole = if (role == "owner") "ADMIN" else "MEMBER"

        viewModelScope.launch {
            _registerState.value = AuthUiState(isLoading = true)
            try {
                authRepository.register(
                    firstName,
                    lastName,
                    email,
                    password,
                    backendRole,
                    gymName.takeIf { role == "owner" }
                )
                _registerState.value = AuthUiState()
                onSuccess()
            } catch (e: AuthException) {
                _registerState.value = AuthUiState(error = e.message)
            } catch (e: Exception) {
                val networkMessage = ApiErrorParser.networkMessage(e)
                _registerState.value = AuthUiState(error = networkMessage ?: "Something went wrong. Try again.")
            }
        }
    }

    fun clearLoginError() {
        _loginState.value = _loginState.value.copy(error = null)
    }

    fun clearRegisterError() {
        _registerState.value = _registerState.value.copy(error = null)
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
