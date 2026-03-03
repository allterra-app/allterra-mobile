package com.allterra.network.route

import kotlinx.serialization.Serializable

@Serializable
data class RouteCreateRequestDto(
    val userId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val gpxFileId: String,
)

@Serializable
data class RoutePointDto(
    val lat: Double,
    val lon: Double,
)

@Serializable
data class RouteDto(
    val id: String,
    val userId: String? = null,
    val title: String,
    val description: String? = null,
    val gpxFileId: String? = null,
    val gpxFileName: String? = null,
    val gpxContentType: String? = null,
    val gpxFileUrl: String? = null,
    val distanceKm: Double? = null,
    val durationMinutes: Long? = null,
    val pointCount: Int? = null,
    val startedAt: String? = null,
    val previewPoints: List<RoutePointDto> = emptyList(),
    val createdAt: String? = null,
    val modifiedAt: String? = null,
)
