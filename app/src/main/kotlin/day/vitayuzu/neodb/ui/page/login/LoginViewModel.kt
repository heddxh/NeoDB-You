package day.vitayuzu.neodb.ui.page.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
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
            authRepo.validateInstanceUrl(instanceUrl).onSuccess { instanceInfo ->
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
}

data class LoginUiState(
    val isFetchingInstanceInfo: Boolean = true,
    val url: String = "",
    val name: String = "",
    val version: String = "",
    val peopleCount: Int = 0,
)
