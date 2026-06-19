package day.vitayuzu.neodb.ui.page.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AppSettings
import day.vitayuzu.neodb.data.AppSettingsManager
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.HOME_TRENDING_TYPES
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.LIBRARY_SHELF_TYPE
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.VERBOSE_LOG
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.OtherRepository
import day.vitayuzu.neodb.data.UserPreference
import day.vitayuzu.neodb.data.UserPreferenceManager
import day.vitayuzu.neodb.data.schema.UserSchema
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val otherRepo: OtherRepository,
    private val appSettingsManager: AppSettingsManager,
    private val userPreferenceManager: UserPreferenceManager,
) : ViewModel() {

    private val refreshing = MutableStateFlow(false)
    private val checkUpdateTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val newVersionUrlFlow: StateFlow<String?> = merge(
        appSettingsManager.appSettings
            .map { it.checkUpdate }
            .distinctUntilChanged()
            .filter { it }, // false -> true
        checkUpdateTrigger,
    ).flatMapLatest {
        otherRepo.checkUpdateFlow.map { it?.htmlUrl }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        authRepo.accountStatus,
        appSettingsManager.appSettings,
        newVersionUrlFlow,
        userPreferenceManager.preference,
        refreshing,
    ) { (val isLogin, val account), appSettings, newVersionUrl, userPreference, refreshing ->
        // Double check account to ensure it actually loaded the info
        // see [updateAccountStatus]
        if (isLogin && account != null) {
            SettingsUiState(
                refreshing = refreshing,
                isLogin = true,
                url = account.url,
                avatar = account.avatar,
                username = account.displayName,
                fediAccount = account.getFediAccount(),
                newVersionUrl = newVersionUrl,
                appSettings = appSettings,
                userPreference = userPreference,
            )
        } else {
            SettingsUiState(
                refreshing = refreshing,
                isLogin = false,
                newVersionUrl = newVersionUrl,
                appSettings = appSettings,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(),
    )

    // Check update, refresh account info and fetch user preference
    fun refresh() {
        refreshing.value = true
        checkUpdate()
        // We don't care about `checkUpdate` status
        viewModelScope.launch {
            listOf(
                launch { userPreferenceManager.refresh() },
                launch { authRepo.updateAccountStatus() }
            ).joinAll()
            refreshing.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepo.revoke()
            Log.d("SettingsViewModel", "Logout successfully")
        }
    }

    fun onToggleCheckUpdate(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsManager.store(AppSettingsManager.CHECK_UPDATE, enabled)
        }
    }

    fun onToggleVerboseLog(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsManager.store(VERBOSE_LOG, enabled)
        }
    }

    fun onChangeShelfType(shelfType: ShelfType) {
        viewModelScope.launch { appSettingsManager.store(LIBRARY_SHELF_TYPE, shelfType.name) }
    }

    fun onChangeEntryTypes(types: List<EntryType>) {
        viewModelScope.launch {
            appSettingsManager.store(HOME_TRENDING_TYPES, types)
        }
    }

    fun checkUpdate() {
        checkUpdateTrigger.tryEmit(Unit)
    }

    private companion object {
        fun UserSchema.getFediAccount(): String? {
            val rawAddress = this.externalAccounts.find { it.platform == "mastodon" }?.handle
            return if (rawAddress == null) {
                null
            } else {
                "@$rawAddress"
            }
        }
    }
}

data class SettingsUiState(
    val refreshing: Boolean = false,
    val isLogin: Boolean = false,
    val url: String = "",
    val avatar: String? = null,
    val username: String = "",
    val fediAccount: String? = null,
    val newVersionUrl: String? = null,
    val appSettings: AppSettings = AppSettings(),
    val userPreference: UserPreference = UserPreference()
)
