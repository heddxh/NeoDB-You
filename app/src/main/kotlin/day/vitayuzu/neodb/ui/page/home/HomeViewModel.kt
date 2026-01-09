package day.vitayuzu.neodb.ui.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AppSettingsManager
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.NeoDBRepository
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.EntryType
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val neoDBRepository: NeoDBRepository,
    settingsManager: AppSettingsManager,
    authRepository: AuthRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState>
        field = MutableStateFlow(HomeUiState())

    private var refreshJob: Job? = null

    private var enabledTrendingTypes: List<EntryType> = EntryType.entries.take(6)

    init {
        updateTrending()
        // Refresh data when app settings change
        settingsManager.appSettings
            .map { it.homeTrendingTypes }
            .distinctUntilChanged()
            .filter { it.isNotEmpty() }
            .onEach { enabledTrendingTypes = it }
            .launchIn(viewModelScope)
        // Refresh data when login status changes.
        authRepository.accountStatus
            .distinctUntilChangedBy { it.isLogin }
            .onEach { updateTrending() }
            .launchIn(viewModelScope)
    }

    fun updateTrending() {
        viewModelScope.launch {
            refreshJob?.cancelAndJoin()
            // Should after job cancellation since it will toggle it to false
            uiState.update { it.copy(isLoading = true) }
            refreshJob = viewModelScope
                .launch {
                    val jobs = enabledTrendingTypes.map {
                        async {
                            neoDBRepository
                                .fetchTrendingByEntryType(it)
                                .first()
                                .map { Entry(it) }
                        }
                    }
                    uiState.update {
                        it.copy(
                            data = enabledTrendingTypes.zip(jobs.awaitAll()).toMap(),
                        )
                    }
                }.apply {
                    invokeOnCompletion { uiState.update { it.copy(isLoading = false) } }
                }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val data: Map<EntryType, List<Entry>> = emptyMap(),
)
