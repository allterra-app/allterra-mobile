package com.allterra.config

enum class AppEnvironment {
    LOCAL,
    STAGING,
    PRODUCTION;

    companion object {
        fun from(raw: String?): AppEnvironment {
            return when (raw?.trim()?.uppercase()) {
                "LOCAL" -> LOCAL
                "STAGING" -> STAGING
                "PROD", "PRODUCTION" -> PRODUCTION
                else -> LOCAL
            }
        }
    }
}

expect object PlatformConfig {
    val appEnvironment: String?
    val customBaseUrl: String?
    val localBaseUrl: String
    val version: String
}

object AppConfig {
    val environment: AppEnvironment = AppEnvironment.from(PlatformConfig.appEnvironment)
    
    val version: String = if (environment == AppEnvironment.LOCAL) "dev-ver" else PlatformConfig.version

    val baseUrl: String = PlatformConfig.customBaseUrl
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?: when (environment) {
            AppEnvironment.LOCAL -> PlatformConfig.localBaseUrl
            AppEnvironment.STAGING,
            AppEnvironment.PRODUCTION -> error(
                "APP_BASE_URL is required when APP_ENV is STAGING or PRODUCTION."
            )
        }
}
