package day.vitayuzu.neodb.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Local storage for user preferences, powered by [DataStore].
 * Store function should handle exception on call site,
 * get function will return null if error occurred.
 * NOTE: [DataStore] has used `Dispatchers.IO` under the hood.
 */
class LocalPreferenceSource @Inject constructor(private val dataStore: DataStore<Preferences>) {

    /**
     * Public immutable cache of instance url.
     * All external access should use this read-only property.
     */
    val instanceUrl get() = _instanceUrl.value
    private val _instanceUrl = MutableStateFlow<String?>(null)

    init {
        runBlocking { _instanceUrl.value = get(INSTANCE_URL) }
    }

    /**
     * Delete all local authentication and set [instanceUrl] to null.
     */
    suspend fun deleteAllAuthData() {
        dataStore.edit {
            it.remove(CLIENT_ID)
            it.remove(CLIENT_SECRET)
            it.remove(ACCESS_TOKEN)
            it.remove(INSTANCE_URL)
            _instanceUrl.value = null
        }
    }

    // Utility functions, only IOException will be caught.
    suspend fun get(key: Preferences.Key<String>) = dataStore.data
        .map { it[key] }
        .catch {
//            if (it !is IOException) throw it // rethrow all but IOException
            Log.e("AuthRepository", "Error while reading preferences ${key.name}", it)
            emit(null)
        }.firstOrNull()

    suspend fun store(key: Preferences.Key<String>, value: String) {
        runCatching {
            dataStore.edit {
                it[key] = value
                if (key == INSTANCE_URL) _instanceUrl.value = value // update cache
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
    }
}
