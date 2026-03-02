package com.allterra.network.media

import com.allterra.config.AppConfig

fun toAbsoluteMediaUrl(rawUrl: String): String {
    val trimmed = rawUrl.trim()
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
        return trimmed
    }

    val normalized = if (trimmed.startsWith("/")) trimmed else "/$trimmed"
    val apiBase = AppConfig.baseUrl.trim().trimEnd('/')
    val apiOrigin = apiBase.removeSuffix("/api/v1")

    return when {
        normalized.startsWith("/api/v1/") -> "$apiOrigin$normalized"
        normalized.startsWith("/files/") -> "$apiBase$normalized"
        else -> "$apiOrigin$normalized"
    }
}
