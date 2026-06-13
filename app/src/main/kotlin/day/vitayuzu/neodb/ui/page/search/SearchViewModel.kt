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
    authRepository: AuthRepository,
) : ViewModel() {
    val searchResult = mutableStateListOf<Entry>()
    private val uuids = mutableSetOf<String>()

    var isSearching by mutableStateOf(false)
    private var searchJob: Job? = null

    val instanceName = authRepository.accountStatus.value.instanceUrl
        .ifEmpty { BASE_URL }

    fun onSearch(query: String) {
        searchJob?.cancel()
        searchResult.clear()
        uuids.clear()
        isSearching = true
        searchJob = repository
            .searchWithKeyword(query)
            .map { result ->
                result.data.mapNotNull {
                    // Search result may contain duplicated entries among different pages
                    if (uuids.add(it.uuid)) Entry(it) else null
                }
            }.onEach {
                searchResult.addAll(it)
            }.onCompletion {
                isSearching = false
            }.launchIn(viewModelScope)
    }
}
