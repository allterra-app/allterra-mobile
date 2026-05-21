package com.allterra.network.packing

import com.allterra.network.gear.ApiGearStatus
import kotlinx.serialization.Serializable

@Serializable
data class PackingItemResponseDto(
    val gearId: String,
    val name: String,
    val category: String,
    val weightKg: Double,
    val status: ApiGearStatus,
    val packed: Boolean
)

@Serializable
data class PackingStatusUpdateDto(
    val packed: Boolean
)
