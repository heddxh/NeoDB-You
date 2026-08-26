package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.AppScope
import day.vitayuzu.neodb.data.schema.UserPreferenceSchema
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.toSupportedTag
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repo subscribed to [AuthRepository.accountStatus]
 * fetches user preferences when login or switching accounts.
 */
@Singleton
class UserPreferenceManager @Inject constructor(
    private val remoteSource: RemoteSource,
    private val authRepo: AuthRepository,
    private val appSettingsManager: AppSettingsManager,
    @AppScope scope: CoroutineScope,
) {

    val preference: StateFlow<UserPreference>
        field = MutableStateFlow(UserPreference())

    private val refreshMutex = Mutex()
    private var serverContentLanguage: String? = null

    init {
        authRepo.accountStatus
            .distinctUntilChangedBy { it.isLogin }
            .onEach {
                if (it.isLogin) {
                    ensureServerLanguage()
                } else {
                    reset()
                }
            }.launchIn(scope)
    }

    suspend fun refresh() {
        refreshMutex.withLock { refreshLocked() }
    }

    private suspend fun ensureServerLanguage(): String = refreshMutex.withLock {
        serverContentLanguage ?: refreshLocked()
    }

    private suspend fun refreshLocked(): String {
        serverContentLanguage = null
        Log.d("UserPreferenceRepository", "Start fetching user preference")
        preference.update { it.copy(loading = true) }
        try {
            val language = try {
                val schema = remoteSource.fetchSelfPreference()
                Log.d("UserPreferenceRepository", "End fetching user preference:$schema")
                preference.value = UserPreference(schema)
                schema.language
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("UserPreferenceRepository", "Error fetching user preference: $error")
                preference.value.language
            }
            serverContentLanguage = language
            return language
        } finally {
            preference.update { current -> current.copy(loading = false) }
        }
    }

    suspend fun serverLanguage(): String {
        if (appSettingsManager.getAllAuthData() == null) {
            return Locale.getDefault().toSupportedTag()
        }
        return ensureServerLanguage()
    }

    fun currentLanguage(): String = preference.value.language

    private suspend fun reset() {
        refreshMutex.withLock {
            serverContentLanguage = null
            preference.value = UserPreference()
        }
    }
}

data class UserPreference(
    val loading: Boolean = false,
    val crossPost: Boolean = true,
    val visibility: Int = 0,
    val hiddenSearchCategories: List<EntryType> = emptyList(),
    val language: String = Locale.getDefault().toSupportedTag(),
) {
    constructor(schema: UserPreferenceSchema) : this(
        loading = false,
        crossPost = schema.crossPost,
        visibility = schema.visibility,
        hiddenSearchCategories = schema.hiddenSearchCategories,
        language = schema.language,
    )
}
