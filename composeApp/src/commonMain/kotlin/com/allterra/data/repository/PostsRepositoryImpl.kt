package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.FeedPageResult
import com.allterra.domain.repository.PostsRepository
import com.allterra.network.media.MediaApi
import com.allterra.network.media.toAbsoluteMediaUrl
import com.allterra.network.notification.NotificationApi
import com.allterra.network.notification.NotificationDto
import com.allterra.network.post.ActivityTypeDto
import com.allterra.network.post.PostApi
import com.allterra.network.post.PostAudienceDto
import com.allterra.network.post.PostCreateRequestDto
import com.allterra.network.post.PostDto
import com.allterra.network.post.PostPhotoApi
import com.allterra.network.post.PostPhotoCreateRequestDto
import com.allterra.network.post.PostTypeDto
import com.allterra.network.user.UserApi
import com.allterra.platform.LocalFileAccess
import com.allterra.presentation.common.model.ActivityTypeUi
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.common.model.NotificationUiModel
import com.allterra.presentation.common.model.PostAudienceUi
import com.allterra.presentation.common.model.PostTypeUi
import com.allterra.presentation.common.time.currentUiDate

class PostsRepositoryImpl(
    private val userApi: UserApi,
    private val postApi: PostApi,
    private val postPhotoApi: PostPhotoApi,
    private val mediaApi: MediaApi,
    private val notificationApi: NotificationApi,
) : PostsRepository {

    private var cachedUserId: String? = null

    private suspend fun resolveUserId(): ApiResult<String> {
        cachedUserId?.let { return ApiResult.Success(it) }
        return when (val meResult = userApi.me()) {
            is ApiResult.Success -> {
                cachedUserId = meResult.data.id
                ApiResult.Success(meResult.data.id)
            }
            is ApiResult.ValidationError -> ApiResult.ValidationError(meResult.message, meResult.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(meResult.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(meResult.message)
            is ApiResult.NotFound -> ApiResult.NotFound(meResult.message)
            is ApiResult.ServerError -> ApiResult.ServerError(meResult.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(meResult.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(meResult.message)
        }
    }

    override suspend fun getFeedPosts(page: Int, size: Int): ApiResult<FeedPageResult> {
        val savedPostIds = when (val savedResult = getMySavedPostIds()) {
            is ApiResult.Success -> savedResult.data
            else -> emptySet()
        }
        return when (val postsResult = postApi.getFeed(page, size)) {
            is ApiResult.Success -> ApiResult.Success(
                FeedPageResult(
                    items = postsResult.data.items.map { it.toUiModel(bookmarkedPostIds = savedPostIds) },
                    page = postsResult.data.page,
                    size = postsResult.data.size,
                    totalItems = postsResult.data.totalItems,
                    hasNext = postsResult.data.hasNext,
                )
            )
            is ApiResult.ValidationError -> ApiResult.ValidationError(postsResult.message, postsResult.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(postsResult.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(postsResult.message)
            is ApiResult.NotFound -> ApiResult.NotFound(postsResult.message)
            is ApiResult.ServerError -> ApiResult.ServerError(postsResult.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(postsResult.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(postsResult.message)
        }
    }

    override suspend fun getMyPosts(): ApiResult<List<ActivityUiModel>> {
        val me = when (val meResult = userApi.me()) {
            is ApiResult.Success -> meResult.data
            is ApiResult.ValidationError -> return ApiResult.ValidationError(meResult.message, meResult.fields)
            is ApiResult.Unauthorized -> return ApiResult.Unauthorized(meResult.message)
            is ApiResult.Forbidden -> return ApiResult.Forbidden(meResult.message)
            is ApiResult.NotFound -> return ApiResult.NotFound(meResult.message)
            is ApiResult.ServerError -> return ApiResult.ServerError(meResult.message)
            is ApiResult.NetworkError -> return ApiResult.NetworkError(meResult.message)
            is ApiResult.UnknownError -> return ApiResult.UnknownError(meResult.message)
        }

        cachedUserId = me.id

        val savedPostIds = when (val savedResult = postApi.getSavedByUser(me.id)) {
            is ApiResult.Success -> savedResult.data.mapTo(linkedSetOf()) { it.id }
            else -> emptySet()
        }

        return when (val postsResult = postApi.getByUser(me.id)) {
            is ApiResult.Success -> ApiResult.Success(
                postsResult.data.map {
                    it.toUiModel(
                        fallbackAuthor = me.username?.takeIf { value -> value.isNotBlank() }
                            ?: me.email.substringBefore('@').ifBlank { "User" },
                        bookmarkedPostIds = savedPostIds,
                    )
                }
            )

            is ApiResult.ValidationError -> ApiResult.ValidationError(postsResult.message, postsResult.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(postsResult.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(postsResult.message)
            is ApiResult.NotFound -> ApiResult.NotFound(postsResult.message)
            is ApiResult.ServerError -> ApiResult.ServerError(postsResult.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(postsResult.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(postsResult.message)
        }
    }

    override suspend fun getMyUserName(): ApiResult<String> {
        return when (val meResult = userApi.me()) {
            is ApiResult.Success -> {
                val value = meResult.data.username?.takeIf { it.isNotBlank() }
                    ?: meResult.data.email.substringBefore('@').ifBlank { "User" }
                ApiResult.Success(value)
            }

            is ApiResult.ValidationError -> ApiResult.ValidationError(meResult.message, meResult.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(meResult.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(meResult.message)
            is ApiResult.NotFound -> ApiResult.NotFound(meResult.message)
            is ApiResult.ServerError -> ApiResult.ServerError(meResult.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(meResult.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(meResult.message)
        }
    }

    override suspend fun getMySavedPostIds(): ApiResult<Set<String>> {
        return when (val idResult = resolveUserId()) {
            is ApiResult.Success -> {
                val userId = idResult.data
                when (val result = postApi.getSavedByUser(userId)) {
                    is ApiResult.Success -> ApiResult.Success(result.data.mapTo(linkedSetOf()) { it.id })
                    else -> result.asPostResult()
                }
            }
            else -> idResult.asPostResult()
        }
    }

    override suspend fun savePost(postId: String): ApiResult<Unit> {
        return when (val idResult = resolveUserId()) {
            is ApiResult.Success -> {
                when (val result = postApi.saveForUser(idResult.data, postId)) {
                    is ApiResult.Success -> ApiResult.Success(Unit)
                    else -> result.asPostResult()
                }
            }
            else -> idResult.asPostResult()
        }
    }

    override suspend fun unsavePost(postId: String): ApiResult<Unit> {
        return when (val idResult = resolveUserId()) {
            is ApiResult.Success -> {
                when (val result = postApi.unsaveForUser(idResult.data, postId)) {
                    is ApiResult.Success -> ApiResult.Success(Unit)
                    else -> result.asPostResult()
                }
            }
            else -> idResult.asPostResult()
        }
    }

    override suspend fun getMyNotifications(): ApiResult<List<NotificationUiModel>> {
        return when (val result = notificationApi.getMine()) {
            is ApiResult.Success -> ApiResult.Success(result.data.map { it.toUiModel() })
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun markNotificationRead(notificationId: String): ApiResult<NotificationUiModel> {
        return when (val result = notificationApi.markRead(notificationId)) {
            is ApiResult.Success -> ApiResult.Success(result.data.toUiModel())
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(notificationId)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun createMyPost(
        title: String,
        description: String,
        localPhotoPaths: List<String>,
        audience: PostAudienceUi,
        selectedTripId: String?,
        selectedRouteId: String?,
        selectedPoiIds: List<String>,
        type: PostTypeUi?,
        activity: ActivityTypeUi?,
    ): ApiResult<ActivityUiModel> {
        val me = when (val meResult = userApi.me()) {
            is ApiResult.Success -> meResult.data
            is ApiResult.ValidationError -> return ApiResult.ValidationError(meResult.message, meResult.fields)
            is ApiResult.Unauthorized -> return ApiResult.Unauthorized(meResult.message)
            is ApiResult.Forbidden -> return ApiResult.Forbidden(meResult.message)
            is ApiResult.NotFound -> return ApiResult.NotFound(meResult.message)
            is ApiResult.ServerError -> return ApiResult.ServerError(meResult.message)
            is ApiResult.NetworkError -> return ApiResult.NetworkError(meResult.message)
            is ApiResult.UnknownError -> return ApiResult.UnknownError(meResult.message)
        }

        val request = PostCreateRequestDto(
            userId = me.id,
            tripId = selectedTripId,
            routeId = selectedRouteId,
            title = title,
            body = description.takeIf { it.isNotBlank() },
            audience = audience.toDto(),
            type = type?.let { PostTypeDto.valueOf(it.name) },
            activity = activity?.let { ActivityTypeDto.valueOf(it.name) },
        )

        val createdPost = when (val createResult = postApi.createForUser(me.id, request)) {
            is ApiResult.Success -> createResult.data
            is ApiResult.ValidationError -> return ApiResult.ValidationError(createResult.message, createResult.fields)
            is ApiResult.Unauthorized -> return ApiResult.Unauthorized(createResult.message)
            is ApiResult.Forbidden -> return ApiResult.Forbidden(createResult.message)
            is ApiResult.NotFound -> return ApiResult.NotFound(createResult.message)
            is ApiResult.ServerError -> return ApiResult.ServerError(createResult.message)
            is ApiResult.NetworkError -> return ApiResult.NetworkError(createResult.message)
            is ApiResult.UnknownError -> return ApiResult.UnknownError(createResult.message)
        }

        val attachedPhotoUris = mutableListOf<String>()
        localPhotoPaths.take(MAX_SERVER_POST_PHOTOS).forEach { localPath ->
            val bytes = LocalFileAccess.readBytes(localPath)
                ?: return ApiResult.ValidationError("Unable to read selected photo", emptyMap())
            val fileName = LocalFileAccess.fileName(localPath)
            val contentType = LocalFileAccess.contentType(localPath)

            val uploaded = when (val uploadResult = mediaApi.upload(fileName, contentType, bytes)) {
                is ApiResult.Success -> uploadResult.data
                else -> return uploadResult.asPostFailure("Failed to upload post photo")
            }

            val absoluteUrl = normalizeToAbsoluteMediaUrl(uploaded.url)
            when (val photoCreateResult = postPhotoApi.create(
                PostPhotoCreateRequestDto(
                    url = absoluteUrl,
                    postId = createdPost.id,
                )
            )) {
                is ApiResult.Success -> Unit
                else -> return photoCreateResult.asPostFailure("Failed to attach photo to post")
            }
            attachedPhotoUris += absoluteUrl
        }

        val refreshed = when (val getByIdResult = postApi.getById(createdPost.id)) {
            is ApiResult.Success -> getByIdResult.data
            else -> createdPost
        }

        val authorFallback = me.username?.takeIf { it.isNotBlank() }
            ?: me.email.substringBefore('@').ifBlank { "User" }

        val uiModel = refreshed.toUiModel(fallbackAuthor = authorFallback)
        return ApiResult.Success(
            uiModel.copy(
                audience = audience,
                tripId = selectedTripId,
                routeId = selectedRouteId,
                poiIds = selectedPoiIds,
                photoUris = if (uiModel.photoUris.isNotEmpty()) uiModel.photoUris else attachedPhotoUris,
            )
        )
    }

    override suspend fun deleteMyPost(postId: String): ApiResult<Unit> {
        return when (val idResult = resolveUserId()) {
            is ApiResult.Success -> {
                when (val deleteResult = postApi.deleteForUser(idResult.data, postId)) {
                    is ApiResult.Success -> ApiResult.Success(Unit)
                    else -> deleteResult.asPostResult()
                }
            }
            else -> idResult.asPostResult()
        }
    }

    override suspend fun likePost(postId: String): ApiResult<Unit> {
        return when (val result = postApi.likePost(postId)) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun unlikePost(postId: String): ApiResult<Unit> {
        return when (val result = postApi.unlikePost(postId)) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    private fun normalizeToAbsoluteMediaUrl(url: String): String {
        return toAbsoluteMediaUrl(url)
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T> ApiResult<*>.asPostResult(): ApiResult<T> = this as ApiResult<T>

private fun ApiResult<*>.asPostFailure(defaultMessage: String): ApiResult<Nothing> {
    return when (this) {
        is ApiResult.Success -> ApiResult.UnknownError(defaultMessage)
        is ApiResult.ValidationError -> ApiResult.ValidationError(message.ifBlank { defaultMessage }, fields)
        is ApiResult.Unauthorized -> ApiResult.Unauthorized(message.ifBlank { defaultMessage })
        is ApiResult.Forbidden -> ApiResult.Forbidden(message.ifBlank { defaultMessage })
        is ApiResult.NotFound -> ApiResult.NotFound(message.ifBlank { defaultMessage })
        is ApiResult.ServerError -> ApiResult.ServerError(message.ifBlank { defaultMessage })
        is ApiResult.NetworkError -> ApiResult.NetworkError(message.ifBlank { defaultMessage })
        is ApiResult.UnknownError -> ApiResult.UnknownError(message.ifBlank { defaultMessage })
    }
}

private fun PostDto.toUiModel(
    fallbackAuthor: String = "User",
    bookmarkedPostIds: Set<String> = emptySet(),
): ActivityUiModel {
    val addedDate = isoDateToUiDate(createdAt).ifBlank { currentUiDate() }
    val author = user?.username?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore('@')?.takeIf { it.isNotBlank() }
        ?: fallbackAuthor

    return ActivityUiModel(
        id = id,
        title = title,
        description = body.orEmpty(),
        author = author,
        addedAt = addedDate,
        updatedAt = isoDateToUiDate(modifiedAt).ifBlank { addedDate },
        audience = audience.toUiModel(),
        type = type.toUiModel(),
        activity = activity.toUiModel(),
        tripId = tripId,
        routeId = routeId,
        photoUris = photos.orEmpty().map { toAbsoluteMediaUrl(it.url) },
        likeCount = likeCount,
        liked = liked,
        bookmarked = id in bookmarkedPostIds,
    )
}

private fun PostTypeDto?.toUiModel(): PostTypeUi {
    return when (this) {
        PostTypeDto.CHECK_IN -> PostTypeUi.CHECK_IN
        PostTypeDto.ROUTE -> PostTypeUi.ROUTE
        PostTypeDto.NOTE -> PostTypeUi.NOTE
        PostTypeDto.GEAR -> PostTypeUi.GEAR
        null -> PostTypeUi.CHECK_IN
    }
}

private fun ActivityTypeDto?.toUiModel(): ActivityTypeUi {
    return when (this) {
        ActivityTypeDto.HIKE -> ActivityTypeUi.HIKE
        ActivityTypeDto.BIKEPACKING -> ActivityTypeUi.BIKEPACKING
        ActivityTypeDto.ALPINISM -> ActivityTypeUi.ALPINISM
        ActivityTypeDto.TREK -> ActivityTypeUi.TREK
        ActivityTypeDto.CLIMB -> ActivityTypeUi.CLIMB
        null -> ActivityTypeUi.HIKE
    }
}

private fun NotificationDto.toUiModel(): NotificationUiModel {
    return NotificationUiModel(
        id = id,
        title = title,
        body = body,
        read = read,
        createdAt = isoDateToUiDate(createdAt).ifBlank { currentUiDate() },
    )
}

private fun PostAudienceDto?.toUiModel(): PostAudienceUi {
    return when (this) {
        PostAudienceDto.FRIENDS -> PostAudienceUi.FRIENDS
        PostAudienceDto.CLUB -> PostAudienceUi.CLUB
        PostAudienceDto.PUBLIC, null -> PostAudienceUi.PUBLIC
    }
}

private fun PostAudienceUi.toDto(): PostAudienceDto {
    return when (this) {
        PostAudienceUi.PUBLIC -> PostAudienceDto.PUBLIC
        PostAudienceUi.FRIENDS -> PostAudienceDto.FRIENDS
        PostAudienceUi.CLUB -> PostAudienceDto.CLUB
    }
}

private const val MAX_SERVER_POST_PHOTOS = 10
