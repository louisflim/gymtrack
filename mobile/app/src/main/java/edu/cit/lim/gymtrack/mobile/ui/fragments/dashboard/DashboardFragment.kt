package edu.cit.lim.gymtrack.mobile.ui.fragments.dashboard

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.cit.lim.gymtrack.mobile.GymTrackApplication
import edu.cit.lim.gymtrack.mobile.R
import edu.cit.lim.gymtrack.mobile.data.model.MemberResponse
import edu.cit.lim.gymtrack.mobile.data.model.MemberUpdateRequest
import edu.cit.lim.gymtrack.mobile.data.model.PlanResponse
import edu.cit.lim.gymtrack.mobile.data.model.StaffResponse
import edu.cit.lim.gymtrack.mobile.data.model.StaffUpdateRequest
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import edu.cit.lim.gymtrack.mobile.databinding.FragmentDashboardBinding
import edu.cit.lim.gymtrack.mobile.ui.adapters.AttendanceLogAdapter
import edu.cit.lim.gymtrack.mobile.ui.adapters.KpiAdapter
import edu.cit.lim.gymtrack.mobile.ui.adapters.MemberAdapter
import edu.cit.lim.gymtrack.mobile.ui.adapters.PaymentAdapter
import edu.cit.lim.gymtrack.mobile.ui.adapters.PlanAdapter
import edu.cit.lim.gymtrack.mobile.ui.adapters.PlanAdminAdapter
import edu.cit.lim.gymtrack.mobile.ui.adapters.StaffAdapter
import edu.cit.lim.gymtrack.mobile.ui.auth.AuthViewModel
import edu.cit.lim.gymtrack.mobile.feature.attendance.QrScannerDialogFragment
import edu.cit.lim.gymtrack.mobile.feature.settings.SettingsViewModel
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.AdminTab
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.AdminViewModel
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.DashboardViewModel
import edu.cit.lim.gymtrack.mobile.ui.util.AdminViewModelFactory
import edu.cit.lim.gymtrack.mobile.ui.util.AuthViewModelFactory
import edu.cit.lim.gymtrack.mobile.ui.util.BrandStatData
import edu.cit.lim.gymtrack.mobile.ui.util.DashboardUiCopy
import edu.cit.lim.gymtrack.mobile.ui.util.DashboardViewModelFactory
import edu.cit.lim.gymtrack.mobile.ui.util.SettingsViewModelFactory
import edu.cit.lim.gymtrack.mobile.ui.util.applyStatusBadge
import edu.cit.lim.gymtrack.mobile.ui.util.loadBase64Qr
import edu.cit.lim.gymtrack.mobile.ui.util.setupNavTabs
import edu.cit.lim.gymtrack.mobile.ui.util.showError
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(requireActivity().application as GymTrackApplication)
    }
    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(requireActivity().application as GymTrackApplication)
    }
    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(requireActivity().application as GymTrackApplication)
    }
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(requireActivity().application as GymTrackApplication)
    }

    private var activeTab = "home"
    private var session = UserSession()
    private var syncingPlanForm = false
    private var syncingStaffForm = false

    private lateinit var planAdapter: PlanAdapter
    private val paymentAdapter = PaymentAdapter()
    private val attendanceAdapter = AttendanceLogAdapter()
    private val adminAttendanceAdapter = AttendanceLogAdapter()
    private val kpiAdapter = KpiAdapter()
    private lateinit var planAdminAdapter: PlanAdminAdapter
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var staffAdapter: StaffAdapter
    private val adminPaymentAdapter = PaymentAdapter()

    private val browserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        dashboardViewModel.syncPendingPayment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeroStats()
        setupRecyclerViews()
        setupMemberStatusChips()
        setupListeners()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.syncPendingPayment()
    }

    private fun setupHeroStats() {
        val statsRow = binding.dashboardHero.dashboardHeroStats
        statsRow.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        listOf(
            BrandStatData("01", "Profile"),
            BrandStatData("02", "Membership"),
            BrandStatData("03", "Access")
        ).forEach { stat ->
            val item = edu.cit.lim.gymtrack.mobile.databinding.ItemBrandStatBinding
                .inflate(inflater, statsRow, false)
            item.statValue.text = stat.value
            item.statLabel.text = stat.label.uppercase()
            statsRow.addView(item.root)
        }
    }

    private fun setupRecyclerViews() {
        planAdapter = PlanAdapter(subscribing = false) { planId ->
            dashboardViewModel.subscribeToPlan(planId) { url ->
                val intent = CustomTabsIntent.Builder().build()
                browserLauncher.launch(intent.intent.apply { data = Uri.parse(url) })
            }
        }
        binding.plansRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.plansRecycler.adapter = planAdapter

        binding.paymentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.paymentsRecycler.adapter = paymentAdapter

        binding.attendanceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.attendanceRecycler.adapter = attendanceAdapter

        binding.kpiRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.kpiRecycler.adapter = kpiAdapter

        planAdminAdapter = PlanAdminAdapter { plan -> adminViewModel.editPlan(plan) }
        binding.adminPlansRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.adminPlansRecycler.adapter = planAdminAdapter

        memberAdapter = MemberAdapter(
            onEdit = { member -> showMemberEditDialog(member) },
            onAssignPlan = { member -> showAssignPlanDialog(member) },
            onDelete = { member -> confirmDeleteMember(member) }
        )
        binding.membersRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.membersRecycler.adapter = memberAdapter

        staffAdapter = StaffAdapter { staff -> showStaffEditDialog(staff) }
        binding.staffRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.staffRecycler.adapter = staffAdapter

        binding.adminPaymentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.adminPaymentsRecycler.adapter = adminPaymentAdapter

        binding.adminAttendanceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.adminAttendanceRecycler.adapter = adminAttendanceAdapter
    }

    private fun setupMemberStatusChips() {
        val options = listOf(
            "ALL" to "All",
            "ACTIVE" to "Active",
            "EXPIRING_SOON" to "Expiring",
            "EXPIRED" to "Expired",
            "NONE" to "None"
        )
        options.forEach { (value, label) ->
            val chip = Chip(requireContext()).apply {
                text = label
                isCheckable = true
                isChecked = value == "ALL"
                setOnCheckedChangeListener { _, checked ->
                    if (checked) adminViewModel.setMemberStatus(value)
                }
            }
            binding.memberStatusChips.addView(chip)
        }
    }

    private fun setupListeners() {
        binding.signOutButton.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_dashboard_to_login)
            }
        }

        setupSettingsListeners()

        binding.memberOnboarding.scanGymButton.setOnClickListener {
            openScanner("gym")
        }

        binding.adminScannerSection.openScannerButton.setOnClickListener {
            openScanner("member")
        }
        binding.staffScannerSection.openScannerButton.setOnClickListener {
            openScanner("member")
        }

        binding.createStaffForm.staffFirstName.doAfterTextChanged {
            if (!syncingStaffForm) dashboardViewModel.onStaffFieldChange("firstName", it?.toString().orEmpty())
        }
        binding.createStaffForm.staffLastName.doAfterTextChanged {
            if (!syncingStaffForm) dashboardViewModel.onStaffFieldChange("lastName", it?.toString().orEmpty())
        }
        binding.createStaffForm.staffEmail.doAfterTextChanged {
            if (!syncingStaffForm) dashboardViewModel.onStaffFieldChange("email", it?.toString().orEmpty())
        }
        binding.createStaffForm.staffPassword.doAfterTextChanged {
            if (!syncingStaffForm) dashboardViewModel.onStaffFieldChange("password", it?.toString().orEmpty())
        }
        binding.createStaffForm.staffConfirmPassword.doAfterTextChanged {
            if (!syncingStaffForm) dashboardViewModel.onStaffFieldChange("confirmPassword", it?.toString().orEmpty())
        }
        binding.createStaffForm.createStaffButton.setOnClickListener {
            dashboardViewModel.createStaff()
        }

        binding.planForm.planNameInput.doAfterTextChanged {
            if (!syncingPlanForm) adminViewModel.onPlanFieldChange("name", it?.toString().orEmpty())
        }
        binding.planForm.planDurationInput.doAfterTextChanged {
            if (!syncingPlanForm) adminViewModel.onPlanFieldChange("durationDays", it?.toString().orEmpty())
        }
        binding.planForm.planPriceInput.doAfterTextChanged {
            if (!syncingPlanForm) adminViewModel.onPlanFieldChange("price", it?.toString().orEmpty())
        }
        binding.planForm.savePlanButton.setOnClickListener {
            adminViewModel.savePlan()
        }

        binding.memberSearchInput.doAfterTextChanged {
            adminViewModel.setMemberSearch(it?.toString().orEmpty())
        }
        binding.attendanceSearchInput.doAfterTextChanged {
            adminViewModel.setAttendanceSearch(it?.toString().orEmpty())
        }
        binding.attendanceDateInput.doAfterTextChanged {
            adminViewModel.setAttendanceDate(it?.toString().orEmpty())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.session.collect { userSession ->
                        session = userSession
                        if (userSession.isLoggedIn && userSession.mustChangePassword) {
                            findNavController().navigate(R.id.action_dashboard_to_change_password)
                            return@collect
                        }
                        binding.overviewWelcome.text = "Welcome, ${userSession.firstName}"
                        updateNavTabs()
                        updateVisibility()
                        if (userSession.role == "MEMBER") {
                            dashboardViewModel.loadMemberDashboard()
                        }
                        if (userSession.role == "ADMIN" || userSession.role == "STAFF") {
                            dashboardViewModel.loadGymQr()
                        }
                        if (userSession.role == "ADMIN") {
                            adminViewModel.loadAll()
                        }
                    }
                }
                launch {
                    dashboardViewModel.uiState.collect { state ->
                        renderDashboardState(state)
                        if (session.role == "ADMIN" && state.staffRefreshKey > 0) {
                            adminViewModel.loadAll()
                        }
                    }
                }
                launch {
                    adminViewModel.uiState.collect { state ->
                        renderAdminState(state)
                    }
                }
                launch {
                    settingsViewModel.uiState.collect { state ->
                        renderSettingsState(state)
                    }
                }
            }
        }
    }

    private fun setupSettingsListeners() {
        val panel = binding.settingsPanel
        listOf(
            panel.settingsCurrentPasswordInput,
            panel.settingsNewPasswordInput,
            panel.settingsConfirmPasswordInput
        ).forEach { field ->
            field.doAfterTextChanged { settingsViewModel.clearPasswordMessages() }
        }
        panel.settingsDeletePasswordInput.doAfterTextChanged {
            settingsViewModel.clearDeleteError()
        }

        panel.settingsUpdatePasswordButton.setOnClickListener {
            settingsViewModel.changePassword(
                currentPassword = panel.settingsCurrentPasswordInput.text?.toString().orEmpty(),
                newPassword = panel.settingsNewPasswordInput.text?.toString().orEmpty(),
                confirmPassword = panel.settingsConfirmPasswordInput.text?.toString().orEmpty()
            ) {
                panel.settingsCurrentPasswordInput.text = null
                panel.settingsNewPasswordInput.text = null
                panel.settingsConfirmPasswordInput.text = null
            }
        }

        panel.settingsDeleteAccountButton.setOnClickListener {
            val password = panel.settingsDeletePasswordInput.text?.toString().orEmpty()
            if (password.isBlank()) {
                settingsViewModel.deleteAccount("") {}
                return@setOnClickListener
            }
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete your account?")
                .setMessage(
                    "Your account, memberships, payments, and attendance history will be permanently removed. This cannot be undone."
                )
                .setNegativeButton("Keep Account", null)
                .setPositiveButton("Delete Account") { _, _ ->
                    settingsViewModel.deleteAccount(password) {
                        findNavController().navigate(R.id.action_dashboard_to_login)
                    }
                }
                .create()
            dialog.show()
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(requireContext().getColor(R.color.gymtrack_error))
        }
    }

    private fun renderSettingsState(state: edu.cit.lim.gymtrack.mobile.feature.settings.SettingsUiState) {
        val panel = binding.settingsPanel
        panel.settingsPasswordError.showError(state.passwordError)
        panel.settingsPasswordSuccess.isVisible = !state.passwordSuccess.isNullOrBlank()
        panel.settingsPasswordSuccess.text = state.passwordSuccess.orEmpty()
        panel.settingsUpdatePasswordButton.isEnabled = !state.passwordLoading
        panel.settingsUpdatePasswordButton.text =
            if (state.passwordLoading) "Updating..." else "Update Password"

        panel.settingsDeleteError.showError(state.deleteError)
        panel.settingsDeleteAccountButton.isEnabled = !state.deleteLoading
        panel.settingsDeleteAccountButton.text =
            if (state.deleteLoading) "Deleting..." else "Delete Account"
    }

    private fun selectTab(tab: String) {
        activeTab = tab
        if (session.role == "ADMIN") {
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
        when (tab) {
            "qr" -> when (session.role) {
                "MEMBER" -> dashboardViewModel.loadMemberQr()
                "ADMIN", "STAFF" -> dashboardViewModel.loadGymQr()
            }
        }
        updateNavTabs()
        updateVisibility()
        renderDashboardState(dashboardViewModel.uiState.value)
    }

    private fun updateNavTabs() {
        binding.navTabsContainer.setupNavTabs(session.role, activeTab, ::selectTab)
    }

    private fun updateVisibility() {
        val isMember = session.role == "MEMBER"
        val canScan = session.role == "ADMIN" || session.role == "STAFF"
        val isAdmin = session.role == "ADMIN"
        val isStaff = session.role == "STAFF"

        binding.summarySection.isVisible = activeTab == "home"
        binding.overviewWelcome.isVisible = activeTab != "settings"
        binding.overviewSubtitle.isVisible = activeTab != "settings"
        binding.memberOnboarding.root.isVisible = isMember && activeTab == "home"
        binding.staffHomeHint.isVisible = isStaff && activeTab == "home"
        if (isStaff && activeTab == "home") {
            binding.staffHomeHint.text = DashboardUiCopy.staffHomeHint
        }

        binding.qrCard.root.isVisible = false
        binding.memberQrLocked.isVisible = false
        if (isMember && activeTab == "qr") {
            val membership = dashboardViewModel.uiState.value.membership
            val showMemberQr = membership?.nextStep == "FIRST_CHECK_IN" || membership?.nextStep == "ACTIVE"
            binding.qrCard.root.isVisible = showMemberQr
            binding.memberQrLocked.isVisible = !showMemberQr
            if (!showMemberQr) binding.memberQrLocked.text = DashboardUiCopy.memberQrLocked
        }
        if (canScan && activeTab == "qr") {
            binding.qrCard.root.isVisible = true
        }

        binding.membershipCard.root.isVisible = isMember && activeTab == "plans"
        binding.planPickerSection.isVisible = isMember && activeTab == "plans"
        binding.activitySection.isVisible = isMember && activeTab == "activity"

        binding.adminScannerSection.root.isVisible = isAdmin && activeTab == "qr"
        binding.staffScannerSection.root.isVisible = isStaff && activeTab == "scan"

        binding.adminOverviewSection.isVisible = isAdmin && activeTab == "home"
        binding.adminPlansSection.isVisible = isAdmin && activeTab == "plans"
        binding.adminMembersSection.isVisible = isAdmin && activeTab == "members"
        binding.adminStaffSection.isVisible = isAdmin && activeTab == "staff"
        binding.adminAttendanceSection.isVisible = isAdmin && activeTab == "attendance"
        binding.adminPaymentsSection.isVisible = isAdmin && activeTab == "payments"
        binding.settingsPanel.root.isVisible = activeTab == "settings"
    }

    private fun renderDashboardState(state: edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.DashboardUiState) {
        binding.summaryName.summaryLabel.text = "NAME"
        binding.summaryName.summaryValue.text = "${session.firstName} ${session.lastName}".trim()
        binding.summaryRole.summaryLabel.text = "ROLE"
        binding.summaryRole.summaryValue.text = session.role
        binding.summaryStatus.summaryLabel.text = "STATUS"
        binding.summaryStatus.summaryValue.text = "Active"

        val membership = state.membership
        val step = membership?.nextStep ?: "ENROLL_AT_GYM"
        val (title, body) = DashboardUiCopy.onboardingStep(step)
        binding.memberOnboarding.onboardingTitle.text = title
        binding.memberOnboarding.onboardingBody.text = body
        binding.memberOnboarding.onboardingGym.isVisible = !membership?.gymName.isNullOrBlank() && step != "ENROLL_AT_GYM"
        if (binding.memberOnboarding.onboardingGym.isVisible) {
            binding.memberOnboarding.onboardingGym.text = "Gym: ${membership?.gymName}"
        }
        binding.memberOnboarding.scanGymButton.isVisible = step == "ENROLL_AT_GYM"
        binding.memberOnboarding.onboardingStatus.showError(state.memberGymScanStatus)

        val isMember = session.role == "MEMBER"
        val canScan = session.role == "ADMIN" || session.role == "STAFF"
        if (isMember && activeTab == "qr") {
            val showMemberQr = membership?.nextStep == "FIRST_CHECK_IN" || membership?.nextStep == "ACTIVE"
            if (showMemberQr) {
                binding.qrCard.qrLoading.isVisible = state.loading
                val loaded = binding.qrCard.qrImage.loadBase64Qr(state.qrImageBase64)
                binding.qrCard.qrNote.text = when {
                    state.loading -> "Loading QR code..."
                    !loaded -> state.memberStatusMessage ?: "We couldn't load your QR code. Please try again."
                    membership?.nextStep == "FIRST_CHECK_IN" -> DashboardUiCopy.memberQrNoteFirstCheckIn
                    else -> DashboardUiCopy.memberQrNoteActive
                }
            }
        } else if (canScan && activeTab == "qr") {
            binding.qrCard.qrLoading.isVisible = state.loadingGymQr
            val loaded = binding.qrCard.qrImage.loadBase64Qr(state.gymQrImageBase64)
            binding.qrCard.qrNote.text = when {
                state.loadingGymQr -> "Loading gym QR code..."
                !loaded -> state.scanStatusMessage ?: "We couldn't load the gym QR code. Please try again."
                else -> "Display this QR at the front desk for member enrollment."
            }
        }

        binding.membershipCard.membershipPlan.text = membership?.planName ?: "No plan"
        binding.membershipCard.membershipStatus.applyStatusBadge(membership?.status)
        binding.membershipCard.membershipEndDate.text = membership?.endDate ?: "—"

        val enrolled = membership?.gymId != null
        if (!enrolled) {
            binding.planPickerEmpty.isVisible = true
            binding.planPickerEmpty.text =
                "Scan the gym QR code at the front desk first to see available subscription plans."
            binding.plansRecycler.isVisible = false
        } else if (state.activePlans.isEmpty()) {
            binding.planPickerEmpty.isVisible = true
            binding.planPickerEmpty.text = "No active plans available at your gym."
            binding.plansRecycler.isVisible = false
        } else {
            binding.planPickerEmpty.isVisible = false
            binding.plansRecycler.isVisible = true
            planAdapter = PlanAdapter(state.subscribing) { planId ->
                dashboardViewModel.subscribeToPlan(planId) { url ->
                    val intent = CustomTabsIntent.Builder().build()
                    browserLauncher.launch(intent.intent.apply { data = Uri.parse(url) })
                }
            }
            binding.plansRecycler.adapter = planAdapter
            planAdapter.submitList(state.activePlans)
        }
        binding.paymentModeNote.isVisible = !state.paymentModeNote.isNullOrBlank()
        binding.paymentModeNote.text = state.paymentModeNote.orEmpty()
        binding.memberStatusMessage.showError(state.memberStatusMessage)

        binding.paymentsEmpty.isVisible = state.myPayments.isEmpty()
        binding.paymentsRecycler.isVisible = state.myPayments.isNotEmpty()
        paymentAdapter.submitList(state.myPayments)

        binding.attendanceLoading.isVisible = state.loading
        binding.attendanceEmpty.isVisible = !state.loading && state.myAttendanceLogs.isEmpty()
        binding.attendanceRecycler.isVisible = state.myAttendanceLogs.isNotEmpty()
        attendanceAdapter.submitList(state.myAttendanceLogs)

        binding.adminScannerSection.scanStatusText.showError(state.scanStatusMessage)
        binding.staffScannerSection.scanStatusText.showError(state.scanStatusMessage)

        val missingGym = session.gymName.isNullOrBlank()
        binding.createStaffForm.staffGymHint.text = if (missingGym) {
            "Your account isn't connected to a gym yet. Sign out, then create a new account as Gym Owner and enter your gym name."
        } else {
            "Gym: ${session.gymName} — new staff will belong to this gym."
        }
        binding.createStaffForm.staffGymHint.setTextColor(
            requireContext().getColor(if (missingGym) R.color.gymtrack_error else R.color.gymtrack_text_primary)
        )
        syncingStaffForm = true
        binding.createStaffForm.staffFirstName.setText(state.staffForm.firstName)
        binding.createStaffForm.staffLastName.setText(state.staffForm.lastName)
        binding.createStaffForm.staffEmail.setText(state.staffForm.email)
        binding.createStaffForm.staffPassword.setText(state.staffForm.password)
        binding.createStaffForm.staffConfirmPassword.setText(state.staffForm.confirmPassword)
        syncingStaffForm = false
        binding.createStaffForm.createStaffButton.isEnabled = !state.creatingStaff && !missingGym
        binding.createStaffForm.createStaffButton.text =
            if (state.creatingStaff) "Creating Staff..." else "Create Staff"
        binding.createStaffForm.staffStatusMessage.showError(state.staffStatusMessage)
    }

    private fun renderAdminState(state: edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.AdminUiState) {
        kpiAdapter.submitList(adminViewModel.kpiItems())

        planAdminAdapter.submitList(state.plans)
        binding.adminPlansEmpty.isVisible = state.plans.isEmpty()
        binding.adminPlansRecycler.isVisible = state.plans.isNotEmpty()

        syncingPlanForm = true
        binding.planForm.planFormTitle.text = if (state.editingPlanId != null) "EDIT PLAN" else "CREATE PLAN"
        binding.planForm.planNameInput.setText(state.planForm.name)
        binding.planForm.planDurationInput.setText(state.planForm.durationDays)
        binding.planForm.planPriceInput.setText(state.planForm.price)
        binding.planForm.savePlanButton.text =
            if (state.editingPlanId != null) "Update Plan" else "Create Plan"
        syncingPlanForm = false
        binding.planForm.planFormStatus.showError(state.statusMessage)

        val filteredMembers = state.members.filter { member ->
            val term = state.memberSearch.trim().lowercase()
            val matchesSearch = term.isEmpty() ||
                "${member.firstName} ${member.lastName} ${member.email}".lowercase().contains(term)
            val matchesStatus = state.memberStatus == "ALL" || member.membershipStatus == state.memberStatus
            matchesSearch && matchesStatus
        }
        memberAdapter.submitList(filteredMembers)
        binding.adminMembersEmpty.isVisible = filteredMembers.isEmpty()
        binding.adminMembersEmpty.text = if (state.members.isEmpty()) {
            "No members enrolled at your gym yet. Ask new members to scan your gym QR code."
        } else {
            "No members match your search or filter."
        }

        staffAdapter.submitList(state.staff)
        binding.adminStaffEmpty.isVisible = state.staff.isEmpty()

        adminAttendanceAdapter.submitList(state.attendanceLogs)

        adminPaymentAdapter.submitList(state.payments)
        binding.adminPaymentsEmpty.isVisible = state.payments.isEmpty()

        binding.adminStatusMessage.showError(state.statusMessage)
    }

    private fun openScanner(mode: String) {
        QrScannerDialogFragment.newInstance(mode).show(childFragmentManager, "qr_scanner")
    }

    fun onQrScan(payload: String, mode: String) {
        if (mode == "gym") dashboardViewModel.onMemberGymScan(payload)
        else dashboardViewModel.onStaffMemberScan(payload)
    }

    private fun showMemberEditDialog(member: MemberResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_member, null)
        val firstName = dialogView.findViewById<TextInputEditText>(R.id.editFirstName)
        val lastName = dialogView.findViewById<TextInputEditText>(R.id.editLastName)
        val email = dialogView.findViewById<TextInputEditText>(R.id.editEmail)
        val activeSwitch = dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.editActiveSwitch)
        firstName.setText(member.firstName)
        lastName.setText(member.lastName)
        email.setText(member.email)
        activeSwitch.isChecked = member.active

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Member")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                adminViewModel.updateMember(
                    member.id,
                    MemberUpdateRequest(
                        firstName.text?.toString(),
                        lastName.text?.toString(),
                        email.text?.toString(),
                        activeSwitch.isChecked
                    )
                )
            }
            .show()
    }

    private fun showAssignPlanDialog(member: MemberResponse) {
        val plans = adminViewModel.uiState.value.plans.filter { it.active }
        if (plans.isEmpty()) return
        var selectedId = plans.first().id
        val labels = plans.map { "${it.name} — ₱${it.price}" }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Assign Plan to ${member.firstName}")
            .setSingleChoiceItems(labels, 0) { _, which ->
                selectedId = plans[which].id
            }
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Assign") { _, _ ->
                adminViewModel.assignPlan(member.id, selectedId)
            }
            .show()
    }

    private fun showStaffEditDialog(staff: StaffResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_staff, null)
        val firstName = dialogView.findViewById<TextInputEditText>(R.id.editFirstName)
        val lastName = dialogView.findViewById<TextInputEditText>(R.id.editLastName)
        val email = dialogView.findViewById<TextInputEditText>(R.id.editEmail)
        val activeSwitch = dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.editActiveSwitch)
        firstName.setText(staff.firstName)
        lastName.setText(staff.lastName)
        email.setText(staff.email)
        activeSwitch.isChecked = staff.active

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Staff")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                adminViewModel.updateStaff(
                    staff.id,
                    StaffUpdateRequest(
                        firstName.text?.toString(),
                        lastName.text?.toString(),
                        email.text?.toString(),
                        activeSwitch.isChecked
                    )
                )
            }
            .show()
    }

    private fun confirmDeleteMember(member: MemberResponse) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove member from gym?")
            .setMessage(
                "This will remove ${member.firstName} ${member.lastName} (${member.email}) from your gym and end their membership.\n\nTheir login account stays active so they can join a gym again later."
            )
            .setNegativeButton("Keep Member", null)
            .setPositiveButton("Remove from Gym") { _, _ ->
                adminViewModel.deleteMember(member.id)
            }
            .create()

        dialog.show()
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(requireContext().getColor(R.color.gymtrack_error))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
