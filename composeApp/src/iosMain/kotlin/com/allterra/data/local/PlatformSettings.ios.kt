package com.allterra.data.local

import platform.Foundation.NSUserDefaults

actual object PlatformSettings {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getString(key: String): String? = defaults.stringForKey(key)

    actual fun putString(key: String, value: String) {
        defaults.setObject(value, key)
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (defaults.objectForKey(key) == null) defaultValue else defaults.boolForKey(key)
    }

    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
    }

    actual fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }
}
