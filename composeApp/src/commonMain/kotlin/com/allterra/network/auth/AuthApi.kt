package com.allterra.network.auth

import com.allterra.core.result.ApiResult
import com.allterra.domain.model.AuthTokens

interface AuthApi {
    suspend fun login(email: String, password: String): ApiResult<AuthTokens>
    suspend fun register(email: String, password: String): ApiResult<AuthTokens>
    suspend fun refresh(refreshToken: String): ApiResult<AuthTokens>
    suspend fun logout(refreshToken: String): ApiResult<Unit>
}
