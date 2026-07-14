package edu.cit.lim.gymtrack.mobile.feature.dashboard

import edu.cit.lim.gymtrack.mobile.data.model.DashboardStatsResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class DashboardRepository(private val apiService: ApiService) {

    suspend fun dashboardStats(): DashboardStatsResponse =
        ApiResponses.unwrap(apiService.dashboardStats())
}
