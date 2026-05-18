package com.allterra.presentation.trips

data class TripReadiness(
    val docsReady: Int,
    val docsTotal: Int,
    val gearReady: Int,
    val gearTotal: Int,
    val routeReady: Boolean,
    val briefingReady: Boolean,
)

data class TripBudget(
    val spent: Int,
    val planned: Int,
)

data class TripWaypoint(
    val title: String,
    val meta: String,
)

data class TripNote(
    val title: String,
    val body: String,
    val date: String,
    val author: String,
)

enum class TripStatus {
    Planned,
    Active,
    Done,
    Cancelled,
}

data class TripUiModel(
    val id: String,
    val title: String,
    val region: String,
    val status: TripStatus,
    val dates: String,
    val daysLeft: Int? = null,
    val members: List<String> = emptyList(),
    val routeId: String? = null,
    val distanceKm: Double = 0.0,
    val elevationM: Int = 0,
    val durationLabel: String = "",
    val docsCount: Int = 0,
    val gearCount: Int = 0,
    val pinsCount: Int = 0,
    val notesCount: Int = 0,
    val readiness: TripReadiness = TripReadiness(0, 0, 0, 0, false, false),
    val budget: TripBudget = TripBudget(0, 0),
    val weatherLabel: String = "",
    val activeProgress: Float? = null,
    val activeDayLabel: String? = null,
    val notes: List<TripNote> = emptyList(),
    val waypoints: List<TripWaypoint> = emptyList(),
)
