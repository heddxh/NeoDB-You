package day.vitayuzu.neodb.ui.page.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    @Inject lateinit var authFlowFactory: AndroidCodeAuthFlowFactory

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            if (authRepository.getAccessToken() != null) {
                Log.d("LoginViewModel", "Found access token")
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            }
        }
    }

    fun login() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val client = authRepository.registerAppIfNeeded().getOrThrow()
            val flow = authFlowFactory.createAuthFlow(client)
            val tokens = flow.getAccessToken()
            Log.d("LoginViewModel", "Tokens: $tokens")
            authRepository.storeAccessToken(tokens.access_token)
            _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
        }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
)
