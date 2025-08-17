package day.vitayuzu.neodb.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Local storage for user preferences, powered by [DataStore].
 * Store function should handle exception on call site,
 * get function will return null if error occurred.
 * NOTE: [DataStore] has used `Dispatchers.IO` under the hood.
 */
class AppSettingsManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    val settingsFlow: Flow<AppSettings> = dataStore.data
        .catch { e ->
            emit(emptyPreferences())
            Log.e("LocalSettingsManager", "Error while reading preferences", e)
        }.map { preferences ->
            val verboseLog = preferences[VERBOSE_LOG] ?: false
            AppSettings(verboseLog)
        }

    /**
     * Delete all local authentication.
     */
    suspend fun deleteAllAuthData() {
        dataStore.edit {
            it.remove(CLIENT_ID)
            it.remove(CLIENT_SECRET)
            it.remove(ACCESS_TOKEN)
            it.remove(INSTANCE_URL)
        }
    }

    // Utility functions, only IOException will be caught.
    suspend fun <T> get(key: Preferences.Key<T>) = dataStore.data
        .map { it[key] }
        .catch {
//            if (it !is IOException) throw it // rethrow all but IOException
            Log.e("AuthRepository", "Error while reading preferences ${key.name}", it)
        }.firstOrNull()

    suspend fun <T> store(key: Preferences.Key<T>, value: T) {
        runCatching {
            dataStore.edit {
                it[key] = value
            }
        }.onFailure {
//            if (it !is IOException) throw it // rethrow all but IOException
            Log.e("AuthRepository", "Error while editing preferences ${key.name}", it)
        }
    }

    /**
     * Helper function to get all auth data from local storage,
     * or null if some of them are missing.
     */
    suspend fun getAllAuthData() = listOfNotNull(
        get(INSTANCE_URL),
        get(CLIENT_ID),
        get(CLIENT_SECRET),
        get(ACCESS_TOKEN),
    ).let { if (it.size == 4) it else null }

    companion object {
        val INSTANCE_URL = stringPreferencesKey("instance_url")
        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")

        // Settings
        val VERBOSE_LOG = booleanPreferencesKey("verbose_log")
    }
}

data class AppSettings(val verboseLog: Boolean = false)
