package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.presentation.packing.PackingItemUiModel

interface PackingRepository {
    suspend fun getPackingList(tripId: String): ApiResult<List<PackingItemUiModel>>
    suspend fun updatePackingStatus(tripId: String, gearId: String, packed: Boolean): ApiResult<PackingItemUiModel>
    suspend fun addGearToTrip(tripId: String, gearId: String): ApiResult<PackingItemUiModel>
    suspend fun removeGearFromTrip(tripId: String, gearId: String): ApiResult<Unit>
}
