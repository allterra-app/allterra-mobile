package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PoisRepository
import com.allterra.network.media.MediaApi
import com.allterra.network.media.toAbsoluteMediaUrl
import com.allterra.network.poi.PoiApi
import com.allterra.network.poi.PoiCreateRequestDto
import com.allterra.network.poi.PoiDto
import com.allterra.network.poi.PoiPhotoApi
import com.allterra.network.poi.PoiPhotoCreateRequestDto
import com.allterra.network.user.UserApi
import com.allterra.platform.LocalFileAccess
import com.allterra.presentation.common.model.PoiType
import com.allterra.presentation.common.model.PoiUiModel
import com.allterra.presentation.common.time.currentUiDate

class PoisRepositoryImpl(
    private val userApi: UserApi,
    private val poiApi: PoiApi,
    private val poiPhotoApi: PoiPhotoApi,
    private val mediaApi: MediaApi,
) : PoisRepository {

    override suspend fun getMyPois(): ApiResult<List<PoiUiModel>> {
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

        return when (val poisResult = poiApi.getByUser(me.id)) {
            is ApiResult.Success -> ApiResult.Success(poisResult.data.map { it.toUiModel() })
            is ApiResult.ValidationError -> ApiResult.ValidationError(poisResult.message, poisResult.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(poisResult.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(poisResult.message)
            is ApiResult.NotFound -> ApiResult.NotFound(poisResult.message)
            is ApiResult.ServerError -> ApiResult.ServerError(poisResult.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(poisResult.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(poisResult.message)
        }
    }

    override suspend fun createPoi(
        name: String,
        type: PoiType,
        localPhotoPaths: List<String>,
    ): ApiResult<PoiUiModel> {
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

        val createRequest = PoiCreateRequestDto(
            userId = me.id,
            name = name,
            type = type.name,
        )
        val createdPoi = when (val createResult = poiApi.createForUser(me.id, createRequest)) {
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
        for (localPath in localPhotoPaths.take(MAX_SERVER_POI_PHOTOS)) {
            val bytes = LocalFileAccess.readBytes(localPath)
                ?: return ApiResult.ValidationError("Unable to read selected photo", emptyMap())
            val fileName = LocalFileAccess.fileName(localPath)
            val contentType = LocalFileAccess.contentType(localPath)

            val uploaded = when (val uploadResult = mediaApi.upload(fileName, contentType, bytes)) {
                is ApiResult.Success -> uploadResult.data
                else -> return uploadResult.asPoiFailure("Failed to upload POI photo")
            }

            val absoluteUrl = normalizeToAbsoluteMediaUrl(uploaded.url)
            when (val photoCreateResult = poiPhotoApi.create(
                PoiPhotoCreateRequestDto(
                    url = absoluteUrl,
                    poiId = createdPoi.id,
                )
            )) {
                is ApiResult.Success -> Unit
                else -> return photoCreateResult.asPoiFailure("Failed to attach photo to POI")
            }
            attachedPhotoUris += absoluteUrl
        }

        return when (val refreshed = poiApi.getByUser(me.id)) {
            is ApiResult.Success -> {
                val actual = refreshed.data.firstOrNull { it.id == createdPoi.id } ?: createdPoi
                val uiModel = actual.toUiModel()
                ApiResult.Success(
                    uiModel.copy(
                        photoUris = if (uiModel.photoUris.isNotEmpty()) uiModel.photoUris else attachedPhotoUris,
                    )
                )
            }

            is ApiResult.Unauthorized -> ApiResult.Unauthorized(refreshed.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(refreshed.message)
            is ApiResult.ValidationError,
            is ApiResult.NotFound,
            is ApiResult.ServerError,
            is ApiResult.NetworkError,
            is ApiResult.UnknownError -> ApiResult.Success(
                createdPoi.toUiModel().copy(
                    photoUris = attachedPhotoUris,
                )
            )
        }
    }

    private fun normalizeToAbsoluteMediaUrl(url: String): String {
        return toAbsoluteMediaUrl(url)
    }
}

private fun ApiResult<*>.asPoiFailure(defaultMessage: String): ApiResult<Nothing> {
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

private fun PoiDto.toUiModel(): PoiUiModel {
    val added = isoDateToUiDate(createdAt).ifBlank { currentUiDate() }
    val updated = isoDateToUiDate(modifiedAt).ifBlank { added }
    val mappedType = runCatching { PoiType.valueOf(type ?: PoiType.OTHER.name) }.getOrDefault(PoiType.OTHER)
    return PoiUiModel(
        id = id,
        title = name,
        type = mappedType,
        photoUris = photos.orEmpty().map { toAbsoluteMediaUrl(it.url) },
        addedAt = added,
        updatedAt = updated,
    )
}

private const val MAX_SERVER_POI_PHOTOS = 5
