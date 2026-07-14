package edu.cit.lim.gymtrack.mobile.feature.auth.login

import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.model.LoginRequest
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import edu.cit.lim.gymtrack.mobile.data.model.saveAuthResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class LoginRepository(
    private val sessionDataStore: SessionDataStore,
    private val apiService: ApiService
) {
    suspend fun login(email: String, password: String): UserSession =
        sessionDataStore.saveAuthResponse(apiService.login(LoginRequest(email.trim(), password))) { code ->
            when (code) {
                401 -> "Invalid email or password."
                403 -> "This account has been deactivated."
                else -> "Something went wrong. Please try again."
            }
        }
}
