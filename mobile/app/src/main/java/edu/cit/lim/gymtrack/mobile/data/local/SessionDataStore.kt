package edu.cit.lim.gymtrack.mobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import edu.cit.lim.gymtrack.mobile.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "gymtrack_session")

class SessionDataStore(private val context: Context) {

    val sessionFlow: Flow<UserSession> = context.sessionDataStore.data.map { prefs ->
        UserSession(
            token = prefs[KEY_TOKEN].orEmpty(),
            userId = prefs[KEY_USER_ID] ?: 0L,
            firstName = prefs[KEY_FIRST_NAME].orEmpty(),
            lastName = prefs[KEY_LAST_NAME].orEmpty(),
            email = prefs[KEY_EMAIL].orEmpty(),
            role = prefs[KEY_ROLE].orEmpty(),
            gymId = prefs[KEY_GYM_ID],
            gymName = prefs[KEY_GYM_NAME],
            mustChangePassword = prefs[KEY_MUST_CHANGE_PASSWORD] ?: false
        )
    }

    suspend fun saveSession(session: UserSession) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = session.token
            prefs[KEY_USER_ID] = session.userId
            prefs[KEY_FIRST_NAME] = session.firstName
            prefs[KEY_LAST_NAME] = session.lastName
            prefs[KEY_EMAIL] = session.email
            prefs[KEY_ROLE] = session.role
            prefs[KEY_MUST_CHANGE_PASSWORD] = session.mustChangePassword
            if (session.gymId != null) {
                prefs[KEY_GYM_ID] = session.gymId
            } else {
                prefs.remove(KEY_GYM_ID)
            }
            if (!session.gymName.isNullOrBlank()) {
                prefs[KEY_GYM_NAME] = session.gymName
            } else {
                prefs.remove(KEY_GYM_NAME)
            }
        }
    }

    suspend fun setMustChangePassword(value: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_MUST_CHANGE_PASSWORD] = value
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_FIRST_NAME = stringPreferencesKey("first_name")
        private val KEY_LAST_NAME = stringPreferencesKey("last_name")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_ROLE = stringPreferencesKey("role")
        private val KEY_GYM_ID = longPreferencesKey("gym_id")
        private val KEY_GYM_NAME = stringPreferencesKey("gym_name")
        private val KEY_MUST_CHANGE_PASSWORD = booleanPreferencesKey("must_change_password")
    }
}
