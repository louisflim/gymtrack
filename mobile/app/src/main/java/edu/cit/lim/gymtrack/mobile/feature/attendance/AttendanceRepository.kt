package edu.cit.lim.gymtrack.mobile.feature.attendance

import edu.cit.lim.gymtrack.mobile.data.model.AttendanceLogResponse
import edu.cit.lim.gymtrack.mobile.data.model.AttendanceScanResponse
import edu.cit.lim.gymtrack.mobile.data.model.MemberGymScanResponse
import edu.cit.lim.gymtrack.mobile.data.model.QrCodeResponse
import edu.cit.lim.gymtrack.mobile.data.model.ScanRequest
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

class AttendanceRepository(
    private val apiService: ApiService
) {
    suspend fun getMyQrCode(): QrCodeResponse {
        val response = apiService.myQrCode()
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body() ?: throw AuthException(response.code(), "We couldn't load your QR code. Please try again.")
    }

    suspend fun getGymQrCode(): QrCodeResponse {
        val response = apiService.gymQrCode()
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body() ?: throw AuthException(response.code(), "We couldn't load the gym QR code. Please try again.")
    }

    suspend fun scanGymQr(qrData: String): MemberGymScanResponse {
        val response = apiService.scanGymQr(ScanRequest(qrData))
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body() ?: throw AuthException(response.code(), "We couldn't complete that scan. Please try again.")
    }

    suspend fun scanQr(qrData: String): AttendanceScanResponse {
        val response = apiService.scanAttendance(ScanRequest(qrData))
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body() ?: throw AuthException(response.code(), "We couldn't complete that scan. Please try again.")
    }

    suspend fun getMyAttendance(): List<AttendanceLogResponse> {
        val response = apiService.myAttendance()
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body().orEmpty()
    }

    suspend fun gymAttendance(search: String? = null, date: String? = null): List<AttendanceLogResponse> {
        val response = apiService.gymAttendance(search, date)
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body().orEmpty()
    }

    private fun extractMessage(statusCode: Int, message: String?): String {
        val clean = message?.trim('"')?.trim().orEmpty()
        if (clean.isNotBlank()) return clean
        return when (statusCode) {
            400 -> "That QR code isn't valid. Please try scanning again."
            401 -> "Please sign in again to continue."
            403 -> "You don't have permission to do that."
            else -> "Something went wrong. Please try again."
        }
    }
}
