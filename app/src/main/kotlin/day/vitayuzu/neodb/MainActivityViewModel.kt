package day.vitayuzu.neodb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.Repository
import day.vitayuzu.neodb.ui.model.Entry
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel scoped to [MainActivity], responsible for authentication and scaffold level UI state.
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val neoRepository: Repository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            authRepository.updateAccountStatus()
        }
    }

    fun search(keywords: String): Flow<List<Entry>> =
        neoRepository.searchWithKeyword(keywords).map { searchResult ->
            searchResult.data.map { Entry(it) }
        }
}
