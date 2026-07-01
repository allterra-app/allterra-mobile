package com.allterra.network.post

import com.allterra.core.result.ApiResult

interface PostApi {
    suspend fun getFeed(page: Int, size: Int): ApiResult<FeedPageDto>
    suspend fun getAll(): ApiResult<List<PostDto>>
    suspend fun getByUser(userId: String): ApiResult<List<PostDto>>
    suspend fun getSavedByUser(userId: String): ApiResult<List<PostDto>>
    suspend fun getById(postId: String): ApiResult<PostDto>
    suspend fun createForUser(userId: String, request: PostCreateRequestDto): ApiResult<PostDto>
    suspend fun saveForUser(userId: String, postId: String): ApiResult<Unit>
    suspend fun unsaveForUser(userId: String, postId: String): ApiResult<Unit>
    suspend fun deleteForUser(userId: String, postId: String): ApiResult<Unit>
    suspend fun likePost(postId: String): ApiResult<Unit>
    suspend fun unlikePost(postId: String): ApiResult<Unit>
}
