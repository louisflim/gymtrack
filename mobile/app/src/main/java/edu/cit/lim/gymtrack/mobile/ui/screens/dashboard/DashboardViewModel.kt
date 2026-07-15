package edu.cit.lim.gymtrack.mobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.cit.lim.gymtrack.mobile.data.model.*
import edu.cit.lim.gymtrack.mobile.feature.attendance.AttendanceRepository
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import edu.cit.lim.gymtrack.mobile.data.repository.AuthRepository
import edu.cit.lim.gymtrack.mobile.feature.auth.staff.StaffCreationRepository
import edu.cit.lim.gymtrack.mobile.feature.membership.MembershipRepository
import edu.cit.lim.gymtrack.mobile.feature.payments.PaymentRepository
import edu.cit.lim.gymtrack.mobile.feature.plans.PlanRepository
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.ui.model.AccountFieldValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
    val staffRefreshKey: Int = 0,
    val pendingPaymentReference: String? = null,
    val pendingMockCheckout: Boolean = false
)

class DashboardViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository,
    private val staffCreationRepository: StaffCreationRepository,
    private val planRepository: PlanRepository,
    private val membershipRepository: MembershipRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(loading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadMemberDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val membership = membershipRepository.myMembership()
                val plans = if (membership.gymId != null) planRepository.activePlans() else emptyList()
                val payments = paymentRepository.myPayments()
                val attendance = runCatching { attendanceRepository.getMyAttendance() }.getOrDefault(emptyList())
                val canShowQr = membership.nextStep == "FIRST_CHECK_IN" || membership.nextStep == "ACTIVE"
                val qrImage = if (canShowQr) {
                    runCatching { fetchMemberQrImage() }.getOrNull()
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
                _uiState.value = _uiState.value.copy(loading = false, memberStatusMessage = "We couldn't load your membership details. Please try again.")
            }
        }
    }

    fun loadGymQr() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loadingGymQr = true)
            try {
                val qr = attendanceRepository.getGymQrCode()
                _uiState.value = _uiState.value.copy(loadingGymQr = false, gymQrImageBase64 = qr.qrImageBase64)
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(
                    loadingGymQr = false,
                    gymQrImageBase64 = null,
                    scanStatusMessage = e.message
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    loadingGymQr = false,
                    gymQrImageBase64 = null,
                    scanStatusMessage = "We couldn't load the gym QR code. Please try again."
                )
            }
        }
    }

    fun loadMemberQr() {
        val membership = _uiState.value.membership
        val canShowQr = membership?.nextStep == "FIRST_CHECK_IN" || membership?.nextStep == "ACTIVE"
        if (!canShowQr) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, memberStatusMessage = null)
            try {
                val qrImage = fetchMemberQrImage()
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    qrImageBase64 = qrImage,
                    memberStatusMessage = if (qrImage == null) "We couldn't load your QR code. Please try again." else null
                )
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    qrImageBase64 = null,
                    memberStatusMessage = e.message
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    qrImageBase64 = null,
                    memberStatusMessage = "We couldn't load your QR code. Please try again."
                )
            }
        }
    }

    private suspend fun fetchMemberQrImage(): String? {
        return attendanceRepository.getMyQrCode().qrImageBase64
    }

    fun subscribeToPlan(planId: Long, onCheckoutUrl: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(subscribing = true, memberStatusMessage = null)
            try {
                val checkout = paymentRepository.checkout(planId)
                _uiState.value = _uiState.value.copy(
                    subscribing = false,
                    pendingPaymentReference = checkout.reference,
                    pendingMockCheckout = checkout.mockCheckout
                )
                if (checkout.mockCheckout) {
                    // Confirm in-app — do not open localhost web return URLs on the phone.
                    syncPendingPayment()
                } else {
                    onCheckoutUrl(checkout.checkoutUrl)
                }
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(subscribing = false, memberStatusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(subscribing = false, memberStatusMessage = "Checkout failed.")
            }
        }
    }

    fun syncPendingPayment() {
        val reference = _uiState.value.pendingPaymentReference ?: return
        viewModelScope.launch {
            try {
                val initial = paymentRepository.paymentStatus(reference)
                if (initial.mockCheckout && !initial.paid) {
                    paymentRepository.confirmMockPayment(reference)
                } else if (!initial.paid) {
                    var paid = false
                    repeat(10) {
                        if (paid) return@repeat
                        val latest = paymentRepository.paymentStatus(reference)
                        if (latest.paid) {
                            paid = true
                            return@repeat
                        }
                        delay(2000)
                    }
                }
                _uiState.value = _uiState.value.copy(
                    pendingPaymentReference = null,
                    pendingMockCheckout = false,
                    memberStatusMessage = "Payment confirmed."
                )
                loadMemberDashboard()
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(memberStatusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    memberStatusMessage = "Payment confirmation is still pending."
                )
            }
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
                    staffStatusMessage = "Your account isn't connected to a gym yet. Sign out, then create a new account as Gym Owner and enter your gym name."
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(creatingStaff = true, staffStatusMessage = null)
            try {
                val created = staffCreationRepository.createStaff(form.firstName, form.lastName, form.email, form.password)
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
                    staffStatusMessage = networkMessage ?: "We couldn't create the staff account. Please try again."
                )
            }
        }
    }

    class Factory(
        private val attendanceRepository: AttendanceRepository,
        private val authRepository: AuthRepository,
        private val staffCreationRepository: StaffCreationRepository,
        private val planRepository: PlanRepository,
        private val membershipRepository: MembershipRepository,
        private val paymentRepository: PaymentRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(
                    attendanceRepository,
                    authRepository,
                    staffCreationRepository,
                    planRepository,
                    membershipRepository,
                    paymentRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
