package com.allterra.network.document

import com.allterra.config.AppConfig
import com.allterra.core.result.ApiResult
import com.allterra.data.local.TokenStorage
import com.allterra.network.mapErrorResponse
import com.allterra.network.mapThrowableToApiResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class DocumentApi(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun getDocuments(): ApiResult<List<DocumentResponseDto>> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.get("${AppConfig.baseUrl}/documents") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }

    suspend fun getDocument(id: String): ApiResult<DocumentResponseDto> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.get("${AppConfig.baseUrl}/documents/$id") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }

    suspend fun createDocument(request: DocumentRequestDto): ApiResult<DocumentResponseDto> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.post("${AppConfig.baseUrl}/documents") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }

    suspend fun deleteDocument(id: String): ApiResult<Unit> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.delete("${AppConfig.baseUrl}/documents/$id") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(Unit)
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }
}
