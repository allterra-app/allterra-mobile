package com.allterra.network.post

import com.allterra.core.result.ApiResult

interface PostPhotoApi {
    suspend fun create(request: PostPhotoCreateRequestDto): ApiResult<PostPhotoDto>
}
