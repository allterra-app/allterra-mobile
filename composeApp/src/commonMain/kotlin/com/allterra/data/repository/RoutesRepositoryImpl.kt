package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.RouteCreateProgressStage
import com.allterra.domain.repository.RoutesRepository
import com.allterra.network.media.MediaApi
import com.allterra.network.route.RouteApi
import com.allterra.network.route.RouteCreateRequestDto
import com.allterra.network.route.RouteDto
import com.allterra.network.user.UserApi
import com.allterra.presentation.common.model.GeoPoint
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.common.time.currentUiDate

class RoutesRepositoryImpl(
    private val userApi: UserApi,
    private val routeApi: RouteApi,
    private val mediaApi: MediaApi,
) : RoutesRepository {

    override suspend fun getMyRoutes(): ApiResult<List<RouteUiModel>> {
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

        return when (val routesResult = routeApi.getByUser(me.id)) {
            is ApiResult.Success -> ApiResult.Success(routesResult.data.map { it.toUiModel() })
            is ApiResult.ValidationError -> ApiResult.ValidationError(routesResult.message, routesResult.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(routesResult.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(routesResult.message)
            is ApiResult.NotFound -> ApiResult.NotFound(routesResult.message)
            is ApiResult.ServerError -> ApiResult.ServerError(routesResult.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(routesResult.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(routesResult.message)
        }
    }

    override suspend fun createRoute(
        title: String,
        description: String,
        fileName: String,
        contentType: String,
        fileBytes: ByteArray,
        onProgress: (RouteCreateProgressStage) -> Unit,
    ): ApiResult<RouteUiModel> {
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

        onProgress(RouteCreateProgressStage.UPLOADING)
        val uploaded = when (val uploadResult = mediaApi.upload(fileName, contentType, fileBytes)) {
            is ApiResult.Success -> uploadResult.data
            is ApiResult.ValidationError -> return ApiResult.ValidationError(uploadResult.message, uploadResult.fields)
            is ApiResult.Unauthorized -> return ApiResult.Unauthorized(uploadResult.message)
            is ApiResult.Forbidden -> return ApiResult.Forbidden(uploadResult.message)
            is ApiResult.NotFound -> return ApiResult.NotFound(uploadResult.message)
            is ApiResult.ServerError -> return ApiResult.ServerError(uploadResult.message)
            is ApiResult.NetworkError -> return ApiResult.NetworkError(uploadResult.message)
            is ApiResult.UnknownError -> return ApiResult.UnknownError(uploadResult.message)
        }

        onProgress(RouteCreateProgressStage.PROCESSING)
        val request = RouteCreateRequestDto(
            userId = me.id,
            title = title.takeIf { it.isNotBlank() },
            description = description.takeIf { it.isNotBlank() },
            gpxFileId = uploaded.id,
        )

        return when (val createResult = routeApi.createForUser(me.id, request)) {
            is ApiResult.Success -> ApiResult.Success(createResult.data.toUiModel())
            is ApiResult.ValidationError -> ApiResult.ValidationError(createResult.message, createResult.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(createResult.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(createResult.message)
            is ApiResult.NotFound -> ApiResult.NotFound(createResult.message)
            is ApiResult.ServerError -> ApiResult.ServerError(createResult.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(createResult.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(createResult.message)
        }
    }

    override suspend fun deleteMyRoute(routeId: String): ApiResult<Unit> {
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

        return when (val deleteResult = routeApi.deleteForUser(me.id, routeId)) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.ValidationError -> ApiResult.ValidationError(deleteResult.message, deleteResult.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(deleteResult.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(deleteResult.message)
            is ApiResult.NotFound -> ApiResult.NotFound(deleteResult.message)
            is ApiResult.ServerError -> ApiResult.ServerError(deleteResult.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(deleteResult.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(deleteResult.message)
        }
    }
}

private fun RouteDto.toUiModel(): RouteUiModel {
    val date = isoDateToUiDate(createdAt).ifBlank { isoDateToUiDate(modifiedAt) }.ifBlank { currentUiDate() }
    val preview = previewPoints.map { GeoPoint(lat = it.lat, lon = it.lon) }
    return RouteUiModel(
        id = id,
        title = title,
        description = description.orEmpty(),
        date = date,
        source = gpxFileName ?: "GPX",
        distanceKm = distanceKm,
        durationMinutes = durationMinutes,
        pointCount = pointCount ?: preview.size,
        previewPoints = preview,
    )
}
