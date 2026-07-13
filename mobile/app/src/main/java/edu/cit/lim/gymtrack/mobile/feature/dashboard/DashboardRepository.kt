package edu.cit.lim.gymtrack.mobile.feature.dashboard

import edu.cit.lim.gymtrack.mobile.data.model.DashboardStatsResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

class DashboardRepository(private val apiService: ApiService) {

    suspend fun dashboardStats(): DashboardStatsResponse =
        unwrap(apiService.dashboardStats())

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
