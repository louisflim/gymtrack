/** Shared dashboard copy/config for web. Keep in sync with mobile DashboardUiCopy.kt */

export const NAV_TABS_BY_ROLE = {
  MEMBER: [
    { id: "home", label: "Home", icon: "⌂" },
    { id: "qr", label: "My QR", icon: "▣" },
    { id: "plans", label: "Plans", icon: "☰" },
    { id: "activity", label: "Activity", icon: "◷" },
  ],
  STAFF: [
    { id: "home", label: "Home", icon: "⌂" },
    { id: "qr", label: "Gym QR", icon: "▣" },
    { id: "scan", label: "Scan", icon: "⌖" },
  ],
  ADMIN: [
    { id: "home", label: "Home", icon: "⌂" },
    { id: "qr", label: "QR", icon: "▣" },
    { id: "plans", label: "Plans", icon: "☰" },
    { id: "members", label: "Members", icon: "☺" },
    { id: "staff", label: "Staff", icon: "⚙" },
    { id: "attendance", label: "Logs", icon: "◷" },
    { id: "payments", label: "Pay", icon: "₱" },
  ],
};

export const ONBOARDING_STEPS = {
  ENROLL_AT_GYM: {
    title: "Step 1: Join this gym",
    body: "Scan the QR code displayed by staff or admin at the front desk to enroll at their gym.",
  },
  PURCHASE_PLAN: {
    title: "Step 2: Choose a subscription",
    body: "You are enrolled. Pick a plan below and complete payment to activate your membership.",
  },
  FIRST_CHECK_IN: {
    title: "Step 3: First check-in",
    body: "Your subscription is active. Show your member QR code below to staff at the front desk so they can scan you in.",
  },
  ACTIVE: {
    title: "You're all set",
    body: "Show your member QR code below to staff for check-in and check-out on future visits.",
  },
};

export const MEMBER_QR_NOTES = {
  FIRST_CHECK_IN: "Show this QR code to staff at the front desk for your first check-in.",
  ACTIVE: "Present this QR code to staff for attendance check-in/check-out.",
  LOCKED: "Complete enrollment and purchase a plan to unlock your member QR code.",
};

export const STAFF_HOME_HINT =
  "Use Gym QR to enroll members, then Scan to check them in and out.";
