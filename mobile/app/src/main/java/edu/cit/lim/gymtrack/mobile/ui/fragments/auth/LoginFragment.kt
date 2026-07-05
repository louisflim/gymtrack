package edu.cit.lim.gymtrack.mobile.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import edu.cit.lim.gymtrack.mobile.GymTrackApplication
import edu.cit.lim.gymtrack.mobile.R
import edu.cit.lim.gymtrack.mobile.databinding.FragmentLoginBinding
import edu.cit.lim.gymtrack.mobile.ui.auth.AuthViewModel
import edu.cit.lim.gymtrack.mobile.ui.util.AuthViewModelFactory
import edu.cit.lim.gymtrack.mobile.ui.util.BrandStatData
import edu.cit.lim.gymtrack.mobile.ui.util.setupBrandPanel
import edu.cit.lim.gymtrack.mobile.ui.util.showError
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(requireActivity().application as GymTrackApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.brandPanel.setupBrandPanel(
            line1 = "TRAIN",
            line2 = "TRACK",
            highlight = "TRANSFORM",
            tagline = "One system for memberships, attendance, and payments. Built for gyms that run on discipline, not paperwork.",
            stats = listOf(
                BrandStatData("01", "Sign In"),
                BrandStatData("02", "Scan QR"),
                BrandStatData("03", "Train")
            )
        )

        binding.emailInput.doAfterTextChanged { viewModel.clearLoginError() }
        binding.passwordInput.doAfterTextChanged { viewModel.clearLoginError() }

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailInput.text?.toString().orEmpty()
            val password = binding.passwordInput.text?.toString().orEmpty()
            viewModel.login(email, password) {
                findNavController().navigate(R.id.action_login_to_dashboard)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    binding.errorText.showError(state.error)
                    binding.signInButton.isEnabled = !state.isLoading &&
                        binding.emailInput.text?.isNotBlank() == true &&
                        binding.passwordInput.text?.isNotBlank() == true
                    binding.signInButton.text = if (state.isLoading) "Signing In..." else "Sign In"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
