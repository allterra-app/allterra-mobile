package com.allterra.network.trip

import kotlinx.serialization.Serializable

@Serializable
enum class ApiTripStatus {
    PLANNED,
    ACTIVE,
    DONE,
    CANCELLED
}

@Serializable
enum class ApiTripActivity {
    TREK,
    ALPINE,
    BIKE,
    SKI
}

@Serializable
data class TripRequestDto(
    val title: String,
    val region: String?,
    val status: ApiTripStatus,
    val startDate: String?,
    val endDate: String?,
    val activity: ApiTripActivity?,
    val routeId: String?
)

@Serializable
data class TripResponseDto(
    val id: String,
    val userId: String,
    val title: String,
    val region: String?,
    val status: ApiTripStatus,
    val startDate: String?,
    val endDate: String?,
    val activity: ApiTripActivity?,
    val routeId: String?,
    val createdAt: String,
    val modifiedAt: String
)
