package edu.cit.lim.gymtrack.mobile.data.model

import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.local.SessionTokenHolder
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import retrofit2.Response

fun AuthResponse.toUserSession() = UserSession(
    token = token,
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    email = email,
    role = role,
    gymId = gymId,
    gymName = gymName,
    mustChangePassword = mustChangePassword
)

suspend fun SessionDataStore.saveAuthResponse(
    response: Response<AuthResponse>,
    fallbackMessage: (Int) -> String = { ApiErrorParser.GENERIC }
): UserSession {
    if (!response.isSuccessful) {
        throw AuthException(
            response.code(),
            ApiErrorParser.parse(
                response.errorBody()?.string(),
                response.code(),
                fallbackMessage(response.code())
            )
        )
    }
    val body = response.body() ?: throw AuthException(response.code(), ApiErrorParser.EMPTY)
    val session = body.toUserSession()
    saveSession(session)
    SessionTokenHolder.token = session.token
    return session
}
