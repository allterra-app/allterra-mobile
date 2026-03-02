package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PostsRepository
import com.allterra.network.media.MediaApi
import com.allterra.network.media.toAbsoluteMediaUrl
import com.allterra.network.post.PostApi
import com.allterra.network.post.PostCreateRequestDto
import com.allterra.network.post.PostDto
import com.allterra.network.post.PostPhotoApi
import com.allterra.network.post.PostPhotoCreateRequestDto
import com.allterra.network.user.UserApi
import com.allterra.platform.LocalFileAccess
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.common.time.currentUiDate

class PostsRepositoryImpl(
    private val userApi: UserApi,
    private val postApi: PostApi,
    private val postPhotoApi: PostPhotoApi,
    private val mediaApi: MediaApi,
) : PostsRepository {

    override suspend fun getFeedPosts(): ApiResult<List<ActivityUiModel>> {
        return when (val postsResult = postApi.getAll()) {
            is ApiResult.Success -> ApiResult.Success(postsResult.data.map { it.toUiModel() })
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

        return when (val postsResult = postApi.getByUser(me.id)) {
            is ApiResult.Success -> ApiResult.Success(
                postsResult.data.map {
                    it.toUiModel(
                        fallbackAuthor = me.username?.takeIf { value -> value.isNotBlank() }
                            ?: me.email.substringBefore('@').ifBlank { "User" },
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

    override suspend fun createMyPost(
        title: String,
        description: String,
        localPhotoPaths: List<String>,
        selectedRouteId: String?,
        selectedPoiIds: List<String>,
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
            title = title,
            body = description.takeIf { it.isNotBlank() },
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
                routeId = selectedRouteId,
                poiIds = selectedPoiIds,
                photoUris = if (uiModel.photoUris.isNotEmpty()) uiModel.photoUris else attachedPhotoUris,
            )
        )
    }

    private fun normalizeToAbsoluteMediaUrl(url: String): String {
        return toAbsoluteMediaUrl(url)
    }
}

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

private fun PostDto.toUiModel(fallbackAuthor: String = "User"): ActivityUiModel {
    val addedDate = isoDateToUiDate(createdAt).ifBlank { currentUiDate() }
    val updatedDate = isoDateToUiDate(modifiedAt).ifBlank { addedDate }
    val author = user?.username?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore('@')?.takeIf { it.isNotBlank() }
        ?: fallbackAuthor

    return ActivityUiModel(
        id = id,
        title = title,
        description = body.orEmpty(),
        author = author,
        addedAt = addedDate,
        updatedAt = updatedDate,
        photoUris = photos.orEmpty().map { toAbsoluteMediaUrl(it.url) },
    )
}

private const val MAX_SERVER_POST_PHOTOS = 10
