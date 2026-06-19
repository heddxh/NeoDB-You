package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.AppScope
import day.vitayuzu.neodb.data.schema.UserPreferenceSchema
import day.vitayuzu.neodb.util.EntryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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
    authRepo: AuthRepository,
    @AppScope scope: CoroutineScope,
) {

    val preference: StateFlow<UserPreference>
        field = MutableStateFlow(UserPreference())

    init {
        authRepo.accountStatus
            .distinctUntilChangedBy { it.isLogin }
            .onEach {
                if (it.isLogin) {
                    refresh()
                } else {
                    reset()
                }
            }.launchIn(scope)
    }

    private suspend fun refresh() {
        runCatching {
            Log.d("UserPreferenceRepository", "Start fetching user preference")
            preference.update { it.copy(loading = true) }
            remoteSource.fetchSelfPreference()
        }.onSuccess { schema ->
            Log.d("UserPreferenceRepository", "End fetching user preference:$schema")
            preference.value = UserPreference(schema)
        }.onFailure { error ->
            Log.e("UserPreferenceRepository", "Error fetching user preference: $error")
        }
        preference.update { it.copy(loading = false) }
    }

    private fun reset() {
        preference.value = UserPreference()
    }
}

data class UserPreference(
    val loading: Boolean = false,
    val crossPost: Boolean = true,
    val visibility: Int = 0,
    val hiddenSearchCategories: List<EntryType> = emptyList(),
    val language: String = Locale.getDefault().toLanguageTag(),
) {
    constructor(schema: UserPreferenceSchema) : this(
        loading = false,
        crossPost = schema.crossPost,
        visibility = schema.visibility,
        hiddenSearchCategories = schema.hiddenSearchCategories,
        language = schema.language,
    )
}
