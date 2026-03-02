package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.routes.RouteGpxMetrics

interface RoutesRepository {
    suspend fun getMyRoutes(): ApiResult<List<RouteUiModel>>
    suspend fun createRoute(
        title: String,
        description: String,
        gpxContent: String,
        metrics: RouteGpxMetrics,
    ): ApiResult<RouteUiModel>
    suspend fun deleteMyRoute(routeId: String): ApiResult<Unit>
}
