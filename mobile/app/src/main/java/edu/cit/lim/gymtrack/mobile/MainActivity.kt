package edu.cit.lim.gymtrack.mobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import edu.cit.lim.gymtrack.mobile.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val app = application as GymTrackApplication
            lifecycleScope.launch {
                val session = app.authRepository.currentSession()
                if (session.isLoggedIn) {
                    val navHost = supportFragmentManager
                        .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val destination = if (session.mustChangePassword) {
                        R.id.changePasswordFragment
                    } else {
                        R.id.dashboardFragment
                    }
                    navHost.navController.navigate(
                        destination,
                        null,
                        navOptions {
                            popUpTo(R.id.loginFragment) { inclusive = true }
                        }
                    )
                }
            }
        }
    }
}
