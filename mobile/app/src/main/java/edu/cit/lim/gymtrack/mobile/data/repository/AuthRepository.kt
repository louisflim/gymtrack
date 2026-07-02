package edu.cit.lim.gymtrack.mobile.data.repository

import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.local.SessionTokenHolder
import edu.cit.lim.gymtrack.mobile.data.model.AuthResponse
import edu.cit.lim.gymtrack.mobile.data.model.LoginRequest
import edu.cit.lim.gymtrack.mobile.data.model.RegisterRequest
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthException(val statusCode: Int, message: String) : Exception(message)

class AuthRepository(
    private val sessionDataStore: SessionDataStore,
    private val apiService: ApiService
) {
    val sessionFlow: Flow<UserSession> = sessionDataStore.sessionFlow

    suspend fun currentSession(): UserSession = sessionFlow.first()

    suspend fun login(email: String, password: String): UserSession {
        val response = apiService.login(LoginRequest(email.trim(), password))
        return handleAuthResponse(response)
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: String
    ): UserSession {
        val response = apiService.register(
            RegisterRequest(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                email = email.trim(),
                password = password,
                role = role
            )
        )
        return handleAuthResponse(response)
    }

    suspend fun logout() {
        sessionDataStore.clearSession()
        SessionTokenHolder.token = null
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

        val message = response.errorBody()?.string()?.trim('"').orEmpty()
            .ifBlank { defaultErrorMessage(response.code()) }
        throw AuthException(response.code(), message)
    }

    private fun AuthResponse.toSession() = UserSession(
        token = token,
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        role = role
    )

    private fun defaultErrorMessage(statusCode: Int): String = when (statusCode) {
        401 -> "Invalid email or password."
        403 -> "This account has been deactivated."
        400 -> "Request failed. Check your details."
        else -> "Something went wrong. Try again."
    }
}
