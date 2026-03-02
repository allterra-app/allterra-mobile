package com.allterra.presentation.common.model

data class ActivityUiModel(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val addedAt: String,
    val updatedAt: String,
    val routeId: String? = null,
    val poiIds: List<String> = emptyList(),
    val photoUris: List<String> = emptyList(),
    val liked: Boolean = false,
    val bookmarked: Boolean = false,
)

data class GeoPoint(
    val lat: Double,
    val lon: Double,
)

enum class PoiType {
    SHOP,
    PARKING,
    CAMPING,
    WATER_SOURCE,
    OTHER,
}

data class PoiUiModel(
    val id: String,
    val title: String,
    val type: PoiType = PoiType.OTHER,
    val photoUris: List<String> = emptyList(),
    val addedAt: String,
    val updatedAt: String,
)

data class RouteUiModel(
    val id: String,
    val title: String,
    val description: String = "",
    val date: String,
    val source: String,
    val distanceKm: Double? = null,
    val durationMinutes: Long? = null,
    val pointCount: Int = 0,
    val previewPoints: List<GeoPoint> = emptyList(),
)
