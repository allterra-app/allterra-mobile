package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.model.AuthTokens

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResult<AuthTokens>
    suspend fun register(email: String, password: String): ApiResult<AuthTokens>
}
