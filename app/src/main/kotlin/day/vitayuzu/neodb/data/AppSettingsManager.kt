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
import day.vitayuzu.neodb.util.toSupportedTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import java.util.Locale
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
        }.map { it.toAppSettings() }.stateIn(
            scope = scope,
            started = WhileSubscribed(5000),
            initialValue = AppSettings(),
        )

    /** Reads the persisted snapshot; [appSettings] may still hold its initial value without collectors. */
    suspend fun currentSettings(): AppSettings = dataStore.data
        .catch { e ->
            emit(emptyPreferences())
            Log.e("LocalSettingsManager", "Error while reading preferences", e)
        }.map { it.toAppSettings() }
        .first()

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

    suspend fun storeContentLanguagePreference(
        source: ContentLanguageSource,
        customLanguage: String,
    ) {
        dataStore.edit {
            it[CONTENT_LANGUAGE_SOURCE] = source.name
            it[CUSTOM_CONTENT_LANGUAGE] = customLanguage
        }
    }

    private fun Preferences.toAppSettings(): AppSettings {
        val homeTrendingTypes = getAsList<EntryType>(HOME_TRENDING_TYPES)
        val libraryShelfType = this[LIBRARY_SHELF_TYPE]?.let { stored ->
            ShelfType.entries.find { it.name == stored }
        }
        val languageSource = this[CONTENT_LANGUAGE_SOURCE]?.let { stored ->
            ContentLanguageSource.entries.find { it.name == stored }
        }

        return AppSettings(
            homeTrendingTypes = homeTrendingTypes,
            libraryShelfType = libraryShelfType,
            contentLanguageSource = languageSource,
            customContentLanguage = this[CUSTOM_CONTENT_LANGUAGE],
            verboseLog = this[VERBOSE_LOG],
            checkUpdate = this[CHECK_UPDATE],
        )
    }

    private inline fun <reified T> Preferences.getAsList(
        key: Preferences.Key<String>,
    ): List<T> = this[key]?.let { encoded ->
        runCatching { Json.decodeFromString<List<T>>(encoded) }
            .onFailure {
                Log.e(
                    "LocalSettingsManager",
                    "Error while reading preferences ${key.name}",
                    it,
                )
            }.getOrDefault(emptyList())
    } ?: emptyList()

    companion object {
        val INSTANCE_URL = stringPreferencesKey("instance_url")
        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")

        // Settings
        val HOME_TRENDING_TYPES = stringPreferencesKey("home_trending_types")
        val LIBRARY_SHELF_TYPE = stringPreferencesKey("library_shelf_type")
        val CONTENT_LANGUAGE_SOURCE = stringPreferencesKey("content_language_source")
        val CUSTOM_CONTENT_LANGUAGE = stringPreferencesKey("custom_content_language")

        // TODO: Control global log level
        val VERBOSE_LOG = booleanPreferencesKey("verbose_log")
        val CHECK_UPDATE = booleanPreferencesKey("check_update")

        // Onboarding
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}

@Suppress("ktlint:standard:max-line-length")
data class AppSettings(
    val homeTrendingTypes: List<EntryType> = emptyList(), // enabled trending types for home
    val libraryShelfType: ShelfType = ShelfType.wishlist, // preferred/default shelf type for library
    val contentLanguageSource: ContentLanguageSource = ContentLanguageSource.Server,
    val customContentLanguage: String = Locale.getDefault().toSupportedTag(),
    val verboseLog: Boolean = false,
    val checkUpdate: Boolean = false, // disabled by default
) {
    // Make null fields have default value
    constructor(
        homeTrendingTypes: List<EntryType>?,
        libraryShelfType: ShelfType?,
        contentLanguageSource: ContentLanguageSource?,
        customContentLanguage: String?,
        verboseLog: Boolean?,
        checkUpdate: Boolean?,
    ) : this(
        homeTrendingTypes ?: emptyList(),
        libraryShelfType ?: ShelfType.progress,
        contentLanguageSource ?: ContentLanguageSource.Server,
        customContentLanguage ?: Locale.getDefault().toSupportedTag(),
        verboseLog ?: false,
        checkUpdate ?: false,
    )
}

enum class ContentLanguageSource { Server, App, User }

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
