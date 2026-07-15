package edu.cit.lim.gymtrack.mobile.feature.settings

import edu.cit.lim.gymtrack.mobile.data.model.DeleteAccountRequest
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthRepository
import edu.cit.lim.gymtrack.mobile.feature.auth.password.ChangePasswordRepository

/**
 * Account settings: change password + delete account.
 * Reuses [ChangePasswordRepository] for password updates.
 */
class SettingsRepository(
    private val apiService: ApiService,
    private val changePasswordRepository: ChangePasswordRepository,
    private val authRepository: AuthRepository
) {
    suspend fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        changePasswordRepository.changePassword(currentPassword, newPassword, confirmPassword)
    }

    suspend fun deleteAccount(password: String) {
        ApiResponses.unwrapUnit(apiService.deleteAccount(DeleteAccountRequest(password)))
        authRepository.logout()
    }
}
