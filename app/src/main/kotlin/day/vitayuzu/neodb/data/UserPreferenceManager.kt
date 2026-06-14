package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.AppScope
import day.vitayuzu.neodb.data.schema.UserPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        field = MutableStateFlow(UserPreference.Default)

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
            remoteSource.fetchSelfPreference()
        }.onSuccess { schema ->
            Log.d("UserPreferenceRepository", "End fetching user preference:$schema")
            preference.value = schema
        }.onFailure { error ->
            Log.e("UserPreferenceRepository", "Error fetching user preference: $error")
        }
    }

    private fun reset() {
        preference.value = UserPreference.Default
    }
}
