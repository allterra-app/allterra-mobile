package com.allterra.network.gear

import kotlinx.serialization.Serializable

@Serializable
enum class ApiGearStatus {
    NEW,
    GOOD,
    WORN,
    BROKEN
}

@Serializable
data class GearRequestDto(
    val name: String,
    val category: String,
    val weightKg: Double,
    val status: ApiGearStatus
)

@Serializable
data class GearResponseDto(
    val id: String,
    val name: String,
    val category: String,
    val weightKg: Double,
    val status: ApiGearStatus,
    val userId: String,
    val createdAt: String? = null,
    val modifiedAt: String? = null
)
