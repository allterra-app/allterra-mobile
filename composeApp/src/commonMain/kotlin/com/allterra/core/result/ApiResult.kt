package com.allterra.core.result

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class ValidationError(val message: String, val fields: Map<String, String>) : ApiResult<Nothing>
    data class Unauthorized(val message: String) : ApiResult<Nothing>
    data class Forbidden(val message: String) : ApiResult<Nothing>
    data class NotFound(val message: String) : ApiResult<Nothing>
    data class ServerError(val message: String) : ApiResult<Nothing>
    data class NetworkError(val message: String) : ApiResult<Nothing>
    data class UnknownError(val message: String) : ApiResult<Nothing>
}
