package com.allterra.data.local

import android.content.SharedPreferences

actual object PlatformSettings {
    private const val PREFS_NAME = "allterra_app_prefs"

    private val prefs: SharedPreferences
        get() = AndroidAppContextHolder.appContext.getSharedPreferences(PREFS_NAME, 0)

    actual fun getString(key: String): String? = prefs.getString(key, null)

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
}
