package edu.cit.lim.gymtrack.mobile.feature.auth.registration

import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.model.RegisterRequest
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import edu.cit.lim.gymtrack.mobile.data.model.saveAuthResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class RegistrationRepository(
    private val sessionDataStore: SessionDataStore,
    private val apiService: ApiService
) {
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: String,
        gymName: String? = null
    ): UserSession =
        sessionDataStore.saveAuthResponse(
            apiService.register(
                RegisterRequest(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = email.trim(),
                    password = password,
                    role = role,
                    gymName = gymName?.trim()?.takeIf { it.isNotBlank() }
                )
            )
        ) { code ->
            when (code) {
                401 -> "Invalid email or password."
                403 -> "You don't have permission to do that."
                400 -> ApiErrorParser.CHECK_DETAILS
                else -> ApiErrorParser.GENERIC
            }
        }
}
