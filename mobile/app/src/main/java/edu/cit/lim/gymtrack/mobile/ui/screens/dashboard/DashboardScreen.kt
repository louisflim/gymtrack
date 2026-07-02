package edu.cit.lim.gymtrack.mobile.ui.screens.dashboard

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import edu.cit.lim.gymtrack.mobile.ui.components.auth.AuthSubmitButton
import edu.cit.lim.gymtrack.mobile.ui.components.auth.BrandStat
import edu.cit.lim.gymtrack.mobile.ui.components.auth.DashboardBrandPanel
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.AttendanceScannerSection
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.CreateStaffForm
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardOverviewHeader
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.DashboardSummaryGrid
import edu.cit.lim.gymtrack.mobile.ui.components.dashboard.MemberQrCard
import edu.cit.lim.gymtrack.mobile.ui.components.qr.QrScannerOverlay
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground

@Composable
fun DashboardScreen(
    session: UserSession,
    dashboardViewModel: DashboardViewModel,
    onSignOut: () -> Unit
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val isMember = session.role == "MEMBER"
    val canScan = session.role == "ADMIN" || session.role == "STAFF"
    val isAdmin = session.role == "ADMIN"
    var scannerOpen by remember { mutableStateOf(false) }

    LaunchedEffect(isMember) {
        if (isMember) {
            dashboardViewModel.loadMemberQr()
        }
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x05FFFFFF), Color.Transparent)
                    )
                )
                .padding(horizontal = 28.dp, vertical = 40.dp)
        ) {
            DashboardOverviewHeader(
                firstName = session.firstName,
                lastName = session.lastName
            )

            Spacer(modifier = Modifier.height(28.dp))
            DashboardSummaryGrid(
                firstName = session.firstName,
                lastName = session.lastName,
                role = session.role
            )

            if (isMember) {
                Spacer(modifier = Modifier.height(20.dp))
                MemberQrCard(
                    qrImageBase64 = uiState.qrImageBase64,
                    loading = uiState.loading
                )
            }

            if (canScan) {
                Spacer(modifier = Modifier.height(20.dp))
                AttendanceScannerSection(
                    onOpenScanner = { scannerOpen = true },
                    statusMessage = uiState.scanStatusMessage
                )
            }

            if (isAdmin) {
                CreateStaffForm(
                    values = uiState.staffForm,
                    onFieldChange = dashboardViewModel::onStaffFieldChange,
                    onSubmit = dashboardViewModel::createStaff,
                    loading = uiState.creatingStaff,
                    statusMessage = uiState.staffStatusMessage
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            AuthSubmitButton(
                text = "Sign Out",
                onClick = onSignOut,
                enabled = true
            )
        }
    }

        if (scannerOpen) {
            BackHandler {
                scannerOpen = false
            }
            QrScannerOverlay(
                onScan = { payload ->
                    scannerOpen = false
                    dashboardViewModel.onScanResult(payload)
                },
                onClose = { scannerOpen = false }
            )
        }
    }
}
