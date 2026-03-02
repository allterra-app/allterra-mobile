package com.allterra.network.media

import kotlinx.serialization.Serializable

@Serializable
data class MediaUploadResponseDto(
    val id: String,
    val fileName: String? = null,
    val contentType: String? = null,
    val size: Long? = null,
    val url: String,
)
