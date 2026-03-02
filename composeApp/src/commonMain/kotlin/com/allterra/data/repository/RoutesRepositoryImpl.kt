package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.RoutesRepository
import com.allterra.network.route.RouteApi
import com.allterra.network.route.RouteCreateRequestDto
import com.allterra.network.route.RouteDto
import com.allterra.network.user.UserApi
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.common.time.currentUiDate
import com.allterra.presentation.routes.RouteGpxMetrics
import com.allterra.presentation.routes.analyzeGpx
import kotlin.math.roundToInt

class RoutesRepositoryImpl(
    private val userApi: UserApi,
    private val routeApi: RouteApi,
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
        gpxContent: String,
        metrics: RouteGpxMetrics,
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

        val request = RouteCreateRequestDto(
            userId = me.id,
            title = title,
            description = description.takeIf { it.isNotBlank() },
            gpxContent = gpxContent,
            distanceKm = metrics.distanceKm,
            durationMinutes = metrics.durationMinutes,
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
}

private fun RouteDto.toUiModel(): RouteUiModel {
    val analyzed = analyzeGpx(gpxContent)
    val date = isoDateToUiDate(createdAt).ifBlank { isoDateToUiDate(modifiedAt) }.ifBlank { currentUiDate() }
    return RouteUiModel(
        id = id,
        title = title,
        description = description.orEmpty(),
        date = date,
        source = "GPX",
        distanceKm = distanceKm ?: analyzed.distanceKm,
        durationMinutes = durationMinutes ?: analyzed.durationMinutes,
        pointCount = pointCount ?: analyzed.pointCount,
        previewPoints = analyzed.points.sampleForPreview(MAX_ROUTE_PREVIEW_POINTS),
    )
}

private fun List<com.allterra.presentation.common.model.GeoPoint>.sampleForPreview(maxPoints: Int): List<com.allterra.presentation.common.model.GeoPoint> {
    if (size <= maxPoints) return this
    val step = (size - 1).toDouble() / (maxPoints - 1).toDouble()
    return List(maxPoints) { index ->
        this[(index * step).roundToInt().coerceIn(0, lastIndex)]
    }
}

private const val MAX_ROUTE_PREVIEW_POINTS = 300
