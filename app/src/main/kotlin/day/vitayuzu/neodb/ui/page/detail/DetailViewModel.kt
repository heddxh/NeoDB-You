package day.vitayuzu.neodb.ui.page.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.Repository
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
            // Posts
            val postList: MutableList<Post> = mutableListOf()
            repo.fetchItemPosts(uuid).collect { paginatedPostList ->
                paginatedPostList.data.forEach { schema ->
                    postList.add(Post(schema))
                }
            }
            postList.sortByDescending { it.date }
            // Info
            repo.fetchDetail(type, uuid).collect { detailSchema ->
                val detail = when (detailSchema) {
                    is EditionSchema -> detailSchema.toDetail()
                    is GameSchema -> detailSchema.toDetail()
                    is MovieSchema -> detailSchema.toDetail()
                    is TVShowSchema -> detailSchema.toDetail()
                    is TVSeasonSchema -> detailSchema.toDetail()
                    is AlbumSchema -> detailSchema.toDetail()
                    is PodcastSchema -> detailSchema.toDetail()
                    is PerformanceSchema -> detailSchema.toDetail()
                }
                _uiState.update { DetailUiState.Success(detail, postList) }
            }
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val detail: Detail,
        val reviewList: List<Post> = emptyList(),
    ) : DetailUiState

    data class Error(val message: String) : DetailUiState
}

data class ReviewUiState(
    val avatar: String?,
    val username: String,
    val rating: Float?,
    val content: String,
)
