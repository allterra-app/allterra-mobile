package com.allterra.presentation.routes

import com.allterra.presentation.common.model.GeoPoint
import kotlinx.datetime.Instant
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.ExperimentalTime

data class RouteGpxMetrics(
    val routeName: String?,
    val points: List<GeoPoint>,
    val pointCount: Int,
    val distanceKm: Double?,
    val durationMinutes: Long?,
    val startedAt: String?,
)

fun analyzeGpx(gpxContent: String): RouteGpxMetrics {
    val routeName = parseRouteName(gpxContent)

    val pointRegex = Regex(
        "<(?:trkpt|rtept)\\b[^>]*lat=\"([\\-0-9.]+)\"[^>]*lon=\"([\\-0-9.]+)\"[^>]*(?:>(.*?)</(?:trkpt|rtept)>|/>)",
        setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE),
    )
    val timeRegex = Regex("<time>(.*?)</time>", RegexOption.DOT_MATCHES_ALL)

    val points = mutableListOf<GeoPoint>()
    val times = mutableListOf<String>()

    pointRegex.findAll(gpxContent).forEach { match ->
        val lat = match.groupValues.getOrNull(1)?.toDoubleOrNull()
        val lon = match.groupValues.getOrNull(2)?.toDoubleOrNull()
        if (lat != null && lon != null) {
            points += GeoPoint(lat = lat, lon = lon)
            val content = match.groupValues.getOrNull(3).orEmpty()
            val time = timeRegex.find(content)?.groupValues?.getOrNull(1)?.trim()
            if (!time.isNullOrBlank()) times += time
        }
    }

    val distanceKm = if (points.size >= 2) {
        val distanceMeters = points.zipWithNext().sumOf { (a, b) -> haversineMeters(a, b) }
        ((distanceMeters / 1000.0) * 100.0).roundToInt() / 100.0
    } else {
        null
    }

    val durationMinutes = parseDurationMinutes(times.firstOrNull(), times.lastOrNull())
    val startedAt = times.firstOrNull()?.replace("T", " ")?.removeSuffix("Z")

    return RouteGpxMetrics(
        routeName = routeName,
        points = points,
        pointCount = points.size,
        distanceKm = distanceKm,
        durationMinutes = durationMinutes,
        startedAt = startedAt,
    )
}

private fun parseRouteName(gpxContent: String): String? {
    val trackName = Regex("<trk[^>]*>.*?<name>(.*?)</name>", RegexOption.DOT_MATCHES_ALL)
        .find(gpxContent)
        ?.groupValues
        ?.getOrNull(1)
    val routeName = Regex("<rte[^>]*>.*?<name>(.*?)</name>", RegexOption.DOT_MATCHES_ALL)
        .find(gpxContent)
        ?.groupValues
        ?.getOrNull(1)
    val fallbackName = Regex("<name>(.*?)</name>", RegexOption.DOT_MATCHES_ALL)
        .find(gpxContent)
        ?.groupValues
        ?.getOrNull(1)

    return (trackName ?: routeName ?: fallbackName)?.trim()?.takeIf { it.isNotBlank() }
}

private fun haversineMeters(a: GeoPoint, b: GeoPoint): Double {
    val radius = 6_371_000.0
    val lat1 = a.lat.toRadians()
    val lat2 = b.lat.toRadians()
    val dLat = (b.lat - a.lat).toRadians()
    val dLon = (b.lon - a.lon).toRadians()

    val x = sin(dLat / 2) * sin(dLat / 2) +
        cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(x), sqrt(1 - x))
    return radius * c
}

private fun Double.toRadians(): Double = this * (kotlin.math.PI / 180.0)

@OptIn(ExperimentalTime::class)
private fun parseDurationMinutes(first: String?, last: String?): Long? {
    if (first.isNullOrBlank() || last.isNullOrBlank()) return null
    val firstInstant = runCatching { Instant.parse(first) }.getOrNull() ?: return null
    val lastInstant = runCatching { Instant.parse(last) }.getOrNull() ?: return null
    val diffSeconds = lastInstant.epochSeconds - firstInstant.epochSeconds
    if (diffSeconds < 0) return null
    return diffSeconds / 60
}
