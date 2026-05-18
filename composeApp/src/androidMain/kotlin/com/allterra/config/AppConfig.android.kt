package com.allterra.config

import com.allterra.BuildConfig

actual object PlatformConfig {
    actual val appEnvironment: String? = BuildConfig.APP_ENV
    actual val customBaseUrl: String? = BuildConfig.APP_BASE_URL
    actual val localBaseUrl: String = "http://10.0.2.2:8080/api/v1"
    actual val version: String = BuildConfig.VERSION_NAME
}
