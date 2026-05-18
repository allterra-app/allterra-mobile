package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.TripRepository
import com.allterra.network.trip.ApiTripStatus
import com.allterra.network.trip.TripApi
import com.allterra.network.trip.TripResponseDto
import com.allterra.presentation.trips.TripStatus
import com.allterra.presentation.trips.TripUiModel
import com.allterra.presentation.trips.TripReadiness
import com.allterra.presentation.trips.TripBudget

class TripRepositoryImpl(
    private val api: TripApi
) : TripRepository {

    override suspend fun getMyTrips(): ApiResult<List<TripUiModel>> {
        return when (val result = api.getMyTrips()) {
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

    override suspend fun getTrip(id: String): ApiResult<TripUiModel> {
        return when (val result = api.getTrip(id)) {
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

    override suspend fun deleteTrip(id: String): ApiResult<Unit> {
        return api.deleteTrip(id)
    }

    private fun TripResponseDto.toDomain(): TripUiModel {
        return TripUiModel(
            id = id,
            title = title,
            region = region ?: "Unknown",
            status = when (status) {
                ApiTripStatus.PLANNED -> TripStatus.Planned
                ApiTripStatus.ACTIVE -> TripStatus.Active
                ApiTripStatus.DONE -> TripStatus.Done
                ApiTripStatus.CANCELLED -> TripStatus.Cancelled
            },
            dates = "${startDate ?: ""} - ${endDate ?: ""}",
            routeId = routeId,
            readiness = TripReadiness(0, 5, 0, 10, routeId != null, false), // Derived later
            budget = TripBudget(0, 1000) // Placeholder
        )
    }
}
