package edu.cit.lim.gymtrack.mobile.feature.staff

import edu.cit.lim.gymtrack.mobile.data.model.StaffResponse
import edu.cit.lim.gymtrack.mobile.data.model.StaffUpdateRequest
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

class StaffRepository(private val apiService: ApiService) {

    suspend fun staff(): List<StaffResponse> =
        unwrap(apiService.staffList())

    suspend fun updateStaff(id: Long, request: StaffUpdateRequest): StaffResponse =
        unwrap(apiService.updateStaff(id, request))

    private suspend fun <T> unwrap(response: retrofit2.Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw AuthException(response.code(), "Empty response from server.")
        }
        val message = response.errorBody()?.string()?.trim('"').orEmpty()
            .ifBlank { "Request failed (${response.code()})." }
        throw AuthException(response.code(), message)
    }
}
