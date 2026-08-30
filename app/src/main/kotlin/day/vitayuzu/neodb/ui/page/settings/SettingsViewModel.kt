package day.vitayuzu.neodb.ui.page.settings

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AppSettings
import day.vitayuzu.neodb.data.AppSettingsManager
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.LIBRARY_SHELF_TYPE
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.VERBOSE_LOG
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.ContentLanguageSource
import day.vitayuzu.neodb.data.OtherRepository
import day.vitayuzu.neodb.data.UserPreferenceManager
import day.vitayuzu.neodb.data.schema.UserSchema
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType
import day.vitayuzu.neodb.util.buildInstanceUri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
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

    val refreshing: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val settingsState: StateFlow<AppSettings?> = appSettingsManager.appSettings

    @OptIn(ExperimentalCoroutinesApi::class)
    val accountState: StateFlow<AccountState?> =
        authRepo.accountStatus.mapLatest { (val isLogin, val instanceUrl, val account) ->
            if (!isLogin || account == null) return@mapLatest null
            AccountState(
                isLogin = true,
                url = buildInstanceUri(instanceUrl, account.url),
                avatar = account.avatar,
                username = account.displayName,
                fediAccount = account.getFediAccount(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    private val checkUpdateTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val updateState: StateFlow<String?> = merge(
        appSettingsManager.appSettings
            .filterNotNull()
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

    fun onChangeEntryType(type: EntryType, enabled: Boolean) {
        viewModelScope.launch {
            appSettingsManager.updateHomeTrendingType(type, enabled)
        }
    }

    fun onChangeContentLanguage(to: ContentLanguageSource, customLanguage: String) {
        viewModelScope.launch {
            appSettingsManager.storeContentLanguagePreference(
                source = to,
                customLanguage = customLanguage,
            )
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

data class AccountState(
    val isLogin: Boolean,
    val url: Uri,
    val avatar: String?,
    val username: String,
    val fediAccount: String?,
)
