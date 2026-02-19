package com.allterra.network

import com.allterra.network.dto.ApiErrorDto
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val REQUEST_TIMEOUT_MS = 30_000L
private const val CONNECT_TIMEOUT_MS = 15_000L
private const val SOCKET_TIMEOUT_MS = 30_000L

fun createHttpClient(): HttpClient {
    return HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                }
            )
        }
    }
}

internal val errorJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

internal fun parseApiError(raw: String?): ApiErrorDto? {
    if (raw.isNullOrBlank()) return null
    return runCatching { errorJson.decodeFromString<ApiErrorDto>(raw) }.getOrNull()
}
