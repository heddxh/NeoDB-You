package day.vitayuzu.neodb.ui.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.NeoDBRepository
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.EntryType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val neoDBRepository: NeoDBRepository,
    authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        // Refresh data when login status changes.
        @OptIn(ExperimentalCoroutinesApi::class)
        authRepository.accountStatus
            .onSubscription { updateTrending() }
            .chunked(2)
            .onEach {
                if (it.first().isLogin != it.last().isLogin) updateTrending()
            }.launchIn(viewModelScope)
    }

    fun updateTrending() {
        viewModelScope.launch {
            refreshJob?.cancelAndJoin()
            // Should after job cancellation since it will toggle it to false
            _uiState.update { it.copy(isLoading = true, data = emptyMap()) }
            refreshJob = viewModelScope.launch {
                val enabledTrendingType =
                    listOf(
                        EntryType.book,
                        EntryType.movie,
                        EntryType.tv,
                        EntryType.music,
                        EntryType.game,
                        EntryType.podcast,
                    )
                enabledTrendingType.forEach { type ->
                    neoDBRepository
                        .fetchTrendingByEntryType(type)
                        .onEach { schemaList ->
                            _uiState.update { curr ->
                                curr.copy(
                                    data = curr.data + mapOf(
                                        type to schemaList.map { Entry(it) },
                                    ),
                                )
                            }
                        }.collect()
                }
            }
            refreshJob?.invokeOnCompletion { _uiState.update { it.copy(isLoading = false) } }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val data: Map<EntryType, List<Entry>> = emptyMap(),
    val book: List<Entry> = emptyList(),
    val movie: List<Entry> = emptyList(),
    val tv: List<Entry> = emptyList(),
    val music: List<Entry> = emptyList(),
    val game: List<Entry> = emptyList(),
    val podcast: List<Entry> = emptyList(),
)
