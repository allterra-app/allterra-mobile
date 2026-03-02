package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.presentation.common.model.PoiType
import com.allterra.presentation.common.model.PoiUiModel

interface PoisRepository {
    suspend fun getMyPois(): ApiResult<List<PoiUiModel>>
    suspend fun createPoi(
        name: String,
        type: PoiType,
        localPhotoPaths: List<String>,
    ): ApiResult<PoiUiModel>
}
