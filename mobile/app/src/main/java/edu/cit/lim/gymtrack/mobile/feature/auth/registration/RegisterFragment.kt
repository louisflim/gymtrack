package edu.cit.lim.gymtrack.mobile.feature.auth.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import edu.cit.lim.gymtrack.mobile.GymTrackApplication
import edu.cit.lim.gymtrack.mobile.R
import edu.cit.lim.gymtrack.mobile.databinding.FragmentRegisterBinding
import edu.cit.lim.gymtrack.mobile.ui.util.BrandStatData
import edu.cit.lim.gymtrack.mobile.ui.util.RegistrationViewModelFactory
import edu.cit.lim.gymtrack.mobile.ui.util.setupBrandPanel
import edu.cit.lim.gymtrack.mobile.ui.util.showError
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory(requireActivity().application as GymTrackApplication)
    }

    private var selectedRole = "member"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.brandPanel.setupBrandPanel(
            line1 = "JOIN",
            line2 = "THE",
            highlight = "FLOOR",
            tagline = "Create your account to manage your membership, track attendance, and pay online — all in one place.",
            stats = listOf(
                BrandStatData("01", "Register"),
                BrandStatData("02", "Choose Plan"),
                BrandStatData("03", "Get QR Code")
            ),
            eyebrow = "New Member"
        )

        val roles = listOf("Member", "Gym Owner")
        binding.roleSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            roles
        )
        binding.roleSpinner.setSelection(0)
        binding.roleSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedRole = if (position == 1) "owner" else "member"
                binding.gymNameLayout.isVisible = selectedRole == "owner"
                viewModel.clearError()
                updateCreateAccountButtonState()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        })

        listOf(
            binding.firstNameInput,
            binding.lastNameInput,
            binding.emailInput,
            binding.passwordInput,
            binding.confirmPasswordInput,
            binding.gymNameInput
        ).forEach { field ->
            field.doAfterTextChanged {
                viewModel.clearError()
                updateCreateAccountButtonState()
            }
        }

        binding.loginLink.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.createAccountButton.setOnClickListener {
            viewModel.register(
                firstName = binding.firstNameInput.text?.toString().orEmpty(),
                lastName = binding.lastNameInput.text?.toString().orEmpty(),
                email = binding.emailInput.text?.toString().orEmpty(),
                password = binding.passwordInput.text?.toString().orEmpty(),
                confirmPassword = binding.confirmPasswordInput.text?.toString().orEmpty(),
                role = selectedRole,
                gymName = binding.gymNameInput.text?.toString().orEmpty(),
                onSuccess = {
                    findNavController().navigate(R.id.action_register_to_dashboard)
                }
            )
        }
        updateCreateAccountButtonState()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.errorText.showError(state.error)
                    updateCreateAccountButtonState()
                    binding.createAccountButton.text =
                        if (state.isLoading) "Creating Account..." else "Create Account"
                }
            }
        }
    }

    private fun updateCreateAccountButtonState() {
        val formValid = binding.firstNameInput.text?.isNotBlank() == true &&
            binding.lastNameInput.text?.isNotBlank() == true &&
            binding.emailInput.text?.isNotBlank() == true &&
            binding.passwordInput.text?.isNotBlank() == true &&
            binding.confirmPasswordInput.text?.isNotBlank() == true &&
            (selectedRole != "owner" || binding.gymNameInput.text?.isNotBlank() == true)
        binding.createAccountButton.isEnabled = !viewModel.uiState.value.isLoading && formValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
