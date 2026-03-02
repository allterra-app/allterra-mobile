package com.allterra.network.user

import com.allterra.config.AppConfig
import com.allterra.core.result.ApiResult
import com.allterra.data.local.TokenStorage
import com.allterra.network.mapErrorResponse
import com.allterra.network.mapThrowableToApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

class UserApiImpl(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
) : UserApi {

    override suspend fun me(): ApiResult<UserMeDto> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.get("${AppConfig.baseUrl}/users/me") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<UserMeDto>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }
}
