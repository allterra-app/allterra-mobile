package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.presentation.gear.GearDraft
import com.allterra.presentation.gear.GearItemUiModel

interface GearRepository {
    suspend fun getGearItems(): ApiResult<List<GearItemUiModel>>
    suspend fun createGearItem(draft: GearDraft): ApiResult<GearItemUiModel>
    suspend fun updateGearItem(id: String, draft: GearDraft): ApiResult<GearItemUiModel>
    suspend fun deleteGearItem(id: String): ApiResult<Unit>
}
