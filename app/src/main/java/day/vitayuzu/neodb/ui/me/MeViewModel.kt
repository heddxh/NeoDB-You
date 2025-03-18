package day.vitayuzu.neodb.ui.me

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import day.vitayuzu.neodb.data.UserShelfRepository
import day.vitayuzu.neodb.data.model.Mark
import day.vitayuzu.neodb.data.model.ShelfType
import kotlinx.coroutines.launch

class MainViewModel(
    private val repo : UserShelfRepository
): ViewModel() {
    var uiState by mutableStateOf(MainUiState())
        private set

    fun fetchUserShelf(type: ShelfType) {
        viewModelScope.launch {
            val response = repo.fetchMyShelf(type)
            uiState = uiState.copy(shelfCount = response.count)
            val marks = response.data?.map { Mark(it) }
            uiState = uiState.copy(entryCards = marks ?: emptyList())
        }
    }
}

data class MainUiState(
    val shelfCount: Int = 0,
    val entryCards: List<Mark> = emptyList()
)