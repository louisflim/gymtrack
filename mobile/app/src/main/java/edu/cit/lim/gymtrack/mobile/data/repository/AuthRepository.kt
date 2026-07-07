package edu.cit.lim.gymtrack.mobile.data.repository

import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.local.SessionTokenHolder
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthException(val statusCode: Int, message: String) : Exception(message)

class AuthRepository(
    private val sessionDataStore: SessionDataStore
) {
    val sessionFlow: Flow<UserSession> = sessionDataStore.sessionFlow

    suspend fun currentSession(): UserSession = sessionFlow.first()

    suspend fun logout() {
        sessionDataStore.clearSession()
        SessionTokenHolder.token = null
    }
}
