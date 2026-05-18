package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.GearRepository
import com.allterra.network.gear.ApiGearStatus
import com.allterra.network.gear.GearApi
import com.allterra.network.gear.GearRequestDto
import com.allterra.network.gear.GearResponseDto
import com.allterra.presentation.gear.GearDraft
import com.allterra.presentation.gear.GearItemUiModel
import com.allterra.presentation.gear.GearStatus

class GearRepositoryImpl(
    private val api: GearApi
) : GearRepository {

    override suspend fun getGearItems(): ApiResult<List<GearItemUiModel>> {
        return when (val result = api.getGearItems()) {
            is ApiResult.Success -> ApiResult.Success(result.data.map { it.toDomain() })
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun createGearItem(draft: GearDraft): ApiResult<GearItemUiModel> {
        return when (val result = api.createGearItem(draft.toRequest())) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun updateGearItem(id: String, draft: GearDraft): ApiResult<GearItemUiModel> {
        return when (val result = api.updateGearItem(id, draft.toRequest())) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun deleteGearItem(id: String): ApiResult<Unit> {
        return api.deleteGearItem(id)
    }

    private fun GearDraft.toRequest(): GearRequestDto {
        return GearRequestDto(
            name = name.trim(),
            category = category.trim(),
            weightKg = weightKg,
            status = status.toApi(),
        )
    }

    private fun GearResponseDto.toDomain(): GearItemUiModel {
        return GearItemUiModel(
            id = id,
            name = name,
            category = category,
            weightKg = weightKg,
            status = when (status) {
                ApiGearStatus.NEW -> GearStatus.NEW
                ApiGearStatus.GOOD -> GearStatus.GOOD
                ApiGearStatus.WORN -> GearStatus.WORN
                ApiGearStatus.BROKEN -> GearStatus.BROKEN
            },
            usesCount = 0,
        )
    }

    private fun GearStatus.toApi(): ApiGearStatus {
        return when (this) {
            GearStatus.NEW -> ApiGearStatus.NEW
            GearStatus.GOOD -> ApiGearStatus.GOOD
            GearStatus.WORN -> ApiGearStatus.WORN
            GearStatus.BROKEN -> ApiGearStatus.BROKEN
        }
    }
}
