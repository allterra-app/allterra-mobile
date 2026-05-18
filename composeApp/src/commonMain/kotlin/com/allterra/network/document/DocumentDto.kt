package com.allterra.network.document

import kotlinx.serialization.Serializable

@Serializable
enum class ApiDocumentType {
    TICKET,
    BOOKING,
    INSURANCE,
    OTHER
}

@Serializable
data class DocumentRequestDto(
    val title: String,
    val type: ApiDocumentType,
    val fileId: String?,
    val tripId: String?,
    val metadata: String?
)

@Serializable
data class DocumentResponseDto(
    val id: String,
    val title: String,
    val type: ApiDocumentType,
    val fileId: String?,
    val tripId: String?,
    val fileUrl: String?,
    val metadata: String?,
    val createdAt: String,
    val modifiedAt: String
)
