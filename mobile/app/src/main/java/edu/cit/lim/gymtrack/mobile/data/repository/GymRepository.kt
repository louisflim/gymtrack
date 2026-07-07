package edu.cit.lim.gymtrack.mobile.data.repository

import edu.cit.lim.gymtrack.mobile.data.model.*
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class GymRepository(private val apiService: ApiService) {

    suspend fun myMembership(): MembershipResponse =
        unwrap(apiService.myMembership())

    suspend fun checkout(planId: Long): CheckoutResponse =
        unwrap(apiService.checkout(CheckoutRequest(planId)))

    suspend fun myPayments(): List<PaymentResponse> =
        unwrap(apiService.myPayments())

    suspend fun allPayments(): List<PaymentResponse> =
        unwrap(apiService.allPayments())

    suspend fun dashboardStats(): DashboardStatsResponse =
        unwrap(apiService.dashboardStats())

    suspend fun staff(): List<StaffResponse> =
        unwrap(apiService.staffList())

    suspend fun updateStaff(id: Long, request: StaffUpdateRequest): StaffResponse =
        unwrap(apiService.updateStaff(id, request))

    suspend fun gymAttendance(search: String? = null, date: String? = null): List<AttendanceLogResponse> =
        unwrap(apiService.gymAttendance(search, date))

    suspend fun confirmMockPayment(reference: String) {
        unwrap(apiService.confirmMockPayment(reference))
    }

    private suspend fun <T> unwrap(response: retrofit2.Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw AuthException(response.code(), "Empty response from server.")
        }
        val message = response.errorBody()?.string()?.trim('"').orEmpty()
            .ifBlank { "Request failed (${response.code()})." }
        throw AuthException(response.code(), message)
    }
}
