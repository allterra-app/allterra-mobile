package com.allterra.domain.repository

import com.allterra.core.result.ApiResult

interface TripRepository {
    suspend fun getMyTrips(): ApiResult<List<com.allterra.presentation.trips.TripUiModel>>
    suspend fun getTrip(id: String): ApiResult<com.allterra.presentation.trips.TripUiModel>
    suspend fun deleteTrip(id: String): ApiResult<Unit>
}
