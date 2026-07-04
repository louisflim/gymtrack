package edu.cit.lim.gymtrack.mobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.cit.lim.gymtrack.mobile.data.model.*
import edu.cit.lim.gymtrack.mobile.data.repository.AttendanceRepository
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import edu.cit.lim.gymtrack.mobile.data.repository.AuthRepository
import edu.cit.lim.gymtrack.mobile.data.repository.GymRepository
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AccountFieldValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val qrImageBase64: String? = null,
    val gymQrImageBase64: String? = null,
    val loading: Boolean = false,
    val loadingGymQr: Boolean = false,
    val scanStatusMessage: String? = null,
    val memberGymScanStatus: String? = null,
    val staffForm: AccountFieldValues = AccountFieldValues(),
    val staffStatusMessage: String? = null,
    val creatingStaff: Boolean = false,
    val membership: MembershipResponse? = null,
    val activePlans: List<PlanResponse> = emptyList(),
    val myPayments: List<PaymentResponse> = emptyList(),
    val memberStatusMessage: String? = null,
    val subscribing: Boolean = false,
    val myAttendanceLogs: List<AttendanceLogResponse> = emptyList(),
    val loadingAttendance: Boolean = false,
    val staffRefreshKey: Int = 0
)

class DashboardViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository,
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(loading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadMemberDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val membership = gymRepository.myMembership()
                val plans = if (membership.gymId != null) gymRepository.activePlans() else emptyList()
                val payments = gymRepository.myPayments()
                val attendance = runCatching { attendanceRepository.getMyAttendance() }.getOrDefault(emptyList())
                val canShowQr = membership.nextStep == "FIRST_CHECK_IN" || membership.nextStep == "ACTIVE"
                val qrImage = if (canShowQr) {
                    runCatching { attendanceRepository.getMyQrCode().qrImageBase64 }.getOrNull()
                } else null
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    qrImageBase64 = qrImage,
                    membership = membership,
                    activePlans = plans,
                    myPayments = payments,
                    myAttendanceLogs = attendance,
                    loadingAttendance = false
                )
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(loading = false, memberStatusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, memberStatusMessage = "Failed to load member data.")
            }
        }
    }

    fun loadGymQr() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loadingGymQr = true)
            try {
                val qr = attendanceRepository.getGymQrCode()
                _uiState.value = _uiState.value.copy(loadingGymQr = false, gymQrImageBase64 = qr.qrImageBase64)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(loadingGymQr = false, gymQrImageBase64 = null)
            }
        }
    }

    fun subscribeToPlan(planId: Long, onCheckoutUrl: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(subscribing = true, memberStatusMessage = null)
            try {
                val checkout = gymRepository.checkout(planId)
                onCheckoutUrl(checkout.checkoutUrl)
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(memberStatusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(memberStatusMessage = "Checkout failed.")
            } finally {
                _uiState.value = _uiState.value.copy(subscribing = false)
            }
        }
    }

    fun confirmMockIfNeeded(url: String) {
        val reference = "reference=([^&]+)".toRegex().find(url)?.groupValues?.getOrNull(1) ?: return
        viewModelScope.launch {
            runCatching { gymRepository.confirmMockPayment(reference) }
            loadMemberDashboard()
        }
    }

    fun onMemberGymScan(qrData: String) {
        viewModelScope.launch {
            try {
                val result = attendanceRepository.scanGymQr(qrData)
                _uiState.value = _uiState.value.copy(memberGymScanStatus = result.message)
                loadMemberDashboard()
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(memberGymScanStatus = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(memberGymScanStatus = "QR scan failed.")
            }
        }
    }

    fun onStaffMemberScan(qrData: String) {
        viewModelScope.launch {
            try {
                val result = attendanceRepository.scanQr(qrData)
                _uiState.value = _uiState.value.copy(
                    scanStatusMessage = result.message.ifBlank {
                        "${result.memberName} (${result.planName ?: "No plan"}) — ${result.membershipStatus} — ${result.action}"
                    }
                )
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(scanStatusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(scanStatusMessage = "QR scan failed.")
            }
        }
    }

    fun onScanError(message: String, isMemberGymScan: Boolean) {
        if (isMemberGymScan) {
            _uiState.value = _uiState.value.copy(memberGymScanStatus = message)
        } else {
            _uiState.value = _uiState.value.copy(scanStatusMessage = message)
        }
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
            val session = authRepository.currentSession()
            if (session.gymName.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    staffStatusMessage = "Your admin account is not linked to a gym. Sign out and register again as Gym Owner with a gym name."
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(creatingStaff = true, staffStatusMessage = null)
            try {
                val created = authRepository.createStaff(form.firstName, form.lastName, form.email, form.password)
                _uiState.value = _uiState.value.copy(
                    creatingStaff = false,
                    staffForm = AccountFieldValues(),
                    staffStatusMessage = "Staff account created for ${created.email}.",
                    staffRefreshKey = _uiState.value.staffRefreshKey + 1
                )
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(creatingStaff = false, staffStatusMessage = e.message)
            } catch (e: Exception) {
                val networkMessage = ApiErrorParser.networkMessage(e)
                _uiState.value = _uiState.value.copy(
                    creatingStaff = false,
                    staffStatusMessage = networkMessage ?: "Failed to create staff account."
                )
            }
        }
    }

    class Factory(
        private val attendanceRepository: AttendanceRepository,
        private val authRepository: AuthRepository,
        private val gymRepository: GymRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(attendanceRepository, authRepository, gymRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
