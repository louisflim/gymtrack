package edu.cit.lim.gymtrack.mobile.ui.screens.dashboard

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import edu.cit.lim.gymtrack.mobile.ui.components.admin.AdminDashboardSection
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.auth.BrandStat
import edu.cit.lim.gymtrack.mobile.ui.components.auth.DashboardBrandPanel
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.AttendanceScannerSection
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardNavBar
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardOverviewHeader
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSummaryGrid
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.GymQrCard
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.MemberQrCard
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardStatusText
import edu.cit.lim.gymtrack.mobile.ui.components.member.AttendanceHistoryCard
import edu.cit.lim.gymtrack.mobile.ui.components.member.MemberOnboardingCard
import edu.cit.lim.gymtrack.mobile.ui.components.member.MembershipCard
import edu.cit.lim.gymtrack.mobile.ui.components.member.PaymentHistoryCard
import edu.cit.lim.gymtrack.mobile.ui.components.member.PlanPickerCard
import edu.cit.lim.gymtrack.mobile.ui.components.qr.QrScannerOverlay
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground
import edu.cit.lim.gymtrack.mobile.ui.util.DashboardUiCopy

@Composable
fun DashboardScreen(
    session: UserSession,
    dashboardViewModel: DashboardViewModel,
    adminViewModel: AdminViewModel,
    onSignOut: () -> Unit
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isMember = session.role == "MEMBER"
    val canScan = session.role == "ADMIN" || session.role == "STAFF"
    val isAdmin = session.role == "ADMIN"
    val isStaff = session.role == "STAFF"
    var scannerOpen by remember { mutableStateOf(false) }
    var scannerMode by remember { mutableStateOf("member") }
    var activeTab by remember { mutableStateOf("home") }

    fun selectTab(tab: String) {
        activeTab = tab
        if (isAdmin) {
            val adminTab = when (tab) {
                "plans" -> AdminTab.PLANS
                "members" -> AdminTab.MEMBERS
                "staff" -> AdminTab.STAFF
                "attendance" -> AdminTab.ATTENDANCE
                "payments" -> AdminTab.PAYMENTS
                else -> AdminTab.OVERVIEW
            }
            adminViewModel.setTab(adminTab)
        }
    }

    val browserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        dashboardViewModel.loadMemberDashboard()
    }

    LaunchedEffect(isMember) {
        if (isMember) dashboardViewModel.loadMemberDashboard()
    }

    LaunchedEffect(canScan) {
        if (canScan) dashboardViewModel.loadGymQr()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GymTrackBackground)
                .verticalScroll(rememberScrollState())
        ) {
            DashboardBrandPanel(
                eyebrow = "GymTrack Dashboard",
                title = "TRAIN\nTRACK",
                highlight = "THRIVE",
                tagline = "Monitor your gym access, membership details, and activity from one place.",
                stats = listOf(
                    BrandStat("01", "Profile"),
                    BrandStat("02", "Membership"),
                    BrandStat("03", "Access")
                )
            )

            DashboardNavBar(
                role = session.role,
                activeTab = activeTab,
                onTabSelected = ::selectTab
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(colors = listOf(Color(0x05FFFFFF), Color.Transparent)))
                    .padding(horizontal = 28.dp, vertical = 40.dp)
            ) {
                DashboardOverviewHeader(firstName = session.firstName, lastName = session.lastName)
                Spacer(modifier = Modifier.height(20.dp))

                if (activeTab == "home") {
                    DashboardSummaryGrid(
                        firstName = session.firstName,
                        lastName = session.lastName,
                        role = session.role
                    )
                }

                if (isMember && activeTab == "home") {
                    Spacer(modifier = Modifier.height(20.dp))
                    MemberOnboardingCard(
                        membership = uiState.membership,
                        onOpenScanner = {
                            scannerMode = "gym"
                            scannerOpen = true
                        },
                        statusMessage = uiState.memberGymScanStatus
                    )
                }

                if (isMember && activeTab == "qr") {
                    val showMemberQr = uiState.membership?.nextStep == "FIRST_CHECK_IN"
                        || uiState.membership?.nextStep == "ACTIVE"
                    if (showMemberQr) {
                        MemberQrCard(
                            qrImageBase64 = uiState.qrImageBase64,
                            loading = uiState.loading,
                            note = if (uiState.membership?.nextStep == "FIRST_CHECK_IN") {
                                DashboardUiCopy.memberQrNoteFirstCheckIn
                            } else {
                                DashboardUiCopy.memberQrNoteActive
                            }
                        )
                    } else {
                        DashboardStatusText(message = DashboardUiCopy.memberQrLocked)
                    }
                }

                if (isMember && activeTab == "plans") {
                    MembershipCard(membership = uiState.membership)
                    PlanPickerCard(
                        plans = uiState.activePlans,
                        subscribing = uiState.subscribing,
                        statusMessage = uiState.memberStatusMessage,
                        enrolled = uiState.membership?.gymId != null,
                        onSubscribe = { planId ->
                            dashboardViewModel.subscribeToPlan(planId) { url ->
                                val intent = CustomTabsIntent.Builder().build()
                                browserLauncher.launch(intent.intent.apply { data = Uri.parse(url) })
                            }
                        }
                    )
                }

                if (isMember && activeTab == "activity") {
                    PaymentHistoryCard(payments = uiState.myPayments)
                    AttendanceHistoryCard(logs = uiState.myAttendanceLogs, loading = uiState.loading)
                }

                if (canScan && activeTab == "qr") {
                    GymQrCard(qrImageBase64 = uiState.gymQrImageBase64, loading = uiState.loadingGymQr)
                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AttendanceScannerSection(
                            onOpenScanner = {
                                scannerMode = "member"
                                scannerOpen = true
                            },
                            statusMessage = uiState.scanStatusMessage
                        )
                    }
                }

                if (isStaff && activeTab == "scan") {
                    AttendanceScannerSection(
                        onOpenScanner = {
                            scannerMode = "member"
                            scannerOpen = true
                        },
                        statusMessage = uiState.scanStatusMessage
                    )
                }

                if (isStaff && activeTab == "home") {
                    Spacer(modifier = Modifier.height(12.dp))
                    DashboardStatusText(message = DashboardUiCopy.staffHomeHint)
                }

                if (isAdmin && activeTab != "qr") {
                    Spacer(modifier = Modifier.height(20.dp))
                    AdminDashboardSection(
                        adminViewModel = adminViewModel,
                        dashboardViewModel = dashboardViewModel,
                        adminGymName = session.gymName,
                        staffRefreshKey = uiState.staffRefreshKey
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
                AuthSubmitButton(text = "Sign Out", onClick = onSignOut, enabled = true)
            }
        }

        if (scannerOpen) {
            BackHandler { scannerOpen = false }
            QrScannerOverlay(
                onScan = { payload ->
                    scannerOpen = false
                    if (scannerMode == "gym") dashboardViewModel.onMemberGymScan(payload)
                    else dashboardViewModel.onStaffMemberScan(payload)
                },
                onClose = { scannerOpen = false }
            )
        }
    }
}
