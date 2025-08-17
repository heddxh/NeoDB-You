package day.vitayuzu.neodb.ui.page.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AppSettings
import day.vitayuzu.neodb.data.AppSettingsManager
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.VERBOSE_LOG
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.UpdateRepository
import day.vitayuzu.neodb.data.schema.UserSchema
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val updateRepo: UpdateRepository,
    private val appSettingsManager: AppSettingsManager,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        authRepo.accountStatus,
        updateRepo.checkUpdateFlow.onStart { emit(null) }, // Start combination asap.
        appSettingsManager.settingsFlow,
    ) { (isLogin, _, schema), appVersionData, appSettings ->
        if (isLogin && schema != null) {
            SettingsUiState(
                isLogin = true,
                url = schema.url,
                avatar = schema.avatar,
                username = schema.displayName,
                fediAccount = schema.getFediAccount(),
                newVersionUrl = appVersionData?.htmlUrl,
                appSettings = appSettings,
            )
        } else {
            SettingsUiState(
                isLogin = false,
                newVersionUrl = appVersionData?.htmlUrl,
                appSettings = appSettings,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(),
    )

    fun logout() {
        viewModelScope.launch {
            authRepo.revoke()
            Log.d("SettingsViewModel", "Logout successfully")
        }
    }

    fun checkUpdate() {
        viewModelScope.launch { updateRepo.checkUpdateFlow.collect() }
    }

    fun onToggleVerboseLog(enabled: Boolean) {
        viewModelScope.launch {
            appSettingsManager.store(VERBOSE_LOG, enabled)
        }
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
    val isLogin: Boolean = false,
    val url: String = "",
    val avatar: String? = null,
    val username: String = "",
    val fediAccount: String? = null,
    val newVersionUrl: String? = null,
    val appSettings: AppSettings = AppSettings(),
)
