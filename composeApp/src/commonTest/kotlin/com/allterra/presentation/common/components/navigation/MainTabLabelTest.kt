package com.allterra.presentation.common.components.navigation

import com.allterra.domain.model.AppLanguage
import com.allterra.presentation.localization.stringsFor
import kotlin.test.Test
import kotlin.test.assertEquals

class MainTabLabelTest {

    @Test
    fun labels_matchLocalizationForEnglish() {
        val strings = stringsFor(AppLanguage.EN)

        assertEquals(strings.tabRoutes, MainTab.ROUTES.label(strings))
        assertEquals(strings.tabProfile, MainTab.PROFILE.label(strings))
        assertEquals(strings.tabMap, MainTab.MAP.label(strings))
        assertEquals(strings.tabFeed, MainTab.FEED.label(strings))
        assertEquals(strings.tabSettings, MainTab.SETTINGS.label(strings))
    }

    @Test
    fun labels_matchLocalizationForRussian() {
        val strings = stringsFor(AppLanguage.RU)

        assertEquals("Маршруты", MainTab.ROUTES.label(strings))
        assertEquals("Профиль", MainTab.PROFILE.label(strings))
        assertEquals("Карта", MainTab.MAP.label(strings))
        assertEquals("Лента", MainTab.FEED.label(strings))
        assertEquals("Настройки", MainTab.SETTINGS.label(strings))
    }
}
