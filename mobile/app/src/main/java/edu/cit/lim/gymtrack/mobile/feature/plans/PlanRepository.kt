package edu.cit.lim.gymtrack.mobile.feature.plans

import edu.cit.lim.gymtrack.mobile.data.model.PlanRequest
import edu.cit.lim.gymtrack.mobile.data.model.PlanResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

class PlanRepository(private val apiService: ApiService) {

    suspend fun activePlans(): List<PlanResponse> =
        unwrap(apiService.activePlans())

    suspend fun allPlans(): List<PlanResponse> =
        unwrap(apiService.allPlans())

    suspend fun createPlan(request: PlanRequest): PlanResponse =
        unwrap(apiService.createPlan(request))

    suspend fun updatePlan(id: Long, request: PlanRequest): PlanResponse =
        unwrap(apiService.updatePlan(id, request))

    private suspend fun <T> unwrap(response: retrofit2.Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw AuthException(response.code(), ApiErrorParser.EMPTY)
        }
        throw AuthException(
            response.code(),
            ApiErrorParser.fromFailedResponse(response.code(), response.errorBody()?.string())
        )
    }
}
