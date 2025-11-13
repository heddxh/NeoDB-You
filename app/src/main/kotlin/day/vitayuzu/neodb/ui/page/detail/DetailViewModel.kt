package day.vitayuzu.neodb.ui.page.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.NeoDBRepository
import day.vitayuzu.neodb.data.schema.MarkInSchema
import day.vitayuzu.neodb.ui.model.Detail
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.model.Post
import day.vitayuzu.neodb.ui.model.toDetail
import day.vitayuzu.neodb.util.EntryType
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted val type: EntryType,
    @Assisted val uuid: String, // FIXME: tv is kinds of confusing, should be season instead of show
    private val repo: NeoDBRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(type: EntryType, uuid: String): DetailViewModel
    }

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    // Keep reference to the job loading reviews to able to cancel it when refreshing again.
    private var loadingReviewsJob: Job? = null

    init {
        refreshDetail()
        refreshUserMark()
        refreshPosts()
    }

    private fun refreshDetail() {
        viewModelScope.launch {
            repo.fetchDetail(type, uuid).collect { detailSchema ->
                _uiState.update { it.copy(detail = detailSchema.toDetail()) }
            }
        }
    }

    private fun refreshUserMark() {
        _uiState.update { it.copy(isLoadingMark = true) }
        viewModelScope
            .launch {
                repo
                    .fetchItemUserMark(uuid)
                    .onEmpty {
                        _uiState.update { it.copy(mark = null) }
                    }.collect { markSchema ->
                        _uiState.update { it.copy(mark = Mark(markSchema)) }
                    }
            }.invokeOnCompletion {
                _uiState.update { it.copy(isLoadingMark = false) }
            }
    }

    @OptIn(ExperimentalTime::class)
    fun refreshPosts(limit: Int = 5) {
        // Reset ui state
        _uiState.update {
            it.copy(
                isLoadingPost = true,
                postList = emptyList(),
                hasMorePost = false,
            )
        }
        loadingReviewsJob = viewModelScope.launch {
            // Cancel previous job
            if (loadingReviewsJob?.isActive == true) {
                loadingReviewsJob?.cancelAndJoin()
            }

            @Suppress(
                "ktlint:standard:chain-method-continuation",
                "ktlint:standard:comment-wrapping",
            )
            repo
                .fetchItemPosts(uuid)
                ./*buffer().*/collect { postList ->
                    _uiState.update { ui ->
                        val newPostList = ui.postList + postList.data.map { Post(it) }
                        ui.copy(
                            postList = newPostList.distinct(), // .sortedByDescending { it.date }
                        )
                    }
                    if (uiState.value.postList.size > limit) {
                        _uiState.update { it.copy(hasMorePost = true) }
                        return@collect
                    }
                }
            _uiState.update { it.copy(isLoadingPost = false) }
        }
    }

    fun postMark(data: MarkInSchema) {
        viewModelScope.launch {
            repo.postMark(uuid, data).collect()
            refreshUserMark()
        }
    }

    fun deleteMark() {
        _uiState.update { it.copy(isLoadingMark = true) }
        viewModelScope.launch {
            repo.deleteMark(uuid).collect()
            refreshUserMark()
        }
    }
}

data class DetailUiState(
    val detail: Detail? = null,
    val mark: Mark? = null,
    val isLoadingMark: Boolean = false,
    val isLoadingPost: Boolean = false,
    val hasMorePost: Boolean = false,
    val postList: List<Post> = emptyList(),
)
