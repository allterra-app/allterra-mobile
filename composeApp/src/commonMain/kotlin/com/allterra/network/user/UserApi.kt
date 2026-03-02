package com.allterra.network.user

import com.allterra.core.result.ApiResult

interface UserApi {
    suspend fun me(): ApiResult<UserMeDto>
}
