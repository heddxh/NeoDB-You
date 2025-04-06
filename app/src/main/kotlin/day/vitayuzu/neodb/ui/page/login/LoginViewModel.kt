package day.vitayuzu.neodb.ui.page.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import day.vitayuzu.neodb.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var client: OpenIdConnectClient

    init {
        viewModelScope.launch {
            client = authRepository.registerAppIfNeeded().getOrThrow()
        }
    }

    fun login(authFlowFactory: AndroidCodeAuthFlowFactory) {
        val flow = authFlowFactory.createAuthFlow(client)
        viewModelScope.launch {
            val tokens = flow.getAccessToken()
            Log.d("LoginViewModel", "Tokens: $tokens")
        }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val username: String = "",
    val password: String = "",
)
