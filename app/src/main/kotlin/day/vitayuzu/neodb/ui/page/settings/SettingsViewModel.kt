package day.vitayuzu.neodb.ui.page.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.schema.UserSchema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(newVersionUrl = repo.newVersionUrl.value) }
    }

    fun refresh() {
        viewModelScope.launch {
            repo.accountStatus.collect { (isLogin, schema) ->
                if (isLogin && schema != null) {
                    _uiState.update {
                        it.copy(
                            isLogin = true,
                            url = schema.url,
                            avatar = schema.avatar,
                            username = schema.displayName,
                            fediAccount = schema.getFediAccount(),
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLogin = false) }
                }
            }
        }
    }

    fun logout() {
        if (!repo.accountStatus.value.isLogin) {
            Log.d("SettingsViewModel", "Already logged out, do nothing")
        }
        viewModelScope.launch {
            // TODO: send revoke request
            repo.revoke()
            _uiState.update { it.copy(isLogin = false) }
            Log.d("SettingsViewModel", "Logout successfully")
        }
    }

    fun checkUpdate() {
        viewModelScope.launch {
            repo.checkUpdate().lastOrNull()?.let { versionInfo ->
                _uiState.update { it.copy(newVersionUrl = versionInfo.htmlUrl) }
            }
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
)
