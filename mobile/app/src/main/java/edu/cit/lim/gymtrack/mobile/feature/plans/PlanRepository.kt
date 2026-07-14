package edu.cit.lim.gymtrack.mobile.feature.plans

import edu.cit.lim.gymtrack.mobile.data.model.PlanRequest
import edu.cit.lim.gymtrack.mobile.data.model.PlanResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class PlanRepository(private val apiService: ApiService) {

    suspend fun activePlans(): List<PlanResponse> =
        ApiResponses.unwrap(apiService.activePlans())

    suspend fun allPlans(): List<PlanResponse> =
        ApiResponses.unwrap(apiService.allPlans())

    suspend fun createPlan(request: PlanRequest): PlanResponse =
        ApiResponses.unwrap(apiService.createPlan(request))

    suspend fun updatePlan(id: Long, request: PlanRequest): PlanResponse =
        ApiResponses.unwrap(apiService.updatePlan(id, request))
}
