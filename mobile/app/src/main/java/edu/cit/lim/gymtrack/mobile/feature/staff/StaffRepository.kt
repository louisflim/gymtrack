package edu.cit.lim.gymtrack.mobile.feature.staff

import edu.cit.lim.gymtrack.mobile.data.model.StaffResponse
import edu.cit.lim.gymtrack.mobile.data.model.StaffUpdateRequest
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class StaffRepository(private val apiService: ApiService) {

    suspend fun staff(): List<StaffResponse> =
        ApiResponses.unwrap(apiService.staffList())

    suspend fun updateStaff(id: Long, request: StaffUpdateRequest): StaffResponse =
        ApiResponses.unwrap(apiService.updateStaff(id, request))

    suspend fun deleteStaff(id: Long) {
        ApiResponses.unwrapUnit(apiService.deleteStaff(id))
    }
}
