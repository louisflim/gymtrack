package edu.cit.lim.gymtrack.mobile.ui.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.cit.lim.gymtrack.mobile.GymTrackApplication
import edu.cit.lim.gymtrack.mobile.ui.auth.AuthViewModel
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.AdminViewModel
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.DashboardViewModel

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

class DashboardViewModelFactory(
    private val app: GymTrackApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(app.attendanceRepository, app.authRepository, app.gymRepository) as T
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
            return AdminViewModel(app.gymRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
