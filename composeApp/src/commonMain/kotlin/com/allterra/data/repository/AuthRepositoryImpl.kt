package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.data.local.TokenStorage
import com.allterra.domain.model.AuthTokens
import com.allterra.domain.repository.AuthRepository
import com.allterra.network.auth.AuthApi

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun login(email: String, password: String): ApiResult<AuthTokens> {
        val result = authApi.login(email = email, password = password)
        if (result is ApiResult.Success) {
            tokenStorage.save(result.data)
        }
        return result
    }

    override suspend fun register(email: String, password: String): ApiResult<AuthTokens> {
        val result = authApi.register(email = email, password = password)
        if (result is ApiResult.Success) {
            tokenStorage.save(result.data)
        }
        return result
    }
}
