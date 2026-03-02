package com.allterra.network.media

import com.allterra.config.AppConfig
import com.allterra.core.result.ApiResult
import com.allterra.data.local.TokenStorage
import com.allterra.network.mapErrorResponse
import com.allterra.network.mapThrowableToApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class MediaApiImpl(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
) : MediaApi {

    override suspend fun upload(fileName: String, contentType: String, bytes: ByteArray): ApiResult<MediaUploadResponseDto> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val safeFileName = sanitizeFileName(fileName)
            val safeContentType = sanitizeContentType(contentType)
            val response = httpClient.post("${AppConfig.baseUrl}/files/upload/raw") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(runCatching { ContentType.parse(safeContentType) }.getOrDefault(ContentType.Application.OctetStream))
                url {
                    parameters.append("fileName", safeFileName)
                    parameters.append("contentType", safeContentType)
                }
                setBody(bytes)
            }

            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<MediaUploadResponseDto>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }
}

private fun sanitizeFileName(original: String): String {
    val trimmed = original.trim().ifBlank { "upload.jpg" }
    val sanitized = trimmed
        .replace("\\", "_")
        .replace("/", "_")
        .replace("\"", "_")
        .replace("\n", "_")
        .replace("\r", "_")

    return sanitized.ifBlank { "upload.jpg" }
}

private fun sanitizeContentType(original: String): String {
    val trimmed = original.trim()
    if (trimmed.isBlank()) return "application/octet-stream"
    if (trimmed.contains("\n") || trimmed.contains("\r")) return "application/octet-stream"
    return trimmed
}
