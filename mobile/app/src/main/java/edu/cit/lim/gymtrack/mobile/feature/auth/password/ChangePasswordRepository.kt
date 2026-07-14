package edu.cit.lim.gymtrack.mobile.feature.auth.password

import edu.cit.lim.gymtrack.mobile.data.local.SessionDataStore
import edu.cit.lim.gymtrack.mobile.data.model.ChangePasswordRequest
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class ChangePasswordRepository(
    private val sessionDataStore: SessionDataStore,
    private val apiService: ApiService
) {
    suspend fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        ApiResponses.unwrapUnit(
            apiService.changePassword(
                ChangePasswordRequest(currentPassword, newPassword, confirmPassword)
            )
        )
        sessionDataStore.setMustChangePassword(false)
    }
}
