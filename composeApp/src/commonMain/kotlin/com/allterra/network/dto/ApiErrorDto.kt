package com.allterra.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val status: String? = null,
    val code: Int? = null,
    val error: String? = null,
    val message: String? = null,
    val path: String? = null,
    val timestamp: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
)
