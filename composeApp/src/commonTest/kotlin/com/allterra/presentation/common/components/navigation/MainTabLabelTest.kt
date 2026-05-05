package com.allterra.presentation.common.components.navigation

import com.allterra.domain.model.AppLanguage
import com.allterra.presentation.localization.stringsFor
import kotlin.test.Test
import kotlin.test.assertEquals

class MainTabLabelTest {

    @Test
    fun labels_matchLocalizationForEnglish() {
        val strings = stringsFor(AppLanguage.EN)

        assertEquals("Home", MainTab.HOME.label(strings))
        assertEquals(strings.tabFeed, MainTab.FEED.label(strings))
        assertEquals(strings.tabMap, MainTab.MAP.label(strings))
        assertEquals("Wallet", MainTab.WALLET.label(strings))
        assertEquals("Trips", MainTab.TRIPS.label(strings))
    }

    @Test
    fun labels_matchLocalizationForRussian() {
        val strings = stringsFor(AppLanguage.RU)

        assertEquals("Главная", MainTab.HOME.label(strings))
        assertEquals("Лента", MainTab.FEED.label(strings))
        assertEquals("Карта", MainTab.MAP.label(strings))
        assertEquals("Кошелек", MainTab.WALLET.label(strings))
        assertEquals("Выходы", MainTab.TRIPS.label(strings))
    }
}
