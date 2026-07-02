package edu.cit.lim.gymtrack.mobile.data.remote

import edu.cit.lim.gymtrack.mobile.data.model.AuthResponse
import edu.cit.lim.gymtrack.mobile.data.model.AttendanceLogResponse
import edu.cit.lim.gymtrack.mobile.data.model.AttendanceScanResponse
import edu.cit.lim.gymtrack.mobile.data.model.LoginRequest
import edu.cit.lim.gymtrack.mobile.data.model.QrCodeResponse
import edu.cit.lim.gymtrack.mobile.data.model.RegisterRequest
import edu.cit.lim.gymtrack.mobile.data.model.ScanRequest
import edu.cit.lim.gymtrack.mobile.data.model.StaffAccountResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/qr/me")
    suspend fun myQrCode(): Response<QrCodeResponse>

    @POST("api/attendance/scan")
    suspend fun scanAttendance(@Body request: ScanRequest): Response<AttendanceScanResponse>

    @GET("api/attendance/me")
    suspend fun myAttendance(): Response<List<AttendanceLogResponse>>

    @POST("api/auth/staff")
    suspend fun createStaff(@Body request: RegisterRequest): Response<StaffAccountResponse>
}
