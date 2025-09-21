package day.vitayuzu.neodb.ui.page.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.NeoDBRepository
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.BASE_URL
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val repository: NeoDBRepository,
    val authRepository: AuthRepository,
) : ViewModel() {
    val searchResult = mutableStateListOf<Entry>()
    var isSearching by mutableStateOf(false)

    private var previousQuery = ""
    private var searchJob: Job? = null

    val instanceName = authRepository.accountStatus.value.instanceUrl
        .ifEmpty { BASE_URL }

    fun onSearch(query: String) {
        if (query == previousQuery) return

        isSearching = true
        searchJob?.cancel()
        previousQuery = query
        searchResult.clear()
        searchJob = repository
            .searchWithKeyword(query)
            .map { result ->
                result.data.map { Entry(it) }
            }.onEach { searchResult.addAll(it) }
            .onCompletion { isSearching = false }
            .launchIn(viewModelScope)
    }
}
