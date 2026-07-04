package edu.cit.lim.gymtrack.mobile.ui.components.admin

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.components.common.KpiSummaryGrid
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.CreateStaffForm
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardStatusText
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.AdminTab
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.AdminViewModel
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.DashboardViewModel

@Composable
fun AdminDashboardSection(
    adminViewModel: AdminViewModel,
    dashboardViewModel: DashboardViewModel,
    adminGymName: String? = null,
    staffRefreshKey: Int = 0
) {
    val adminState by adminViewModel.uiState.collectAsState()
    val dashState by dashboardViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { adminViewModel.loadAll() }
    LaunchedEffect(staffRefreshKey) {
        if (staffRefreshKey > 0) adminViewModel.loadAll()
    }

    when (adminState.tab) {
        AdminTab.OVERVIEW -> {
            KpiSummaryGrid(items = adminViewModel.kpiItems())
            Spacer(modifier = Modifier.height(16.dp))
            CreateStaffForm(
                values = dashState.staffForm,
                onFieldChange = dashboardViewModel::onStaffFieldChange,
                onSubmit = dashboardViewModel::createStaff,
                loading = dashState.creatingStaff,
                statusMessage = dashState.staffStatusMessage,
                adminGymName = adminGymName
            )
        }
        AdminTab.PLANS -> {
            PlanListCard(plans = adminState.plans, onEdit = adminViewModel::editPlan)
            PlanFormCard(
                form = adminState.planForm,
                editing = adminState.editingPlanId != null,
                statusMessage = adminState.statusMessage,
                onFieldChange = adminViewModel::onPlanFieldChange,
                onSave = adminViewModel::savePlan
            )
        }
        AdminTab.MEMBERS -> MemberListCard(
            members = adminState.members,
            plans = adminState.plans.filter { it.active },
            search = adminState.memberSearch,
            onSearchChange = adminViewModel::setMemberSearch,
            status = adminState.memberStatus,
            onStatusChange = adminViewModel::setMemberStatus,
            onUpdate = adminViewModel::updateMember,
            onAssignPlan = adminViewModel::assignPlan
        )
        AdminTab.STAFF -> StaffListCard(
            staff = adminState.staff,
            onUpdate = adminViewModel::updateStaff
        )
        AdminTab.ATTENDANCE -> AttendanceLogCard(
            logs = adminState.attendanceLogs,
            search = adminState.attendanceSearch,
            onSearchChange = adminViewModel::setAttendanceSearch,
            date = adminState.attendanceDate,
            onDateChange = adminViewModel::setAttendanceDate
        )
        AdminTab.PAYMENTS -> PaymentListCard(payments = adminState.payments)
    }

    DashboardStatusText(message = adminState.statusMessage)
}
