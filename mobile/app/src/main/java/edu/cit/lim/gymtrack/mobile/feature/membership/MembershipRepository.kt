package edu.cit.lim.gymtrack.mobile.feature.membership

import edu.cit.lim.gymtrack.mobile.data.model.MembershipResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class MembershipRepository(private val apiService: ApiService) {

    suspend fun myMembership(): MembershipResponse =
        ApiResponses.unwrap(apiService.myMembership())
}
