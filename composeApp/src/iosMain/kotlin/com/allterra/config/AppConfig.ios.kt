package com.allterra.config

import platform.Foundation.NSProcessInfo
import platform.Foundation.NSBundle

actual object PlatformConfig {
    actual val appEnvironment: String?
        get() = NSProcessInfo.processInfo.environment["APP_ENV"] as? String

    actual val customBaseUrl: String?
        get() = NSProcessInfo.processInfo.environment["APP_BASE_URL"] as? String

    actual val localBaseUrl: String = "http://localhost:8080/api/v1"
    
    actual val version: String
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: "unknown"
}
