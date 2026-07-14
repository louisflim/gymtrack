package edu.cit.lim.gymtrack.mobile.feature.members

import edu.cit.lim.gymtrack.mobile.data.model.AssignPlanRequest
import edu.cit.lim.gymtrack.mobile.data.model.MemberResponse
import edu.cit.lim.gymtrack.mobile.data.model.MemberUpdateRequest
import edu.cit.lim.gymtrack.mobile.data.model.MembershipResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class MemberRepository(private val apiService: ApiService) {

    suspend fun members(search: String? = null, status: String? = null): List<MemberResponse> =
        ApiResponses.unwrap(apiService.members(search, status))

    suspend fun updateMember(id: Long, request: MemberUpdateRequest): MemberResponse =
        ApiResponses.unwrap(apiService.updateMember(id, request))

    suspend fun deleteMember(id: Long) {
        ApiResponses.unwrapUnit(apiService.deleteMember(id))
    }

    suspend fun assignPlan(memberId: Long, planId: Long): MembershipResponse =
        ApiResponses.unwrap(apiService.assignPlan(AssignPlanRequest(memberId, planId)))
}
