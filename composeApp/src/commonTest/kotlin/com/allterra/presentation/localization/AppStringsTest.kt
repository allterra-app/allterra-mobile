package com.allterra.presentation.localization

import com.allterra.domain.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppStringsTest {

    @Test
    fun stringsFor_returnsExpectedLanguageVariants() {
        assertEquals("Login", stringsFor(AppLanguage.EN).loginAction)
        assertEquals("Войти", stringsFor(AppLanguage.RU).loginAction)
        assertEquals("Logowanie", stringsFor(AppLanguage.PL).loginAction)
    }

    @Test
    fun allLanguages_haveNonBlankCoreLabels() {
        AppLanguage.entries.forEach { language ->
            val strings = stringsFor(language)
            assertTrue(strings.appName.isNotBlank())
            assertTrue(strings.feedTitle.isNotBlank())
            assertTrue(strings.routesTitle.isNotBlank())
            assertTrue(strings.poisTitle.isNotBlank())
        }
    }
}
