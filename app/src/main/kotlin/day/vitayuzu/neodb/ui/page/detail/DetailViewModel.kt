package day.vitayuzu.neodb.ui.page.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.Repository
import day.vitayuzu.neodb.data.schema.detail.DetailSchema
import day.vitayuzu.neodb.util.EntryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted val type: EntryType,
    @Assisted val uuid: String,
    private val repo: Repository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            type: EntryType,
            uuid: String,
        ): DetailViewModel
    }

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        refresh(type, uuid)
    }

    fun refresh(
        type: EntryType,
        uuid: String,
    ) {
        _uiState.update { DetailUiState.Loading }
        viewModelScope.launch {
            repo.fetchDetail(type, uuid).collect { detailRes ->
                _uiState.update { DetailUiState.Success(detailRes) }
            }
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val detail: DetailSchema, // FIXME: Should not reference schema directly
        val reviewList: List<ReviewUiState> = emptyList(),
    ) : DetailUiState

    data class Error(val message: String) : DetailUiState
}

data class ReviewUiState(
    val avatar: String?,
    val username: String,
    val content: String,
)
