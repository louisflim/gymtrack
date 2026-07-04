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
    val role: String,
    val gymName: String? = null
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val gymId: Long? = null,
    val gymName: String? = null
)

data class UserSession(
    val token: String = "",
    val userId: Long = 0L,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val role: String = "",
    val gymId: Long? = null,
    val gymName: String? = null
) {
    val isLoggedIn: Boolean get() = token.isNotBlank()
}

data class QrCodeResponse(
    val qrData: String,
    val qrImageBase64: String
)

data class ScanRequest(
    val qrData: String
)

data class AttendanceScanResponse(
    val action: String,
    val memberName: String,
    val role: String,
    val active: Boolean,
    val planName: String?,
    val membershipStatus: String?,
    val timestamp: String,
    val message: String,
    val gymName: String? = null,
    val enrolled: Boolean = false
)

data class AttendanceLogResponse(
    val id: Long? = null,
    val memberId: Long? = null,
    val memberName: String? = null,
    val checkInTime: String,
    val checkOutTime: String?
)

data class StaffAccountResponse(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String
)
