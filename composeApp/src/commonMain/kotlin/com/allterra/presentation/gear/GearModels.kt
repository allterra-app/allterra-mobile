package com.allterra.presentation.gear

enum class GearStatus {
    NEW,
    GOOD,
    WORN,
    BROKEN
}

data class GearItemUiModel(
    val id: String,
    val name: String,
    val category: String,
    val weightKg: Double,
    val status: GearStatus,
    val usesCount: Int,
)

data class GearDraft(
    val name: String,
    val category: String,
    val weightKg: Double,
    val status: GearStatus,
)
