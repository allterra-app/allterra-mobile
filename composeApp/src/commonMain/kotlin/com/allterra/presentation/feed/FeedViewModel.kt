package com.allterra.presentation.feed

import androidx.lifecycle.ViewModel
import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PostsRepository
import com.allterra.presentation.common.model.ActivityTypeUi
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.common.model.NotificationUiModel
import com.allterra.presentation.common.model.PostTypeUi
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
    val notifications: List<NotificationUiModel> = emptyList(),
    val selectedPostTypes: Set<PostTypeUi> = emptySet(),
    val selectedActivityTypes: Set<ActivityTypeUi> = emptySet(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isNotificationsLoading: Boolean = false,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val loadError: String? = null,
    val actionError: String? = null,
    val notificationsSeen: Boolean = false,
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
        loadNotifications()
    }

    fun refresh(isManual: Boolean = false) {
        scope.launch {
            _state.update {
                if (isManual) it.copy(isRefreshing = true, loadError = null)
                else it.copy(isLoading = it.activities.isEmpty(), loadError = null)
            }
            when (val result = postsRepository.getFeedPosts(page = 0, size = FEED_PAGE_SIZE)) {
                is ApiResult.Success -> _state.update {
                    it.copy(
                        activities = result.data.items,
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        currentPage = result.data.page,
                        hasNextPage = result.data.hasNext,
                        loadError = null,
                    )
                }

                else -> _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        loadError = result.message(),
                    )
                }
            }
        }
    }

    fun loadMore() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasNextPage) return

        scope.launch {
            _state.update { it.copy(isLoadingMore = true, loadError = null) }
            when (val result = postsRepository.getFeedPosts(page = current.currentPage + 1, size = FEED_PAGE_SIZE)) {
                is ApiResult.Success -> _state.update { state ->
                    state.copy(
                        activities = state.activities + result.data.items.filterNot { next ->
                            state.activities.any { existing -> existing.id == next.id }
                        },
                        isLoadingMore = false,
                        currentPage = result.data.page,
                        hasNextPage = result.data.hasNext,
                        loadError = null,
                    )
                }

                else -> _state.update {
                    it.copy(
                        isLoadingMore = false,
                        loadError = result.message(),
                    )
                }
            }
        }
    }

    fun loadNotifications() {
        if (_state.value.isNotificationsLoading) return
        scope.launch {
            _state.update { it.copy(isNotificationsLoading = true, actionError = null) }
            when (val result = postsRepository.getMyNotifications()) {
                is ApiResult.Success -> _state.update {
                    val hasNewUnread = result.data.any { n -> !n.read && !it.notificationsSeen }
                    it.copy(
                        notifications = result.data,
                        isNotificationsLoading = false,
                        notificationsSeen = if (hasNewUnread) false else it.notificationsSeen,
                    )
                }

                else -> _state.update {
                    it.copy(
                        isNotificationsLoading = false,
                        actionError = result.message(),
                    )
                }
            }
        }
    }

    fun onNotificationsOpened() {
        _state.update { it.copy(notificationsSeen = true) }
    }

    fun markNotificationRead(id: String) {
        if (_state.value.notifications.none { it.id == id && !it.read }) return
        scope.launch {
            when (val result = postsRepository.markNotificationRead(id)) {
                is ApiResult.Success -> _state.update { state ->
                    state.copy(
                        notifications = state.notifications.map { item ->
                            if (item.id == id) result.data else item
                        },
                        actionError = null,
                    )
                }

                else -> _state.update { it.copy(actionError = result.message()) }
            }
        }
    }

    fun onPostCreated(item: ActivityUiModel) {
        _state.update { state ->
            state.copy(activities = listOf(item) + state.activities.filterNot { it.id == item.id })
        }
        loadNotifications()
    }

    fun toggleLike(id: String) {
        val current = _state.value.activities.firstOrNull { it.id == id } ?: return
        scope.launch {
            val result = if (current.liked) {
                postsRepository.unlikePost(id)
            } else {
                postsRepository.likePost(id)
            }
            when (result) {
                is ApiResult.Success -> _state.update { state ->
                    state.copy(
                        activities = state.activities.map { item ->
                            if (item.id == id) {
                                item.copy(
                                    liked = !item.liked,
                                    likeCount = if (item.liked) item.likeCount - 1 else item.likeCount + 1,
                                )
                            } else item
                        },
                        actionError = null,
                    )
                }

                else -> _state.update { it.copy(actionError = result.message()) }
            }
        }
    }

    fun toggleBookmark(id: String) {
        val current = _state.value.activities.firstOrNull { it.id == id } ?: return
        scope.launch {
            val result = if (current.bookmarked) {
                postsRepository.unsavePost(id)
            } else {
                postsRepository.savePost(id)
            }
            when (result) {
                is ApiResult.Success -> _state.update { state ->
                    state.copy(
                        activities = state.activities.map { item ->
                            if (item.id == id) item.copy(bookmarked = !item.bookmarked) else item
                        },
                        actionError = null,
                    )
                }

                else -> _state.update { it.copy(actionError = result.message()) }
            }
        }
    }

    fun togglePostTypeFilter(type: PostTypeUi) {
        _state.update { state ->
            val updated = if (type in state.selectedPostTypes) state.selectedPostTypes - type else state.selectedPostTypes + type
            state.copy(selectedPostTypes = updated)
        }
    }

    fun toggleActivityTypeFilter(type: ActivityTypeUi) {
        _state.update { state ->
            val updated = if (type in state.selectedActivityTypes) state.selectedActivityTypes - type else state.selectedActivityTypes + type
            state.copy(selectedActivityTypes = updated)
        }
    }

    fun clearFilters() {
        _state.update { it.copy(selectedPostTypes = emptySet(), selectedActivityTypes = emptySet()) }
    }

    fun clearActionError() {
        _state.update { it.copy(actionError = null) }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

private const val FEED_PAGE_SIZE = 20

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
