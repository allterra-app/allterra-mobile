package com.allterra.network.poi

import com.allterra.core.result.ApiResult

interface PoiApi {
    suspend fun getByUser(userId: String): ApiResult<List<PoiDto>>
    suspend fun createForUser(userId: String, request: PoiCreateRequestDto): ApiResult<PoiDto>
    suspend fun getById(poiId: String): ApiResult<PoiDto>
    suspend fun deleteForUser(userId: String, poiId: String): ApiResult<Unit>
}
