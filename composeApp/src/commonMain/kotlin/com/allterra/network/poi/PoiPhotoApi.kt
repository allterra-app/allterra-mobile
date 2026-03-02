package com.allterra.network.poi

import com.allterra.core.result.ApiResult

interface PoiPhotoApi {
    suspend fun create(request: PoiPhotoCreateRequestDto): ApiResult<PoiPhotoDto>
}
