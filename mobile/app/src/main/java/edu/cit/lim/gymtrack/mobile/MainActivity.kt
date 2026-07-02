package edu.cit.lim.gymtrack.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import edu.cit.lim.gymtrack.mobile.ui.auth.AuthViewModel
import edu.cit.lim.gymtrack.mobile.ui.navigation.AppNavGraph
import edu.cit.lim.gymtrack.mobile.ui.navigation.Routes
import edu.cit.lim.gymtrack.mobile.ui.screens.dashboard.DashboardViewModel
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as GymTrackApplication

        setContent {
            GymTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = GymTrackBackground
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel(
                        factory = AuthViewModel.Factory(app.authRepository)
                    )
                    val dashboardViewModel: DashboardViewModel = viewModel(
                        factory = DashboardViewModel.Factory(
                            app.attendanceRepository,
                            app.authRepository
                        )
                    )

                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        val session = app.authRepository.currentSession()
                        startDestination = if (session.isLoggedIn) {
                            Routes.DASHBOARD
                        } else {
                            Routes.LOGIN
                        }
                    }

                    startDestination?.let { destination ->
                        AppNavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                            dashboardViewModel = dashboardViewModel,
                            startDestination = destination
                        )
                    }
                }
            }
        }
    }
}
