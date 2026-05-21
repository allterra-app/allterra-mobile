package com.allterra.presentation.packing

import com.allterra.presentation.gear.GearStatus

data class PackingItemUiModel(
    val gearId: String,
    val name: String,
    val category: String,
    val weightKg: Double,
    val status: GearStatus,
    val packed: Boolean
)
