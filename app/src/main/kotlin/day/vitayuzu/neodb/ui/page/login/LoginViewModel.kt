package day.vitayuzu.neodb.ui.page.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
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

    /**
     * Register client and return client id as flow.
     */
    fun getClientId() = flow {
        _uiState.update { it.copy(isPreparingOauth = true, isShowTextField = false) }
        authRepo.registerClientIfNeeded(uiState.value.url).onSuccess {
            kotlinx.coroutines.delay(3000)
            emit(it.first)
        }
    }.onCompletion { _uiState.update { it.copy(isPreparingOauth = false) } }

    fun handleAuthCode(code: String) = flow<Unit> {
        // Called from [OauthActivity][day.vitayuzu.neodb.OauthActivity].
        // Since `OauthActivity` creates a new instance of this ViewModel,
        // the instance URL needs to be retrieved from the [AuthRepository] rather than the UI state.
        val instanceUrl = authRepo.instanceUrl ?: throw Exception("No instance url found")
        _uiState.update { it.copy(isExchangingAccessToken = true, isShowTextField = false) }
        authRepo.registerClientIfNeeded(instanceUrl).onSuccess { (clientId, clientSecret) ->
            authRepo
                .exchangeAccessToken(clientId, clientSecret, code)
                .collect { result ->
                    if (result) {
                        Log.d("LoginViewModel", "Successfully exchanged access token")
                    } else {
                        throw Exception("No instance url found")
                    }
                }
        }
    }.onCompletion {
        _uiState.update { it.copy(isExchangingAccessToken = false) }
    }

    fun reShowTextField() {
        _uiState.update { it.copy(isShowTextField = true) }
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
    val isShowTextField: Boolean = true,
    val isFetchingInstanceInfo: Boolean = true,
    val url: String = "", // Normalized instance url
    val name: String = "",
    val version: String = "",
    val peopleCount: Int = 0,
    val isPreparingOauth: Boolean = false,
    val isExchangingAccessToken: Boolean = false,
)
