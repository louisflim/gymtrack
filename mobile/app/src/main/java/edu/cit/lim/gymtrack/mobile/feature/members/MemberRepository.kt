package edu.cit.lim.gymtrack.mobile.feature.members

import edu.cit.lim.gymtrack.mobile.data.model.AssignPlanRequest
import edu.cit.lim.gymtrack.mobile.data.model.MemberResponse
import edu.cit.lim.gymtrack.mobile.data.model.MemberUpdateRequest
import edu.cit.lim.gymtrack.mobile.data.model.MembershipResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

class MemberRepository(private val apiService: ApiService) {

    suspend fun members(search: String? = null, status: String? = null): List<MemberResponse> =
        unwrap(apiService.members(search, status))

    suspend fun updateMember(id: Long, request: MemberUpdateRequest): MemberResponse =
        unwrap(apiService.updateMember(id, request))

    suspend fun deleteMember(id: Long) {
        unwrapUnit(apiService.deleteMember(id))
    }

    suspend fun assignPlan(memberId: Long, planId: Long): MembershipResponse =
        unwrap(apiService.assignPlan(AssignPlanRequest(memberId, planId)))

    private suspend fun <T> unwrap(response: retrofit2.Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw AuthException(response.code(), ApiErrorParser.EMPTY)
        }
        throw AuthException(
            response.code(),
            ApiErrorParser.fromFailedResponse(response.code(), response.errorBody()?.string())
        )
    }

    private suspend fun unwrapUnit(response: retrofit2.Response<Unit>) {
        if (response.isSuccessful) return
        throw AuthException(
            response.code(),
            ApiErrorParser.fromFailedResponse(response.code(), response.errorBody()?.string())
        )
    }
}
