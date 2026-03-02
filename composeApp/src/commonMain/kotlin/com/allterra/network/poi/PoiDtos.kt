package com.allterra.network.poi

import kotlinx.serialization.Serializable

@Serializable
data class PoiRefDto(
    val id: String,
)

@Serializable
data class PoiPhotoDto(
    val id: String? = null,
    val url: String,
)

@Serializable
data class PoiCreateRequestDto(
    val userId: String? = null,
    val name: String,
    val description: String? = null,
    val rating: Int = 0,
    val actual: Boolean = true,
    val url: String? = null,
    val type: String = "OTHER",
)

@Serializable
data class PoiDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val rating: Int = 0,
    val actual: Boolean = true,
    val url: String? = null,
    val type: String? = null,
    val photos: List<PoiPhotoDto>? = null,
    val createdAt: String? = null,
    val modifiedAt: String? = null,
)

@Serializable
data class PoiPhotoCreateRequestDto(
    val url: String,
    val poiId: String,
)
