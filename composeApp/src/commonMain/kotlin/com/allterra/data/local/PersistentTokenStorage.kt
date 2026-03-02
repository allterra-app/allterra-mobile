package com.allterra.data.local

import com.allterra.domain.model.AuthTokens
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PersistentTokenStorage : TokenStorage {
    private val lock = Mutex()
    private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null
    private var initialized: Boolean = false

    override suspend fun save(tokens: AuthTokens) {
        lock.withLock {
            cachedAccessToken = tokens.accessToken
            cachedRefreshToken = tokens.refreshToken
            PlatformSettings.putString(KEY_ACCESS_TOKEN, tokens.accessToken)
            PlatformSettings.putString(KEY_REFRESH_TOKEN, tokens.refreshToken)
            initialized = true
        }
    }

    override suspend fun getAccessToken(): String? {
        return lock.withLock {
            ensureInitialized()
            cachedAccessToken
        }
    }

    override suspend fun getRefreshToken(): String? {
        return lock.withLock {
            ensureInitialized()
            cachedRefreshToken
        }
    }

    override suspend fun clear() {
        lock.withLock {
            cachedAccessToken = null
            cachedRefreshToken = null
            PlatformSettings.remove(KEY_ACCESS_TOKEN)
            PlatformSettings.remove(KEY_REFRESH_TOKEN)
            initialized = true
        }
    }

    private fun ensureInitialized() {
        if (initialized) return
        cachedAccessToken = PlatformSettings.getString(KEY_ACCESS_TOKEN)?.takeIf { it.isNotBlank() }
        cachedRefreshToken = PlatformSettings.getString(KEY_REFRESH_TOKEN)?.takeIf { it.isNotBlank() }
        initialized = true
    }

    private companion object {
        private const val KEY_ACCESS_TOKEN = "auth_access_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
    }
}
