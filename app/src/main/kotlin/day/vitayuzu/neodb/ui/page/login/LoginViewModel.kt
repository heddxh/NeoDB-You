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

    fun handleAuthCode(code: String) = viewModelScope.launch {
        // Calling from OauthActivity, which create another instance of this viewmodel,
        // so we need to get instance url from auth repo, instead of ui state.
        val instanceUrl = authRepo.instanceUrl
        if (instanceUrl == null) {
            Log.e("LoginViewModel", "No instance url found")
            return@launch
        }
        authRepo.registerClientIfNeeded(instanceUrl).onSuccess { (clientId, clientSecret) ->
            authRepo
                .exchangeAccessToken(clientId, clientSecret, code)
                .collect { _uiState.update { it.copy(isLogin = true) } }
        }
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
    val isLogin: Boolean = false,
)
