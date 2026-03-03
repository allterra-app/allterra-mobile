package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.presentation.common.model.RouteUiModel

enum class RouteCreateProgressStage {
    UPLOADING,
    PROCESSING,
}

interface RoutesRepository {
    suspend fun getMyRoutes(): ApiResult<List<RouteUiModel>>
    suspend fun createRoute(
        title: String,
        description: String,
        fileName: String,
        contentType: String,
        fileBytes: ByteArray,
        onProgress: (RouteCreateProgressStage) -> Unit = {},
    ): ApiResult<RouteUiModel>
    suspend fun deleteMyRoute(routeId: String): ApiResult<Unit>
}
