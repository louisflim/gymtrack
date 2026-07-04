package edu.cit.lim.gymtrack.mobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.cit.lim.gymtrack.mobile.data.model.*
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import edu.cit.lim.gymtrack.mobile.data.repository.GymRepository
import edu.cit.lim.gymtrack.mobile.ui.components.common.KpiItem
import edu.cit.lim.gymtrack.mobile.ui.util.formatCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class AdminUiState(
    val tab: AdminTab = AdminTab.OVERVIEW,
    val plans: List<PlanResponse> = emptyList(),
    val members: List<MemberResponse> = emptyList(),
    val payments: List<PaymentResponse> = emptyList(),
    val staff: List<StaffResponse> = emptyList(),
    val stats: DashboardStatsResponse? = null,
    val attendanceLogs: List<AttendanceLogResponse> = emptyList(),
    val memberSearch: String = "",
    val memberStatus: String = "ALL",
    val attendanceSearch: String = "",
    val attendanceDate: String = "",
    val planForm: PlanFormState = PlanFormState(),
    val editingPlanId: Long? = null,
    val statusMessage: String? = null,
    val loading: Boolean = false
)

enum class AdminTab { OVERVIEW, PLANS, MEMBERS, STAFF, ATTENDANCE, PAYMENTS }

data class PlanFormState(
    val name: String = "",
    val durationDays: String = "30",
    val price: String = "999",
    val active: Boolean = true
)

class AdminViewModel(private val gymRepository: GymRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun setTab(tab: AdminTab) {
        _uiState.value = _uiState.value.copy(tab = tab)
        if (tab == AdminTab.ATTENDANCE) {
            loadAttendance()
        }
    }

    fun setMemberSearch(value: String) {
        _uiState.value = _uiState.value.copy(memberSearch = value)
    }

    fun setMemberStatus(value: String) {
        _uiState.value = _uiState.value.copy(memberStatus = value)
    }

    fun setAttendanceSearch(value: String) {
        _uiState.value = _uiState.value.copy(attendanceSearch = value)
        loadAttendance()
    }

    fun setAttendanceDate(value: String) {
        _uiState.value = _uiState.value.copy(attendanceDate = value)
        loadAttendance()
    }

    fun kpiItems(): List<KpiItem> {
        val stats = _uiState.value.stats ?: return emptyList()
        return listOf(
            KpiItem("Total Members", stats.totalMembers.toString()),
            KpiItem("Active Subscriptions", stats.activeSubscriptions.toString()),
            KpiItem("Expired", stats.expiredMemberships.toString()),
            KpiItem("Today's Check-ins", stats.todayCheckIns.toString()),
            KpiItem("Total Collected", formatCurrency(stats.totalPaymentsCollected))
        )
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val plans = gymRepository.allPlans()
                val members = gymRepository.members()
                val payments = gymRepository.allPayments()
                val staff = gymRepository.staff()
                val stats = gymRepository.dashboardStats()
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    plans = plans,
                    members = members,
                    payments = payments,
                    staff = staff,
                    stats = stats
                )
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(loading = false, statusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, statusMessage = "Failed to load admin data.")
            }
        }
    }

    fun loadAttendance() {
        viewModelScope.launch {
            val state = _uiState.value
            try {
                val logs = gymRepository.gymAttendance(
                    state.attendanceSearch.takeIf { it.isNotBlank() },
                    state.attendanceDate.takeIf { it.isNotBlank() }
                )
                _uiState.value = _uiState.value.copy(attendanceLogs = logs)
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(statusMessage = e.message)
            }
        }
    }

    fun onPlanFieldChange(field: String, value: String) {
        val form = _uiState.value.planForm
        val updated = when (field) {
            "name" -> form.copy(name = value)
            "durationDays" -> form.copy(durationDays = value)
            "price" -> form.copy(price = value)
            "active" -> form.copy(active = value.toBooleanStrictOrNull() ?: form.active)
            else -> form
        }
        _uiState.value = _uiState.value.copy(planForm = updated, statusMessage = null)
    }

    fun editPlan(plan: PlanResponse) {
        _uiState.value = _uiState.value.copy(
            tab = AdminTab.PLANS,
            editingPlanId = plan.id,
            planForm = PlanFormState(plan.name, plan.durationDays.toString(), plan.price.toPlainString(), plan.active)
        )
    }

    fun savePlan() {
        val form = _uiState.value.planForm
        viewModelScope.launch {
            try {
                val request = PlanRequest(form.name, form.durationDays.toInt(), BigDecimal(form.price), form.active)
                if (_uiState.value.editingPlanId != null) {
                    gymRepository.updatePlan(_uiState.value.editingPlanId!!, request)
                } else {
                    gymRepository.createPlan(request)
                }
                _uiState.value = _uiState.value.copy(
                    planForm = PlanFormState(),
                    editingPlanId = null,
                    statusMessage = "Plan saved."
                )
                loadAll()
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(statusMessage = e.message)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(statusMessage = "Failed to save plan.")
            }
        }
    }

    fun assignPlan(memberId: Long, planId: Long) {
        viewModelScope.launch {
            try {
                gymRepository.assignPlan(memberId, planId)
                _uiState.value = _uiState.value.copy(statusMessage = "Plan assigned.")
                loadAll()
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(statusMessage = e.message)
            }
        }
    }

    fun updateMember(id: Long, request: MemberUpdateRequest) {
        viewModelScope.launch {
            try {
                gymRepository.updateMember(id, request)
                _uiState.value = _uiState.value.copy(statusMessage = "Member updated.")
                loadAll()
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(statusMessage = e.message)
            }
        }
    }

    fun updateStaff(id: Long, request: StaffUpdateRequest) {
        viewModelScope.launch {
            try {
                gymRepository.updateStaff(id, request)
                _uiState.value = _uiState.value.copy(statusMessage = "Staff updated.")
                loadAll()
            } catch (e: AuthException) {
                _uiState.value = _uiState.value.copy(statusMessage = e.message)
            }
        }
    }

    class Factory(private val gymRepository: GymRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
                return AdminViewModel(gymRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
