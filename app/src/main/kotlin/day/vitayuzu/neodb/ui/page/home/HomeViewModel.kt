package day.vitayuzu.neodb.ui.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.NeoDBRepository
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.EntryType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val neoDBRepository: NeoDBRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Initial refresh
        viewModelScope.launch { updateTrending() }
        // Refresh data when login status changes.
        authRepository.accountStatus
            .chunked(2)
            .onEach {
                if (it.first().isLogin != it.last().isLogin) updateTrending()
            }.launchIn(viewModelScope)
    }

    fun updateTrending() {
        _uiState.update { it.copy(isLoading = true) }
        neoDBRepository.serverTrending
            .onEach { result ->
                result.forEach { (type, list) ->
                    _uiState.update { curr ->
                        when (type) {
                            EntryType.book -> curr.copy(book = list.map { Entry(it) })
                            EntryType.movie -> curr.copy(movie = list.map { Entry(it) })
                            EntryType.tv -> curr.copy(tv = list.map { Entry(it) })
                            EntryType.music -> curr.copy(music = list.map { Entry(it) })
                            EntryType.game -> curr.copy(game = list.map { Entry(it) })
                            EntryType.podcast -> curr.copy(podcast = list.map { Entry(it) })
                            else -> curr
                        }
                    }
                }
            }.onCompletion {
                _uiState.update { it.copy(isLoading = false) }
            }.launchIn(viewModelScope)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val book: List<Entry> = emptyList(),
    val movie: List<Entry> = emptyList(),
    val tv: List<Entry> = emptyList(),
    val music: List<Entry> = emptyList(),
    val game: List<Entry> = emptyList(),
    val podcast: List<Entry> = emptyList(),
)
