package com.allterra.network.trip

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

class TripApi(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun getMyTrips(): ApiResult<List<TripResponseDto>> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.get("${AppConfig.baseUrl}/trips") {
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

    suspend fun getTrip(id: String): ApiResult<TripResponseDto> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.get("${AppConfig.baseUrl}/trips/$id") {
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

    suspend fun createTrip(request: TripRequestDto): ApiResult<TripResponseDto> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.post("${AppConfig.baseUrl}/trips") {
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

    suspend fun deleteTrip(id: String): ApiResult<Unit> {
        return try {
            val accessToken = tokenStorage.getAccessToken() ?: return ApiResult.Unauthorized("Missing token")
            val response = httpClient.delete("${AppConfig.baseUrl}/trips/$id") {
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
