package com.allterra.presentation.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.allterra.domain.model.AppLanguage

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.EN }
val LocalAppStrings = staticCompositionLocalOf { stringsFor(AppLanguage.EN) }

@Composable
@ReadOnlyComposable
fun appStrings(): AppStrings = LocalAppStrings.current

fun languageDisplayName(language: AppLanguage, strings: AppStrings): String {
    return when (language) {
        AppLanguage.EN -> "English"
        AppLanguage.RU -> "Русский"
        AppLanguage.PL -> "Polski"
        AppLanguage.DE -> "Deutsch"
    }
}
