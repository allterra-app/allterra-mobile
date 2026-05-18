package com.allterra.network.gear

import com.allterra.config.AppConfig
import com.allterra.core.result.ApiResult
import com.allterra.data.local.TokenStorage
import com.allterra.network.mapErrorResponse
import com.allterra.network.mapThrowableToApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class GearApi(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun getGearItems(): ApiResult<List<GearResponseDto>> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.get("${AppConfig.baseUrl}/gear") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(response.body())
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }

    suspend fun createGearItem(request: GearRequestDto): ApiResult<GearResponseDto> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.post("${AppConfig.baseUrl}/gear") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) ApiResult.Success(response.body())
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (e: Throwable) {
            println("ALLTERRA_GEAR_DEBUG createGearItem throwable=${e::class.qualifiedName} message=${e.message}")
            e.printStackTrace()
            mapThrowableToApiResult(e)
        }
    }

    suspend fun updateGearItem(id: String, request: GearRequestDto): ApiResult<GearResponseDto> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.put("${AppConfig.baseUrl}/gear/$id") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) ApiResult.Success(response.body())
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }

    suspend fun deleteGearItem(id: String): ApiResult<Unit> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.delete("${AppConfig.baseUrl}/gear/$id") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(Unit)
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }
}
