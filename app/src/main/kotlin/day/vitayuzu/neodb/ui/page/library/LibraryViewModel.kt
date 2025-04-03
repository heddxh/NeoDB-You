package day.vitayuzu.neodb.ui.page.library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import day.vitayuzu.neodb.data.Repository
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.GregorianCalendar

// FIXME: Move filtering to UI(LibraryPage)
class LibraryViewModel(private val repo: Repository = Repository()) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    private val marks = mutableListOf<Mark>()

    init {
        refresh()
    }

    fun refresh() {
        marks.clear()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.fetchMyAllShelf().collect { pagedMarkSchemaFlow ->
                marks += pagedMarkSchemaFlow.data?.map { Mark(it) }?.sortedByDescending { it.date }
                         ?: emptyList()
            }
            refreshDisplayedMarks()
            generateHeatMap()
            _uiState.update { it.copy(isLoading = false) }
            marks.forEach {
                Log.d("LibraryViewModel", "Mark: ${it.date} ${it.entry.title}")
            }
        }

    }

    fun toggleSelectedEntryType(which: EntryType) {
        _uiState.update {
            it.copy(
                selectedEntryTypes = if (which in it.selectedEntryTypes) {
                    it.selectedEntryTypes - which
                } else {
                    it.selectedEntryTypes + which
                }
            )
        }
        refreshDisplayedMarks()
    }

    fun resetEntryType() {
        println("Long pressed")
        _uiState.update { it.copy(selectedEntryTypes = emptySet()) }
        refreshDisplayedMarks()
    }

    private fun refreshDisplayedMarks() {
        // Show all when no filters
        if (_uiState.value.selectedEntryTypes.isEmpty()) {
            _uiState.update { curr ->
                curr.copy(displayedMarks = marks.filter {
                    it.shelfType == curr.selectedShelfType
                })
            }
            return
        }
        _uiState.update { curr ->
            curr.copy(
                displayedMarks = marks.filter {
                    it.entry.category in curr.selectedEntryTypes
                    && it.shelfType == curr.selectedShelfType
                })
        }
    }

    fun switchShelfType(type: ShelfType) {
        _uiState.update { it.copy(selectedShelfType = type) }
        refreshDisplayedMarks()
    }

    private fun generateHeatMap() {
        val heatMapDaysByWeek = marks
            .filter { // Only this year
                it.date.year == Clock.System.todayIn(TimeZone.currentSystemDefault()).year
            }.groupBy { mark -> // week-index -> marks
                // TODO: change to kotlinx-datetime until https://github.com/Kotlin/kotlinx-datetime/issues/129
                // FIXME: Locale and time zone
                GregorianCalendar(
                    mark.date.year,
                    mark.date.monthNumber - 1, // GregorianCalendar month is 0-based
                    mark.date.dayOfMonth
                ).get(GregorianCalendar.WEEK_OF_YEAR)
            }.mapValues { (weekIndex, marks) -> // Construct a week
                return@mapValues marks
                    .groupBy { it.date } // Get marks with same day
                    .map { (_, sameDayMarks) ->
                        HeatMapDayData(weekIndex, sameDayMarks)
                    }
            }
        // Heatmap should be in reverse order, newest week first
        val heatMap = (GregorianCalendar().get(GregorianCalendar.WEEK_OF_YEAR) downTo 0)
            .map { weekIndex ->
                HeatMapWeekUiState(
                    index = weekIndex,
                    blocks = heatMapDaysByWeek[weekIndex] ?: emptyList()
                )
            }
        _uiState.update { it.copy(heatMap = heatMap) }
    }
}

/**
 * UI state for the Library screen.
 * @param displayedMarks: Marks to be displayed in the list(may filtered)
 * @param selectedEntryTypes: Set of selected [EntryType] for filtering
 * @param selectedShelfType: Currently selected [ShelfType]
 * @param heatMap: Map of week index to list of [HeatMapDayData]
 */
data class LibraryUiState(
    val isLoading: Boolean = false,
    val displayedMarks: List<Mark> = emptyList(),
    val selectedEntryTypes: Set<EntryType> = emptySet(), // Filter chips
    val selectedShelfType: ShelfType = ShelfType.wishlist, // Tabs
    val heatMap: List<HeatMapWeekUiState> = emptyList()
)

data class HeatMapWeekUiState(
    val index: Int = 0,
    val blocks: List<HeatMapDayData> = emptyList()
)