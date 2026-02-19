package com.allterra.data.local

import com.allterra.domain.model.AuthTokens
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryTokenStorage : TokenStorage {
    private val lock = Mutex()
    private var accessToken: String? = null
    private var refreshToken: String? = null

    override suspend fun save(tokens: AuthTokens) {
        lock.withLock {
            accessToken = tokens.accessToken
            refreshToken = tokens.refreshToken
        }
    }

    override suspend fun getAccessToken(): String? = lock.withLock { accessToken }

    override suspend fun getRefreshToken(): String? = lock.withLock { refreshToken }

    override suspend fun clear() {
        lock.withLock {
            accessToken = null
            refreshToken = null
        }
    }
}
