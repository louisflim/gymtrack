package edu.cit.lim.gymtrack.mobile.feature.auth.registration

import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.local.SessionTokenHolder
import edu.cit.lim.gymtrack.mobile.data.model.AuthResponse
import edu.cit.lim.gymtrack.mobile.data.model.RegisterRequest
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

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
    ): UserSession {
        val response = apiService.register(
            RegisterRequest(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                email = email.trim(),
                password = password,
                role = role,
                gymName = gymName?.trim()?.takeIf { it.isNotBlank() }
            )
        )
        return handleAuthResponse(response)
    }

    private suspend fun handleAuthResponse(response: retrofit2.Response<AuthResponse>): UserSession {
        if (response.isSuccessful) {
            val body = response.body()
                ?: throw AuthException(response.code(), "Empty response from server.")
            val session = body.toSession()
            sessionDataStore.saveSession(session)
            SessionTokenHolder.token = session.token
            return session
        }

        val message = ApiErrorParser.parse(
            response.errorBody()?.string(),
            response.code(),
            defaultErrorMessage(response.code())
        )
        throw AuthException(response.code(), message)
    }

    private fun AuthResponse.toSession() = UserSession(
        token = token,
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        role = role,
        gymId = gymId,
        gymName = gymName
    )

    private fun defaultErrorMessage(statusCode: Int): String = when (statusCode) {
        401 -> "Invalid email or password."
        403 -> "You do not have permission to perform this action."
        400 -> "Request failed. Check your details."
        else -> "Something went wrong. Try again."
    }
}
