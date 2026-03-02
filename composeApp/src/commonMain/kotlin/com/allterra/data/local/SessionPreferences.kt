package com.allterra.data.local

import com.allterra.domain.model.AppLanguage

interface SessionPreferences {
    suspend fun isLoggedIn(): Boolean
    suspend fun setLoggedIn(value: Boolean)
    suspend fun getLanguage(): AppLanguage
    suspend fun setLanguage(language: AppLanguage)
}

class SessionPreferencesImpl : SessionPreferences {

    override suspend fun isLoggedIn(): Boolean {
        return PlatformSettings.getBoolean(KEY_LOGGED_IN, defaultValue = false)
    }

    override suspend fun setLoggedIn(value: Boolean) {
        PlatformSettings.putBoolean(KEY_LOGGED_IN, value)
    }

    override suspend fun getLanguage(): AppLanguage {
        return AppLanguage.fromCode(PlatformSettings.getString(KEY_LANGUAGE))
    }

    override suspend fun setLanguage(language: AppLanguage) {
        PlatformSettings.putString(KEY_LANGUAGE, language.code)
    }

    private companion object {
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_LANGUAGE = "language"
    }
}
