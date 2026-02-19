package com.allterra.data.local

import com.allterra.domain.model.AuthTokens

interface TokenStorage {
    suspend fun save(tokens: AuthTokens)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clear()
}
