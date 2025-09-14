package day.vitayuzu.neodb.ui.page.search

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.NeoDBRepository
import day.vitayuzu.neodb.ui.model.Entry
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(val repository: NeoDBRepository) : ViewModel() {
    val searchResult = mutableStateListOf<Entry>()

    private var previousQuery = ""

    fun onSearch(query: String) {
        if (query == previousQuery) return
        previousQuery = query
        searchResult.clear()
        repository
            .searchWithKeyword(query)
            .map { result ->
                result.data.map { Entry(it) }
            }.onEach { searchResult.addAll(it) }
            .launchIn(viewModelScope)
    }
}
