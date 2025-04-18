package day.vitayuzu.neodb.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import day.vitayuzu.neodb.data.schema.AuthClientIdentify
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Local storage for user preferences, powered by [DataStore].
 * Store function should handle exception on call site,
 * get function will return null if error occurred.
 * NOTE: [DataStore] is moved to `Dispatchers.IO` under the hood.
 */
class LocalPreferenceSource @Inject constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun storeAuthCode(code: String) {
        dataStore.edit { it[AUTH_CODE] = code }
    }

    suspend fun storeAccessToken(token: String) {
        dataStore.edit {
            it[ACCESS_TOKEN] = token
        }
    }

    suspend fun storeAuthClientIdentify(identify: AuthClientIdentify) {
        dataStore.edit {
            it[CLIENT_ID] = identify.clientId
            it[CLIENT_SECRET] = identify.clientSecret
        }
    }

    suspend fun getAuthClientId(): String? = dataStore.data
        .map { it[CLIENT_ID] }
        .catch {
            Log.e("AuthRepository", "Error while reading preferences", it)
            emit(null)
        }.firstOrNull()

    suspend fun getAuthClientSecret(): String? = dataStore.data
        .map { it[CLIENT_SECRET] }
        .catch {
            Log.e("AuthRepository", "Error while reading preferences", it)
            emit(null)
        }.firstOrNull()

    suspend fun getAccessToken(): String? = dataStore.data
        .map { it[ACCESS_TOKEN] }
        .catch {
            Log.e("AuthRepository", "Error while reading preferences", it)
            emit(null)
        }.firstOrNull()

    private companion object {
        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")
        val AUTH_CODE = stringPreferencesKey("auth_code")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
    }
}
