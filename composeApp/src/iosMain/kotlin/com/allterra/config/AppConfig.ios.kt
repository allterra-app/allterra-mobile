package com.allterra.config

import platform.Foundation.NSProcessInfo

actual object PlatformConfig {
    actual val appEnvironment: String?
        get() = NSProcessInfo.processInfo.environment["APP_ENV"] as? String

    actual val customBaseUrl: String?
        get() = NSProcessInfo.processInfo.environment["APP_BASE_URL"] as? String

    actual val localBaseUrl: String = "http://localhost:8080/api/v1"
}
