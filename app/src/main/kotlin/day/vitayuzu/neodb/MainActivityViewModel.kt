package day.vitayuzu.neodb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel scoped to [MainActivity], responsible for authentication and scaffold level UI state.
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.updateAccountStatus()
        }
    }

    fun dismissComposeModal() {
        _uiState.update { it.copy(isShowComposeModal = false) }
    }
}

data class MainActivityUiState(val isShowComposeModal: Boolean = false)
