package com.allterra.network.notification

import com.allterra.core.result.ApiResult

interface NotificationApi {
    suspend fun getMine(): ApiResult<List<NotificationDto>>
    suspend fun markRead(id: String): ApiResult<NotificationDto>
}
