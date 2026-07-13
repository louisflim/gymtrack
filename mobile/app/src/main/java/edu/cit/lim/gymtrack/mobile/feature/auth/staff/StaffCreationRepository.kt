package edu.cit.lim.gymtrack.mobile.feature.auth.staff

import edu.cit.lim.gymtrack.mobile.data.model.CreateStaffRequest
import edu.cit.lim.gymtrack.mobile.data.model.StaffAccountResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiErrorParser
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

class StaffCreationRepository(
    private val apiService: ApiService
) {
    suspend fun createStaff(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): StaffAccountResponse {
        val response = apiService.createStaff(
            CreateStaffRequest(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                email = email.trim(),
                password = password
            )
        )
        if (response.isSuccessful) {
            return response.body()
                ?: throw AuthException(response.code(), ApiErrorParser.EMPTY)
        }

        val message = ApiErrorParser.parse(
            response.errorBody()?.string(),
            response.code(),
            defaultErrorMessage(response.code())
        )
        throw AuthException(response.code(), message)
    }

    private fun defaultErrorMessage(statusCode: Int): String = when (statusCode) {
        403 -> "You don't have permission to do that."
        400 -> ApiErrorParser.CHECK_DETAILS
        else -> ApiErrorParser.GENERIC
    }
}
