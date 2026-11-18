package day.vitayuzu.neodb.ui.page.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.NeoDBRepository
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.BASE_URL
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NeoDBRepository,
    authRepository: AuthRepository,
) : ViewModel() {
    val uiState: StateFlow<SearchUiState>
        field = MutableStateFlow(SearchUiState())

    private val query = MutableStateFlow("")

    val instanceName = authRepository.accountStatus.value.instanceUrl
        .ifEmpty { BASE_URL }

    init {
        viewModelScope.launch {
            query
                .debounce(500)
                .filterNot { it.isEmpty() }
                .distinctUntilChanged()
                .collectLatest { search(it) }
        }
    }

    fun onQueryChanged(query: String) {
        this.query.value = query
    }

    private suspend fun search(query: String) {
        val seenUuids = mutableSetOf<String>()
        uiState.value = SearchUiState(isSearching = true)
        try {
            repository.searchWithKeyword(query).collect { page ->
                val entries = page.data.mapNotNull { entry ->
                    if (seenUuids.add(entry.uuid)) Entry(entry) else null
                }
                uiState.update { it.copy(results = it.results + entries) }
            }
        } finally {
            uiState.update { it.copy(isSearching = false) }
        }
    }
}

data class SearchUiState(
    val results: List<Entry> = emptyList(),
    val isSearching: Boolean = false,
)
