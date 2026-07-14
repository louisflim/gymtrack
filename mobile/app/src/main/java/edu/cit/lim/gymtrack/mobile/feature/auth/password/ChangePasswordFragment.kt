package edu.cit.lim.gymtrack.mobile.feature.auth.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import edu.cit.lim.gymtrack.mobile.GymTrackApplication
import edu.cit.lim.gymtrack.mobile.R
import edu.cit.lim.gymtrack.mobile.databinding.FragmentChangePasswordBinding
import edu.cit.lim.gymtrack.mobile.ui.auth.AuthViewModel
import edu.cit.lim.gymtrack.mobile.ui.util.AuthViewModelFactory
import edu.cit.lim.gymtrack.mobile.ui.util.ChangePasswordViewModelFactory
import edu.cit.lim.gymtrack.mobile.ui.util.showError
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangePasswordViewModel by viewModels {
        ChangePasswordViewModelFactory(requireActivity().application as GymTrackApplication)
    }

    private val authViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(requireActivity().application as GymTrackApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOf(
            binding.currentPasswordInput,
            binding.newPasswordInput,
            binding.confirmPasswordInput
        ).forEach { field ->
            field.doAfterTextChanged {
                viewModel.clearError()
                updateButtonState()
            }
        }

        binding.updatePasswordButton.setOnClickListener {
            viewModel.changePassword(
                currentPassword = binding.currentPasswordInput.text?.toString().orEmpty(),
                newPassword = binding.newPasswordInput.text?.toString().orEmpty(),
                confirmPassword = binding.confirmPasswordInput.text?.toString().orEmpty()
            ) {
                findNavController().navigate(R.id.action_change_password_to_dashboard)
            }
        }

        binding.signOutLink.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_change_password_to_login)
            }
        }

        updateButtonState()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.errorText.showError(state.error)
                    updateButtonState()
                    binding.updatePasswordButton.text =
                        if (state.isLoading) "Updating..." else "Update Password"
                }
            }
        }
    }

    private fun updateButtonState() {
        binding.updatePasswordButton.isEnabled = !viewModel.uiState.value.isLoading &&
            binding.currentPasswordInput.text?.isNotBlank() == true &&
            binding.newPasswordInput.text?.isNotBlank() == true &&
            binding.confirmPasswordInput.text?.isNotBlank() == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
