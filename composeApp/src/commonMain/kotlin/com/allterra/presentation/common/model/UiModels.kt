package com.allterra.presentation.common.model

enum class PostAudienceUi {
    PUBLIC,
    FRIENDS,
    CLUB,
}

enum class PostTypeUi {
    CHECK_IN,
    ROUTE,
    NOTE,
    GEAR,
}

enum class ActivityTypeUi {
    HIKE,
    BIKEPACKING,
    ALPINISM,
    TREK,
    CLIMB,
}

data class ActivityUiModel(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val addedAt: String,
    val updatedAt: String,
    val audience: PostAudienceUi = PostAudienceUi.PUBLIC,
    val type: PostTypeUi = PostTypeUi.CHECK_IN,
    val activity: ActivityTypeUi = ActivityTypeUi.HIKE,
    val tripId: String? = null,
    val routeId: String? = null,
    val poiIds: List<String> = emptyList(),
    val photoUris: List<String> = emptyList(),
    val likeCount: Int = 0,
    val liked: Boolean = false,
    val bookmarked: Boolean = false,
    val distanceKm: Double? = null,
    val elevationGain: Int? = null,
)

data class NotificationUiModel(
    val id: String,
    val title: String,
    val body: String,
    val read: Boolean,
    val createdAt: String,
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

/**
 * Shared extension to extract initials from a name string.
 */
fun String.initials(): String {
    return split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { take(2).uppercase() }
}

/**
 * Shared extension to generate a handle from a name string.
 */
fun String.handle(): String {
    return lowercase().replace(' ', '.')
}

