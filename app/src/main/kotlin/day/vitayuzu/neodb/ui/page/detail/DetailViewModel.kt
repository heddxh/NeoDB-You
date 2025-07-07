package day.vitayuzu.neodb.ui.page.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.Repository
import day.vitayuzu.neodb.data.schema.MarkInSchema
import day.vitayuzu.neodb.data.schema.detail.AlbumSchema
import day.vitayuzu.neodb.data.schema.detail.EditionSchema
import day.vitayuzu.neodb.data.schema.detail.GameSchema
import day.vitayuzu.neodb.data.schema.detail.MovieSchema
import day.vitayuzu.neodb.data.schema.detail.PerformanceSchema
import day.vitayuzu.neodb.data.schema.detail.PodcastSchema
import day.vitayuzu.neodb.data.schema.detail.TVSeasonSchema
import day.vitayuzu.neodb.data.schema.detail.TVShowSchema
import day.vitayuzu.neodb.ui.model.Detail
import day.vitayuzu.neodb.ui.model.Post
import day.vitayuzu.neodb.ui.model.toDetail
import day.vitayuzu.neodb.util.EntryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted val type: EntryType,
    @Assisted val uuid: String, // FIXME: tv is kinds of confusing, should be season instead of show
    private val repo: Repository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            type: EntryType,
            uuid: String,
        ): DetailViewModel
    }

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState = _detailUiState.asStateFlow()
    private val _postUiState = MutableStateFlow(PostUiState())
    val postUiState = _postUiState.asStateFlow()

    init {
        refreshDetail()
        refreshPosts()
    }

    @OptIn(ExperimentalTime::class)
    fun refreshPosts() {
        viewModelScope.launch {
            _postUiState.update { it.copy(isLoading = true, postList = emptyList()) }
            repo.fetchItemPosts(uuid).collect { paginatedPostList ->
                paginatedPostList.data.forEach { schema ->
                    _postUiState.update {
                        it.copy(
                            postList = (it.postList + Post(schema)).sortedByDescending { it.date },
                        )
                    }
                }
            }
            _postUiState.update { it.copy(isLoading = false) }
        }
    }

    private fun refreshDetail() {
        viewModelScope.launch {
            _detailUiState.update { DetailUiState.Loading }
            repo.fetchDetail(type, uuid).collect {
                val detail = when (it) {
                    is EditionSchema -> it.toDetail()
                    is GameSchema -> it.toDetail()
                    is MovieSchema -> it.toDetail()
                    is TVShowSchema -> it.toDetail()
                    is TVSeasonSchema -> it.toDetail()
                    is AlbumSchema -> it.toDetail()
                    is PodcastSchema -> it.toDetail()
                    is PerformanceSchema -> it.toDetail()
                }
                _detailUiState.update { DetailUiState.Success(detail) }
            }
        }
    }

    fun postMark(data: MarkInSchema) {
        viewModelScope.launch {
            repo.postMark(uuid, data).collect { refreshPosts() }
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val detail: Detail,
        val reviewList: List<Post> = emptyList(),
    ) : DetailUiState
}

data class PostUiState(
    val isLoading: Boolean = false,
    val postList: List<Post> = emptyList(),
)
