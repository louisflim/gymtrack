package edu.cit.lim.gymtrack.mobile.data.repository

import edu.cit.lim.gymtrack.mobile.data.model.AttendanceLogResponse
import edu.cit.lim.gymtrack.mobile.data.model.AttendanceScanResponse
import edu.cit.lim.gymtrack.mobile.data.model.QrCodeResponse
import edu.cit.lim.gymtrack.mobile.data.model.ScanRequest
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class AttendanceRepository(
    private val apiService: ApiService
) {
    suspend fun getMyQrCode(): QrCodeResponse {
        val response = apiService.myQrCode()
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body() ?: throw AuthException(response.code(), "No QR data returned.")
    }

    suspend fun scanQr(qrData: String): AttendanceScanResponse {
        val response = apiService.scanAttendance(ScanRequest(qrData))
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body() ?: throw AuthException(response.code(), "No scan result returned.")
    }

    suspend fun getMyAttendance(): List<AttendanceLogResponse> {
        val response = apiService.myAttendance()
        if (!response.isSuccessful) {
            throw AuthException(response.code(), extractMessage(response.code(), response.errorBody()?.string()))
        }
        return response.body().orEmpty()
    }

    private fun extractMessage(statusCode: Int, message: String?): String {
        val clean = message?.trim('"')?.trim().orEmpty()
        if (clean.isNotBlank()) return clean
        return when (statusCode) {
            400 -> "Invalid QR data."
            401 -> "Please sign in again."
            403 -> "Access denied for this action."
            else -> "Something went wrong. Try again."
        }
    }
}
