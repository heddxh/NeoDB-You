package day.vitayuzu.neodb.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import day.vitayuzu.neodb.AppScope
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType
import day.vitayuzu.neodb.util.USER_PREFERENCES
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local storage for user preferences, powered by [DataStore].
 * Store function should handle exception on call site,
 * get function will return null if error occurred.
 * NOTE: [DataStore] has used `Dispatchers.IO` under the hood.
 */
@Singleton
class AppSettingsManager @Inject constructor(
    val dataStore: DataStore<Preferences>,
    @AppScope private val scope: CoroutineScope,
) {

    val appSettings: StateFlow<AppSettings> = dataStore.data
        .catch { e ->
            emit(emptyPreferences())
            Log.e("LocalSettingsManager", "Error while reading preferences", e)
        }.map { preferences ->
            val homeTrendingTypes = getAsList<EntryType>(HOME_TRENDING_TYPES)
            val libraryShelfType =
                ShelfType.valueOf(preferences[LIBRARY_SHELF_TYPE] ?: ShelfType.progress.name)
            val verboseLog = preferences[VERBOSE_LOG] ?: false
            AppSettings(homeTrendingTypes, libraryShelfType, verboseLog)
        }.stateIn(
            scope = scope,
            started = WhileSubscribed(5000),
            initialValue = AppSettings(),
        )

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

    // Helper functions to get auth data.
    suspend fun <T> getAuthData(key: Preferences.Key<T>) = dataStore.data
        .map { it[key] }
        .catch {
//            if (it !is IOException) throw it // rethrow all but IOException
            Log.e("AuthRepository", "Error while reading preferences ${key.name}", it)
        }.firstOrNull()

    /**
     * Helper function to get all auth data from local storage,
     * or null if some of them are missing.
     */
    suspend fun getAllAuthData() = listOfNotNull(
        getAuthData(INSTANCE_URL),
        getAuthData(CLIENT_ID),
        getAuthData(CLIENT_SECRET),
        getAuthData(ACCESS_TOKEN),
    ).let { if (it.size == 4) it else null }

    private suspend inline fun <reified T> getAsList(key: Preferences.Key<String>): List<T> =
        dataStore.data
            .map { Json.decodeFromString<List<T>>(it[key] ?: "[]") }
            .catch { Log.e("AuthRepository", "Error while reading preferences ${key.name}", it) }
            .first()

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

    suspend inline fun <reified T> store(key: Preferences.Key<String>, value: List<T>) {
        runCatching {
            dataStore.edit {
                it[key] = Json.encodeToString(value)
            }
        }.onFailure {
            Log.e("AuthRepository", "Error while editing preferences ${key.name}", it)
        }
    }

    companion object {
        val INSTANCE_URL = stringPreferencesKey("instance_url")
        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")

        // Settings
        val HOME_TRENDING_TYPES = stringPreferencesKey("home_trending_types")
        val LIBRARY_SHELF_TYPE = stringPreferencesKey("library_shelf_type")

        // TODO: Control global log level
        val VERBOSE_LOG = booleanPreferencesKey("verbose_log")

        // Onboarding
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}

@Suppress("ktlint:standard:max-line-length")
data class AppSettings(
    val homeTrendingTypes: List<EntryType> = emptyList(), // enabled trending types for home
    val libraryShelfType: ShelfType = ShelfType.wishlist, // preferred/default shelf type for library
    val verboseLog: Boolean = false,
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun providerDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) },
    )
}
