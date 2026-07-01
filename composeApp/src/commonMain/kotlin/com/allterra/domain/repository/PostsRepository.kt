package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.presentation.common.model.ActivityTypeUi
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.common.model.NotificationUiModel
import com.allterra.presentation.common.model.PostAudienceUi
import com.allterra.presentation.common.model.PostTypeUi

data class FeedPageResult(
    val items: List<ActivityUiModel>,
    val page: Int,
    val size: Int,
    val totalItems: Long,
    val hasNext: Boolean,
)

interface PostsRepository {
    suspend fun getFeedPosts(page: Int, size: Int): ApiResult<FeedPageResult>
    suspend fun getMyPosts(): ApiResult<List<ActivityUiModel>>
    suspend fun getMyUserName(): ApiResult<String>
    suspend fun getMySavedPostIds(): ApiResult<Set<String>>
    suspend fun savePost(postId: String): ApiResult<Unit>
    suspend fun unsavePost(postId: String): ApiResult<Unit>
    suspend fun getMyNotifications(): ApiResult<List<NotificationUiModel>>
    suspend fun markNotificationRead(notificationId: String): ApiResult<NotificationUiModel>
    suspend fun createMyPost(
        title: String,
        description: String,
        localPhotoPaths: List<String>,
        audience: PostAudienceUi,
        selectedTripId: String?,
        selectedRouteId: String?,
        selectedPoiIds: List<String>,
        type: PostTypeUi? = null,
        activity: ActivityTypeUi? = null,
    ): ApiResult<ActivityUiModel>
    suspend fun deleteMyPost(postId: String): ApiResult<Unit>
    suspend fun likePost(postId: String): ApiResult<Unit>
    suspend fun unlikePost(postId: String): ApiResult<Unit>
}
