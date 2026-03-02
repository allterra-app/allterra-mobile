package com.allterra.presentation.profile

import androidx.lifecycle.ViewModel
import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PostsRepository
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.common.model.PoiUiModel
import com.allterra.presentation.common.model.RouteUiModel
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

enum class ProfileFeedLayout {
    LIST,
    GRID,
}

data class PostCreateDraft(
    val title: String = "",
    val description: String = "",
    val selectedRouteId: String? = null,
    val selectedPoiIds: Set<String> = emptySet(),
    val photoUris: List<String> = emptyList(),
)

data class ProfileUiState(
    val userName: String = "",
    val about: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val feedLayout: ProfileFeedLayout = ProfileFeedLayout.LIST,
    val activities: List<ActivityUiModel> = emptyList(),
    val isCreatePostOpen: Boolean = false,
    val postCreateDraft: PostCreateDraft = PostCreateDraft(),
    val postCreateError: String? = null,
)

class ProfileViewModel(
    private val postsRepository: PostsRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            _state.update { it.copy(isLoading = true, postCreateError = null) }

            val userName = when (val userResult = postsRepository.getMyUserName()) {
                is ApiResult.Success -> userResult.data
                else -> state.value.userName
            }

            when (val postsResult = postsRepository.getMyPosts()) {
                is ApiResult.Success -> _state.update {
                    it.copy(
                        userName = userName,
                        activities = postsResult.data,
                        isLoading = false,
                        postCreateError = null,
                    )
                }

                else -> _state.update {
                    it.copy(
                        userName = userName,
                        isLoading = false,
                        postCreateError = postsResult.message(),
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

    fun setLayout(layout: ProfileFeedLayout) {
        _state.update { it.copy(feedLayout = layout) }
    }

    fun openCreatePost() {
        _state.update { it.copy(isCreatePostOpen = true, postCreateDraft = PostCreateDraft(), postCreateError = null) }
    }

    fun closeCreatePost() {
        _state.update { it.copy(isCreatePostOpen = false, postCreateError = null) }
    }

    fun onPostTitleChanged(value: String) {
        _state.update { it.copy(postCreateDraft = it.postCreateDraft.copy(title = value), postCreateError = null) }
    }

    fun onPostDescriptionChanged(value: String) {
        _state.update { it.copy(postCreateDraft = it.postCreateDraft.copy(description = value), postCreateError = null) }
    }

    fun onPostRouteSelected(routeId: String?) {
        _state.update { it.copy(postCreateDraft = it.postCreateDraft.copy(selectedRouteId = routeId), postCreateError = null) }
    }

    fun togglePostPoi(poiId: String) {
        _state.update { state ->
            val current = state.postCreateDraft.selectedPoiIds
            val updated = if (poiId in current) current - poiId else current + poiId
            state.copy(postCreateDraft = state.postCreateDraft.copy(selectedPoiIds = updated), postCreateError = null)
        }
    }

    fun addCreatePhotos(photoUris: List<String>, maxPhotos: Int, limitMessage: String) {
        if (photoUris.isEmpty()) return
        _state.update { currentState ->
            val currentPhotos = currentState.postCreateDraft.photoUris
            val freeSlots = (maxPhotos - currentPhotos.size).coerceAtLeast(0)
            if (freeSlots == 0) {
                currentState.copy(postCreateError = limitMessage)
            } else {
                val nextPhotos = currentPhotos + photoUris.take(freeSlots)
                currentState.copy(
                    postCreateDraft = currentState.postCreateDraft.copy(photoUris = nextPhotos),
                    postCreateError = if (nextPhotos.size >= maxPhotos && photoUris.size > freeSlots) limitMessage else null,
                )
            }
        }
    }

    fun removeCreatePhoto(uri: String) {
        _state.update {
            it.copy(
                postCreateDraft = it.postCreateDraft.copy(photoUris = it.postCreateDraft.photoUris - uri),
                postCreateError = null,
            )
        }
    }

    fun onPostPhotoReadError(message: String) {
        _state.update { it.copy(postCreateError = message) }
    }

    fun saveCreatedPost(
        validationMessage: String,
        availableRoutes: List<RouteUiModel>,
        availablePois: List<PoiUiModel>,
    ): Boolean {
        val currentState = _state.value
        val draft = currentState.postCreateDraft
        val title = draft.title.trim()
        if (title.isBlank()) {
            _state.update { it.copy(postCreateError = validationMessage) }
            return false
        }

        val selectedRouteId = draft.selectedRouteId?.takeIf { id -> availableRoutes.any { it.id == id } }
        val selectedPoiIds = draft.selectedPoiIds.filter { id -> availablePois.any { it.id == id } }

        scope.launch {
            _state.update { it.copy(isSaving = true, postCreateError = null) }
            when (
                val result = postsRepository.createMyPost(
                    title = title,
                    description = draft.description.trim(),
                    localPhotoPaths = draft.photoUris,
                    selectedRouteId = selectedRouteId,
                    selectedPoiIds = selectedPoiIds,
                )
            ) {
                is ApiResult.Success -> _state.update {
                    it.copy(
                        isSaving = false,
                        activities = listOf(result.data) + it.activities,
                        userName = if (it.userName.isBlank()) result.data.author else it.userName,
                        isCreatePostOpen = false,
                        postCreateDraft = PostCreateDraft(),
                        postCreateError = null,
                    )
                }

                else -> _state.update {
                    it.copy(
                        isSaving = false,
                        postCreateError = result.message(),
                    )
                }
            }
        }

        return true
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
