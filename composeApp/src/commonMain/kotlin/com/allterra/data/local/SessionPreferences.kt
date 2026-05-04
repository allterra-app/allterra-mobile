package com.allterra.data.local

import com.allterra.domain.model.AppLanguage

interface SessionPreferences {
    suspend fun isLoggedIn(): Boolean
    suspend fun setLoggedIn(value: Boolean)
    suspend fun getLanguage(): AppLanguage
    suspend fun setLanguage(language: AppLanguage)
    suspend fun isOnboardingCompleted(): Boolean
    suspend fun setOnboardingCompleted(value: Boolean)
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

    override suspend fun isOnboardingCompleted(): Boolean {
        return PlatformSettings.getBoolean(KEY_ONBOARDING_COMPLETED, defaultValue = false)
    }

    override suspend fun setOnboardingCompleted(value: Boolean) {
        PlatformSettings.putBoolean(KEY_ONBOARDING_COMPLETED, value)
    }

    private companion object {
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
