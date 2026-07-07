package edu.cit.lim.gymtrack.mobile.ui.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.cit.lim.gymtrack.mobile.GymTrackApplication
import edu.cit.lim.gymtrack.mobile.ui.auth.AuthViewModel
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.AdminViewModel
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.DashboardViewModel
import edu.cit.lim.gymtrack.mobile.feature.auth.login.LoginViewModel
import edu.cit.lim.gymtrack.mobile.feature.auth.registration.RegistrationViewModel

class AuthViewModelFactory(
    private val app: GymTrackApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(app.authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LoginViewModelFactory(
    private val app: GymTrackApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(app.loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RegistrationViewModelFactory(
    private val app: GymTrackApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(app.registrationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DashboardViewModelFactory(
    private val app: GymTrackApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                app.attendanceRepository,
                app.authRepository,
                app.gymRepository,
                app.staffCreationRepository,
                app.planRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AdminViewModelFactory(
    private val app: GymTrackApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(app.gymRepository, app.planRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
