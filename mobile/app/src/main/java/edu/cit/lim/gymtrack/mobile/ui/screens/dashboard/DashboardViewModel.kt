package edu.cit.lim.gymtrack.mobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.cit.lim.gymtrack.mobile.data.repository.AttendanceRepository
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import edu.cit.lim.gymtrack.mobile.data.repository.AuthRepository
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AccountFieldValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val qrImageBase64: String? = null,
    val loading: Boolean = false,
    val scanStatusMessage: String? = null,
    val staffForm: AccountFieldValues = AccountFieldValues(),
    val staffStatusMessage: String? = null,
    val creatingStaff: Boolean = false
)

class DashboardViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(loading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadMemberQr() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val qr = attendanceRepository.getMyQrCode()
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    qrImageBase64 = qr.qrImageBase64
                )
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(loading = false, scanStatusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, scanStatusMessage = "Failed to load dashboard data.")
            }
        }
    }

    fun onScanResult(qrData: String) {
        viewModelScope.launch {
            try {
                val result = attendanceRepository.scanQr(qrData)
                _uiState.value = _uiState.value.copy(
                    scanStatusMessage = "${result.memberName}: ${result.message}"
                )
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(scanStatusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(scanStatusMessage = "QR scan failed.")
            }
        }
    }

    fun onScanError(message: String) {
        _uiState.value = _uiState.value.copy(scanStatusMessage = message)
    }

    fun onStaffFieldChange(field: String, value: String) {
        val current = _uiState.value.staffForm
        val updated = when (field) {
            "firstName" -> current.copy(firstName = value)
            "lastName" -> current.copy(lastName = value)
            "email" -> current.copy(email = value)
            "password" -> current.copy(password = value)
            "confirmPassword" -> current.copy(confirmPassword = value)
            else -> current
        }
        _uiState.value = _uiState.value.copy(staffForm = updated, staffStatusMessage = null)
    }

    fun createStaff() {
        val form = _uiState.value.staffForm
        if (form.password != form.confirmPassword) {
            _uiState.value = _uiState.value.copy(staffStatusMessage = "Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(creatingStaff = true, staffStatusMessage = null)
            try {
                val created = authRepository.createStaff(
                    firstName = form.firstName,
                    lastName = form.lastName,
                    email = form.email,
                    password = form.password
                )
                _uiState.value = _uiState.value.copy(
                    creatingStaff = false,
                    staffForm = AccountFieldValues(),
                    staffStatusMessage = "Staff account created for ${created.email}."
                )
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(
                    creatingStaff = false,
                    staffStatusMessage = e.message
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    creatingStaff = false,
                    staffStatusMessage = "Failed to create staff account."
                )
            }
        }
    }

    class Factory(
        private val attendanceRepository: AttendanceRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(attendanceRepository, authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
