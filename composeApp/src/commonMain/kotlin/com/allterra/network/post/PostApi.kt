package com.allterra.network.post

import com.allterra.core.result.ApiResult

interface PostApi {
    suspend fun getAll(): ApiResult<List<PostDto>>
    suspend fun getByUser(userId: String): ApiResult<List<PostDto>>
    suspend fun getById(postId: String): ApiResult<PostDto>
    suspend fun createForUser(userId: String, request: PostCreateRequestDto): ApiResult<PostDto>
    suspend fun deleteForUser(userId: String, postId: String): ApiResult<Unit>
}
