package com.allterra.domain.model

enum class AppLanguage(val code: String) {
    EN("en"),
    RU("ru"),
    PL("pl");

    companion object {
        fun fromCode(raw: String?): AppLanguage {
            return entries.firstOrNull { it.code == raw?.lowercase() } ?: EN
        }
    }
}
