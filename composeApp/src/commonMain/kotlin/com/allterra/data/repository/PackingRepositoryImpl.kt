package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PackingRepository
import com.allterra.network.gear.ApiGearStatus
import com.allterra.network.packing.PackingApi
import com.allterra.network.packing.PackingItemResponseDto
import com.allterra.presentation.gear.GearStatus
import com.allterra.presentation.packing.PackingItemUiModel

class PackingRepositoryImpl(
    private val packingApi: PackingApi
) : PackingRepository {

    override suspend fun getPackingList(tripId: String): ApiResult<List<PackingItemUiModel>> {
        val result = packingApi.getPackingList(tripId)
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.map { it.toUiModel() })
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun updatePackingStatus(
        tripId: String,
        gearId: String,
        packed: Boolean
    ): ApiResult<PackingItemUiModel> {
        val result = packingApi.updatePackingStatus(tripId, gearId, packed)
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.toUiModel())
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun addGearToTrip(tripId: String, gearId: String): ApiResult<PackingItemUiModel> {
        val result = packingApi.addGearToTrip(tripId, gearId)
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.toUiModel())
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun removeGearFromTrip(tripId: String, gearId: String): ApiResult<Unit> {
        return packingApi.removeGearFromTrip(tripId, gearId)
    }

    private fun PackingItemResponseDto.toUiModel() = PackingItemUiModel(
        gearId = gearId,
        name = name,
        category = category,
        weightKg = weightKg,
        status = status.toUiStatus(),
        packed = packed
    )

    private fun ApiGearStatus.toUiStatus(): GearStatus {
        return when (this) {
            ApiGearStatus.NEW -> GearStatus.NEW
            ApiGearStatus.GOOD -> GearStatus.GOOD
            ApiGearStatus.WORN -> GearStatus.WORN
            ApiGearStatus.BROKEN -> GearStatus.BROKEN
        }
    }
}
