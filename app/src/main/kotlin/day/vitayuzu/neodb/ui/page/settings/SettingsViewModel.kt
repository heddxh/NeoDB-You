package day.vitayuzu.neodb.ui.page.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.schema.UserSchema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {

    @Inject lateinit var authFlowFactory: AndroidCodeAuthFlowFactory

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            val isLogin = repo.isLogin.get()
            if (isLogin) {
                repo.fetchSelfAccountInfo().collect { schema ->
                    _uiState.update {
                        it.copy(
                            isLogin = true,
                            url = schema.url,
                            avatar = schema.avatar,
                            username = schema.displayName,
                            fediAccount = schema.getFediAccount(),
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isLogin = false) }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            // Clean persistence credential
            repo.revoke()

            val client = repo.registerAppIfNeeded().getOrThrow()
            val flow = authFlowFactory.createAuthFlow(client)
            try {
                val tokens = flow.getAccessToken()
                repo.storeAccessToken(tokens.access_token)
                _uiState.update { it.copy(isLogin = true) }
                refresh() // fetch user info
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to login", e)
            }
        }
    }

    fun logout() {
        if (!repo.isLogin.get()) {
            Log.d("SettingsViewModel", "Already logged out, do nothing")
        }
        viewModelScope.launch {
            val client = repo.registerAppIfNeeded().getOrThrow()
            val code = client.revokeToken(repo.getAccessToken().orEmpty())
            if (code.value == 200) {
                repo.revoke()
                _uiState.update { it.copy(isLogin = false) }
                Log.d("SettingsViewModel", "Logout successfully")
            } else {
                Log.d("SettingsViewModel", "Logout failed: $code")
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
)

fun UserSchema.getFediAccount(): String? {
    val rawAddress = this.externalAccounts.find { it.platform == "mastodon" }?.handle
    return if (rawAddress == null) {
        null
    } else {
        "@$rawAddress"
    }
}
