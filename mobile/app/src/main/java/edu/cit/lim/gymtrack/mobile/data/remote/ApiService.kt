package edu.cit.lim.gymtrack.mobile.data.remote

import edu.cit.lim.gymtrack.mobile.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Map<String, String>>

    @POST("api/auth/staff")
    suspend fun createStaff(@Body request: CreateStaffRequest): Response<StaffAccountResponse>

    @GET("api/qr/me")
    suspend fun myQrCode(): Response<QrCodeResponse>

    @GET("api/qr/gym")
    suspend fun gymQrCode(): Response<QrCodeResponse>

    @POST("api/attendance/scan-gym")
    suspend fun scanGymQr(@Body request: ScanRequest): Response<MemberGymScanResponse>

    @POST("api/attendance/scan")
    suspend fun scanAttendance(@Body request: ScanRequest): Response<AttendanceScanResponse>

    @GET("api/attendance/me")
    suspend fun myAttendance(): Response<List<AttendanceLogResponse>>

    @GET("api/attendance/gym")
    suspend fun gymAttendance(
        @Query("search") search: String? = null,
        @Query("date") date: String? = null
    ): Response<List<AttendanceLogResponse>>

    @GET("api/dashboard/stats")
    suspend fun dashboardStats(): Response<DashboardStatsResponse>

    @GET("api/staff")
    suspend fun staffList(): Response<List<StaffResponse>>

    @PUT("api/staff/{id}")
    suspend fun updateStaff(
        @Path("id") id: Long,
        @Body request: StaffUpdateRequest
    ): Response<StaffResponse>

    @GET("api/plans/active")
    suspend fun activePlans(): Response<List<PlanResponse>>

    @GET("api/plans")
    suspend fun allPlans(): Response<List<PlanResponse>>

    @POST("api/plans")
    suspend fun createPlan(@Body request: PlanRequest): Response<PlanResponse>

    @PUT("api/plans/{id}")
    suspend fun updatePlan(@Path("id") id: Long, @Body request: PlanRequest): Response<PlanResponse>

    @GET("api/members")
    suspend fun members(
        @Query("search") search: String? = null,
        @Query("status") status: String? = null
    ): Response<List<MemberResponse>>

    @PUT("api/members/{id}")
    suspend fun updateMember(@Path("id") id: Long, @Body request: MemberUpdateRequest): Response<MemberResponse>

    @DELETE("api/members/{id}")
    suspend fun deleteMember(@Path("id") id: Long): Response<Unit>

    @POST("api/members/assign-plan")
    suspend fun assignPlan(@Body request: AssignPlanRequest): Response<MembershipResponse>

    @GET("api/membership/me")
    suspend fun myMembership(): Response<MembershipResponse>

    @POST("api/payments/checkout")
    suspend fun checkout(@Body request: CheckoutRequest): Response<CheckoutResponse>

    @GET("api/payments/me")
    suspend fun myPayments(): Response<List<PaymentResponse>>

    @GET("api/payments")
    suspend fun allPayments(): Response<List<PaymentResponse>>

    @GET("api/payments/status")
    suspend fun paymentStatus(@Query("reference") reference: String): Response<PaymentStatusResponse>

    @POST("api/payments/confirm-mock")
    suspend fun confirmMockPayment(@Query("reference") reference: String): Response<Map<String, String>>
}
