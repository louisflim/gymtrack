package edu.cit.lim.gymtrack.mobile.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: String
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String
)

data class UserSession(
    val token: String = "",
    val userId: Long = 0L,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val role: String = ""
) {
    val isLoggedIn: Boolean get() = token.isNotBlank()
}
