package com.allterra.presentation.feed

import androidx.lifecycle.ViewModel
import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PostsRepository
import com.allterra.presentation.common.model.ActivityUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val activities: List<ActivityUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val loadError: String? = null,
)

class FeedViewModel(
    private val postsRepository: PostsRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _state = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            _state.update { it.copy(isLoading = true, loadError = null) }
            when (val result = postsRepository.getFeedPosts()) {
                is ApiResult.Success -> _state.update {
                    it.copy(
                        activities = result.data,
                        isLoading = false,
                        loadError = null,
                    )
                }

                else -> _state.update {
                    it.copy(
                        isLoading = false,
                        loadError = result.message(),
                    )
                }
            }
        }
    }

    fun toggleLike(id: String) {
        _state.update { state ->
            state.copy(
                activities = state.activities.map { item ->
                    if (item.id == id) item.copy(liked = !item.liked) else item
                }
            )
        }
    }

    fun toggleBookmark(id: String) {
        _state.update { state ->
            state.copy(
                activities = state.activities.map { item ->
                    if (item.id == id) item.copy(bookmarked = !item.bookmarked) else item
                }
            )
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

private fun ApiResult<*>.message(): String {
    return when (this) {
        is ApiResult.Success -> ""
        is ApiResult.ValidationError -> message
        is ApiResult.Unauthorized -> message
        is ApiResult.Forbidden -> message
        is ApiResult.NotFound -> message
        is ApiResult.ServerError -> message
        is ApiResult.NetworkError -> message
        is ApiResult.UnknownError -> message
    }
}
