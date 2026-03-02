package com.allterra.network.auth

import com.allterra.config.AppConfig
import com.allterra.core.result.ApiResult
import com.allterra.domain.model.AuthTokens
import com.allterra.network.parseApiError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class AuthApiImpl(
    private val httpClient: HttpClient,
) : AuthApi {

    override suspend fun login(email: String, password: String): ApiResult<AuthTokens> {
        return runAuthCall {
            httpClient.post("${AppConfig.baseUrl}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email = email, password = password))
            }
        }
    }

    override suspend fun register(email: String, password: String): ApiResult<AuthTokens> {
        return runAuthCall {
            httpClient.post("${AppConfig.baseUrl}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequestDto(email = email, password = password))
            }
        }
    }

    override suspend fun refresh(refreshToken: String): ApiResult<AuthTokens> {
        return runAuthCall {
            httpClient.post("${AppConfig.baseUrl}/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequestDto(refreshToken = refreshToken))
            }
        }
    }

    override suspend fun logout(refreshToken: String): ApiResult<Unit> {
        return try {
            val response = httpClient.delete("${AppConfig.baseUrl}/auth/logout") {
                contentType(ContentType.Application.Json)
                setBody(LogoutRequestDto(refreshToken = refreshToken))
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(Unit)
            } else {
                mapErrorResponse(response.status.value, response.body<String>())
            }
        } catch (exception: Throwable) {
            mapThrowable(exception)
        }
    }

    private suspend fun runAuthCall(request: suspend () -> HttpResponse): ApiResult<AuthTokens> {
        return try {
            val response = request()
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<AuthTokensDto>().toDomain())
            } else {
                mapErrorResponse(response.status.value, response.body<String>())
            }
        } catch (exception: Throwable) {
            mapThrowable(exception)
        }
    }

    private fun mapErrorResponse(statusCode: Int, bodyText: String?): ApiResult<Nothing> {
        val error = parseApiError(bodyText)
        return when (statusCode) {
            400 -> ApiResult.ValidationError(
                message = error?.message ?: "Bad request",
                fields = error?.validationErrors.orEmpty(),
            )
            401 -> ApiResult.Unauthorized(error?.message ?: "Unauthorized")
            403 -> ApiResult.Forbidden(error?.message ?: "Forbidden")
            404 -> ApiResult.NotFound(error?.message ?: "Not found")
            409 -> ApiResult.ValidationError(
                message = error?.message ?: "Conflict",
                fields = error?.validationErrors.orEmpty(),
            )
            in 300..399 -> ApiResult.UnknownError(
                error?.message ?: "Request was redirected. Check API URL and HTTPS configuration."
            )
            in 500..599 -> ApiResult.ServerError(error?.message ?: "Internal server error")
            else -> ApiResult.UnknownError(error?.message ?: "Request failed")
        }
    }

    private suspend fun mapThrowable(throwable: Throwable): ApiResult<Nothing> {
        return when (throwable) {
            is CancellationException -> throw throwable
            is TimeoutCancellationException,
            is HttpRequestTimeoutException -> ApiResult.NetworkError(
                "Request timed out. Check your connection and try again."
            )
            is UnresolvedAddressException -> ApiResult.NetworkError(
                "Server address is unreachable. Check network or backend URL."
            )
            is IOException -> ApiResult.NetworkError(
                throwable.message?.let { message ->
                    if (message.contains("Connection refused", ignoreCase = true)
                        || message.contains("ECONNREFUSED", ignoreCase = true)
                    ) {
                        "Server is unavailable. Please make sure backend is running."
                    } else {
                        "Network error. Check your connection and try again."
                    }
                } ?: "Network error. Check your connection and try again."
            )
            is ClientRequestException -> mapErrorResponse(
                throwable.response.status.value,
                throwable.response.body<String>()
            )
            is ServerResponseException -> {
                val bodyText = throwable.response.body<String>()
                val error = parseApiError(bodyText)
                ApiResult.ServerError(error?.message ?: "Internal server error")
            }
            is ResponseException -> ApiResult.UnknownError("Request failed")
            else -> ApiResult.NetworkError("Unable to reach server. Please try again.")
        }
    }
}

private fun AuthTokensDto.toDomain(): AuthTokens {
    return AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}
