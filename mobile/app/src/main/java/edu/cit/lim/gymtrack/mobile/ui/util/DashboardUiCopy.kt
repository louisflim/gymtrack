package edu.cit.lim.gymtrack.mobile.ui.util

import edu.cit.lim.gymtrack.mobile.ui.model.DashboardNavItem

/** Shared dashboard copy/config for mobile. Keep in sync with web/src/constants/dashboardUi.js */

object DashboardUiCopy {
    val memberQrNoteFirstCheckIn =
        "Show this QR code to staff at the front desk for your first check-in."
    val memberQrNoteActive =
        "Present this QR code to staff for attendance check-in/check-out."
    val memberQrLocked =
        "Complete enrollment and purchase a plan to unlock your member QR code."
    val staffHomeHint =
        "Use Gym QR to enroll members, then Scan to check them in and out."

    fun navTabsForRole(role: String): List<DashboardNavItem> = when (role) {
        "STAFF" -> listOf(
            DashboardNavItem("home", "Home"),
            DashboardNavItem("qr", "Gym QR"),
            DashboardNavItem("scan", "Scan"),
            DashboardNavItem("settings", "Settings")
        )
        "ADMIN" -> listOf(
            DashboardNavItem("home", "Home"),
            DashboardNavItem("qr", "QR"),
            DashboardNavItem("plans", "Plans"),
            DashboardNavItem("members", "Members"),
            DashboardNavItem("staff", "Staff"),
            DashboardNavItem("attendance", "Logs"),
            DashboardNavItem("payments", "Payments"),
            DashboardNavItem("settings", "Settings")
        )
        else -> listOf(
            DashboardNavItem("home", "Home"),
            DashboardNavItem("qr", "My QR"),
            DashboardNavItem("plans", "Plans"),
            DashboardNavItem("activity", "Activity"),
            DashboardNavItem("settings", "Settings")
        )
    }

    fun onboardingStep(nextStep: String?): Pair<String, String> = when (nextStep) {
        "PURCHASE_PLAN" -> "Step 2: Choose a subscription" to
            "You are enrolled. Pick a plan below and complete payment to activate your membership."
        "FIRST_CHECK_IN" -> "Step 3: First check-in" to
            "Your subscription is active. Show your member QR code below to staff at the front desk so they can scan you in."
        "ACTIVE" -> "You're all set" to
            "Show your member QR code below to staff for check-in and check-out on future visits."
        else -> "Step 1: Join this gym" to
            "Scan the QR code displayed by staff or admin at the front desk to enroll at their gym."
    }
}
