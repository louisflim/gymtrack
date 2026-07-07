package edu.cit.lim.gymtrack.mobile.feature.members

import edu.cit.lim.gymtrack.mobile.data.model.AssignPlanRequest
import edu.cit.lim.gymtrack.mobile.data.model.MemberResponse
import edu.cit.lim.gymtrack.mobile.data.model.MemberUpdateRequest
import edu.cit.lim.gymtrack.mobile.data.model.MembershipResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

class MemberRepository(private val apiService: ApiService) {

    suspend fun members(search: String? = null, status: String? = null): List<MemberResponse> =
        unwrap(apiService.members(search, status))

    suspend fun updateMember(id: Long, request: MemberUpdateRequest): MemberResponse =
        unwrap(apiService.updateMember(id, request))

    suspend fun assignPlan(memberId: Long, planId: Long): MembershipResponse =
        unwrap(apiService.assignPlan(AssignPlanRequest(memberId, planId)))

    private suspend fun <T> unwrap(response: retrofit2.Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw AuthException(response.code(), "Empty response from server.")
        }
        val message = response.errorBody()?.string()?.trim('"').orEmpty()
            .ifBlank { "Request failed (${response.code()})." }
        throw AuthException(response.code(), message)
    }
}
