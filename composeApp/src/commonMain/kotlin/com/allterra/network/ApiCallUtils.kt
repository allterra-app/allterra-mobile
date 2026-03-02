package com.allterra.network

import com.allterra.core.result.ApiResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlin.coroutines.cancellation.CancellationException

internal fun mapErrorResponse(statusCode: Int, bodyText: String?): ApiResult<Nothing> {
    val error = parseApiError(bodyText)
    return when (statusCode) {
        HttpStatusCode.BadRequest.value -> ApiResult.ValidationError(
            message = error?.message ?: "Bad request",
            fields = error?.validationErrors.orEmpty(),
        )

        HttpStatusCode.Unauthorized.value -> ApiResult.Unauthorized(error?.message ?: "Unauthorized")
        HttpStatusCode.Forbidden.value -> ApiResult.Forbidden(error?.message ?: "Forbidden")
        HttpStatusCode.NotFound.value -> ApiResult.NotFound(error?.message ?: "Not found")
        HttpStatusCode.Conflict.value -> ApiResult.ValidationError(
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

internal suspend fun mapThrowableToApiResult(throwable: Throwable): ApiResult<Nothing> {
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
            throwable.response.bodyAsText()
        )

        is ServerResponseException -> {
            val bodyText = throwable.response.bodyAsText()
            val error = parseApiError(bodyText)
            ApiResult.ServerError(error?.message ?: "Internal server error")
        }

        is ResponseException -> ApiResult.UnknownError("Request failed")
        else -> ApiResult.NetworkError("Unable to reach server. Please try again.")
    }
}
