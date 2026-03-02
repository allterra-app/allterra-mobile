package com.allterra.network.poi

import com.allterra.config.AppConfig
import com.allterra.core.result.ApiResult
import com.allterra.data.local.TokenStorage
import com.allterra.network.mapErrorResponse
import com.allterra.network.mapThrowableToApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class PoiApiImpl(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
) : PoiApi {

    override suspend fun getByUser(userId: String): ApiResult<List<PoiDto>> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.get("${AppConfig.baseUrl}/pois/users/$userId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<List<PoiDto>>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun createForUser(userId: String, request: PoiCreateRequestDto): ApiResult<PoiDto> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.post("${AppConfig.baseUrl}/pois/users/$userId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<PoiDto>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun getById(poiId: String): ApiResult<PoiDto> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.get("${AppConfig.baseUrl}/pois/$poiId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<PoiDto>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }
}
