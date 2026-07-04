package edu.cit.lim.gymtrack.mobile.data.repository

import edu.cit.lim.gymtrack.mobile.data.model.*
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class GymRepository(private val apiService: ApiService) {

    suspend fun activePlans(): List<PlanResponse> =
        unwrap(apiService.activePlans())

    suspend fun allPlans(): List<PlanResponse> =
        unwrap(apiService.allPlans())

    suspend fun createPlan(request: PlanRequest): PlanResponse =
        unwrap(apiService.createPlan(request))

    suspend fun updatePlan(id: Long, request: PlanRequest): PlanResponse =
        unwrap(apiService.updatePlan(id, request))

    suspend fun members(search: String? = null, status: String? = null): List<MemberResponse> =
        unwrap(apiService.members(search, status))

    suspend fun updateMember(id: Long, request: MemberUpdateRequest): MemberResponse =
        unwrap(apiService.updateMember(id, request))

    suspend fun assignPlan(memberId: Long, planId: Long): MembershipResponse =
        unwrap(apiService.assignPlan(AssignPlanRequest(memberId, planId)))

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
