package day.vitayuzu.neodb.ui.page.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchInstanceInfo(instanceUrl: String) {
        _uiState.update { it.copy(isFetchingInstanceInfo = true) }
        viewModelScope.launch {
            val normalizedUrl = instanceUrl.toNormalizedUrl()
            authRepo.validateInstanceUrl(normalizedUrl).onSuccess { instanceInfo ->
                _uiState.update {
                    LoginUiState(
                        isFetchingInstanceInfo = false,
                        url = instanceInfo.uri,
                        name = instanceInfo.title,
                        version = instanceInfo.version,
                        peopleCount = instanceInfo.stats.userCount,
                    )
                }
            }
        }
    }

    // FIXME
    suspend fun getClientIdentity() = viewModelScope
        .async {
            authRepo.registerClientIfNeeded(_uiState.value.url).getOrNull()
        }.await()

    /**
     * Called from [OauthActivity][day.vitayuzu.neodb.OauthActivity].
     * Since `OauthActivity` creates a new instance of this ViewModel,
     * the instance URL needs to be retrieved from the [AuthRepository] rather than the UI state.
     */
    fun handleAuthCode(code: String) = viewModelScope.launch {
        val instanceUrl = authRepo.instanceUrl
        if (instanceUrl == null) {
            Log.e("LoginViewModel", "No instance url found")
            return@launch
        }
        _uiState.update { it.copy(isExchangingAccessToken = true) }
        authRepo.registerClientIfNeeded(instanceUrl).onSuccess { (clientId, clientSecret) ->
            authRepo
                .exchangeAccessToken(clientId, clientSecret, code)
                .collect { result ->
                    if (result) {
                        Log.d("LoginViewModel", "Successfully exchanged access token")
                        _uiState.update { it.copy(isLogin = true) }
                    } else {
                        Log.e("LoginViewModel", "Failed to exchange access token")
                    }
                }
        }
        _uiState.update { it.copy(isExchangingAccessToken = false) }
    }

    private companion object {
        val schemeRegex by lazy { Regex("^https?://") }
        val trailingSlashRegex by lazy { Regex("/$") }

        /**
         * Remove scheme and trailing slash from url string.
         */
        fun String.toNormalizedUrl(): String =
            this.replaceFirst(schemeRegex, "").replaceFirst(trailingSlashRegex, "")
    }
}

data class LoginUiState(
    val isFetchingInstanceInfo: Boolean = true,
    val url: String = "", // Normalized instance url
    val name: String = "",
    val version: String = "",
    val peopleCount: Int = 0,
    val isExchangingAccessToken: Boolean = false,
    val isLogin: Boolean = false,
)
