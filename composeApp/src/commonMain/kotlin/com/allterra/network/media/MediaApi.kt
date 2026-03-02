package com.allterra.network.media

import com.allterra.core.result.ApiResult

interface MediaApi {
    suspend fun upload(fileName: String, contentType: String, bytes: ByteArray): ApiResult<MediaUploadResponseDto>
}
