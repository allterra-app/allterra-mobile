package com.allterra.network.route

import com.allterra.core.result.ApiResult

interface RouteApi {
    suspend fun getByUser(userId: String): ApiResult<List<RouteDto>>
    suspend fun createForUser(userId: String, request: RouteCreateRequestDto): ApiResult<RouteDto>
    suspend fun deleteForUser(userId: String, routeId: String): ApiResult<Unit>
}
