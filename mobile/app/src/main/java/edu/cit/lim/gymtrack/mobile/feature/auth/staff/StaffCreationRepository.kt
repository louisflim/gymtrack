package edu.cit.lim.gymtrack.mobile.feature.auth.staff

import edu.cit.lim.gymtrack.mobile.data.model.CreateStaffRequest
import edu.cit.lim.gymtrack.mobile.data.model.StaffAccountResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class StaffCreationRepository(
    private val apiService: ApiService
) {
    suspend fun createStaff(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): StaffAccountResponse =
        ApiResponses.unwrap(
            apiService.createStaff(
                CreateStaffRequest(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = email.trim(),
                    password = password
                )
            )
        )
}
