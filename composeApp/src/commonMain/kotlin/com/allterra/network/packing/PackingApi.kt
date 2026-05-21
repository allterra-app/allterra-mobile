package com.allterra.network.packing

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

class PackingApi(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun getPackingList(tripId: String): ApiResult<List<PackingItemResponseDto>> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.get("${AppConfig.baseUrl}/trips/$tripId/gear") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(response.body())
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }

    suspend fun addGearToTrip(tripId: String, gearId: String): ApiResult<PackingItemResponseDto> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.post("${AppConfig.baseUrl}/trips/$tripId/gear/$gearId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(response.body())
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }

    suspend fun updatePackingStatus(
        tripId: String,
        gearId: String,
        packed: Boolean
    ): ApiResult<PackingItemResponseDto> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.put("${AppConfig.baseUrl}/trips/$tripId/gear/$gearId/packed") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(PackingStatusUpdateDto(packed))
            }
            if (response.status.isSuccess()) ApiResult.Success(response.body())
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }

    suspend fun removeGearFromTrip(tripId: String, gearId: String): ApiResult<Unit> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.delete("${AppConfig.baseUrl}/trips/$tripId/gear/$gearId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(Unit)
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (e: Throwable) {
            mapThrowableToApiResult(e)
        }
    }
}
