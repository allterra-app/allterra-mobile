package com.allterra.network.poi

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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class PoiPhotoApiImpl(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
) : PoiPhotoApi {

    override suspend fun create(request: PoiPhotoCreateRequestDto): ApiResult<PoiPhotoDto> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.post("${AppConfig.baseUrl}/poi-photos") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<PoiPhotoDto>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }
}
