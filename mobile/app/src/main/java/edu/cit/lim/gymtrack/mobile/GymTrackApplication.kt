package edu.cit.lim.gymtrack.mobile

import android.app.Application
import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.local.SessionTokenHolder
import edu.cit.lim.gymtrack.mobile.data.remote.RetrofitClient
import edu.cit.lim.gymtrack.mobile.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking

class GymTrackApplication : Application() {

    lateinit var authRepository: AuthRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val sessionDataStore = SessionDataStore(this)
        authRepository = AuthRepository(sessionDataStore, RetrofitClient.apiService)

        runBlocking {
            val session = authRepository.currentSession()
            SessionTokenHolder.token = session.token.ifBlank { null }
        }

        RetrofitClient.setTokenProvider { SessionTokenHolder.token }
    }
}
