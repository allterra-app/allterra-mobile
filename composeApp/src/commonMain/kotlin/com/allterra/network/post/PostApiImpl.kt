package com.allterra.network.post

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

class PostApiImpl(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
) : PostApi {

    override suspend fun getFeed(page: Int, size: Int): ApiResult<FeedPageDto> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.get("${AppConfig.baseUrl}/feed") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                url {
                    parameters.append("page", page.toString())
                    parameters.append("size", size.toString())
                }
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<FeedPageDto>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun getAll(): ApiResult<List<PostDto>> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.get("${AppConfig.baseUrl}/posts") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<List<PostDto>>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun getByUser(userId: String): ApiResult<List<PostDto>> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.get("${AppConfig.baseUrl}/posts/users/$userId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<List<PostDto>>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun getSavedByUser(userId: String): ApiResult<List<PostDto>> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.get("${AppConfig.baseUrl}/posts/users/$userId/saved") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<List<PostDto>>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun getById(postId: String): ApiResult<PostDto> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.get("${AppConfig.baseUrl}/posts/$postId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<PostDto>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun createForUser(userId: String, request: PostCreateRequestDto): ApiResult<PostDto> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.post("${AppConfig.baseUrl}/posts/users/$userId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<PostDto>())
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun saveForUser(userId: String, postId: String): ApiResult<Unit> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.put("${AppConfig.baseUrl}/posts/users/$userId/saved/$postId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(Unit)
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun unsaveForUser(userId: String, postId: String): ApiResult<Unit> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.delete("${AppConfig.baseUrl}/posts/users/$userId/saved/$postId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(Unit)
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun deleteForUser(userId: String, postId: String): ApiResult<Unit> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.delete("${AppConfig.baseUrl}/posts/users/$userId/$postId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(Unit)
            } else {
                mapErrorResponse(response.status.value, response.bodyAsText())
            }
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun likePost(postId: String): ApiResult<Unit> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.put("${AppConfig.baseUrl}/posts/$postId/like") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(Unit)
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }

    override suspend fun unlikePost(postId: String): ApiResult<Unit> {
        val accessToken = tokenStorage.getAccessToken()
            ?: return ApiResult.Unauthorized("Missing access token")

        return try {
            val response = httpClient.delete("${AppConfig.baseUrl}/posts/$postId/like") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            if (response.status.isSuccess()) ApiResult.Success(Unit)
            else mapErrorResponse(response.status.value, response.bodyAsText())
        } catch (exception: Throwable) {
            mapThrowableToApiResult(exception)
        }
    }
}
