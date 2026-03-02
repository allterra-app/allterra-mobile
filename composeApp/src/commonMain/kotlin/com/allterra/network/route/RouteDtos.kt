package com.allterra.network.route

import kotlinx.serialization.Serializable

@Serializable
data class RouteCreateRequestDto(
    val userId: String? = null,
    val title: String,
    val description: String? = null,
    val gpxContent: String,
    val distanceKm: Double? = null,
    val durationMinutes: Long? = null,
    val pointCount: Int? = null,
)

@Serializable
data class RouteDto(
    val id: String,
    val userId: String? = null,
    val title: String,
    val description: String? = null,
    val gpxContent: String,
    val distanceKm: Double? = null,
    val durationMinutes: Long? = null,
    val pointCount: Int? = null,
    val createdAt: String? = null,
    val modifiedAt: String? = null,
)
