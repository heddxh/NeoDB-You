package day.vitayuzu.neodb.ui.page.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.schema.UserSchema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isLogin = repo.isLogin.get()
            if (isLogin) {
                repo.fetchMe().collect { schema ->
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
