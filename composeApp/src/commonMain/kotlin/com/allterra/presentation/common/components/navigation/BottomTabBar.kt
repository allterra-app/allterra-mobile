package com.allterra.presentation.common.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.localization.AppStrings

enum class MainTab {
    ROUTES,
    PROFILE,
    FEED,
    MAP,
    SETTINGS,
}

@Composable
fun BottomTabBar(
    modifier: Modifier = Modifier,
    selectedTab: MainTab,
    strings: AppStrings,
    onTabSelected: (MainTab) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xBFE0EAF2))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            val tint = if (selected) Color(0xFF0B7A74) else Color(0xFFB27A91)

            androidx.compose.material3.IconButton(onClick = { onTabSelected(tab) }) {
                Icon(
                    imageVector = tab.icon(),
                    contentDescription = tab.label(strings),
                    tint = tint,
                )
            }
        }
    }
}

private fun MainTab.icon(): androidx.compose.ui.graphics.vector.ImageVector {
    return when (this) {
        MainTab.ROUTES -> Icons.Outlined.Place
        MainTab.PROFILE -> Icons.Outlined.Person
        MainTab.MAP -> Icons.Outlined.Map
        MainTab.FEED -> Icons.Outlined.Home
        MainTab.SETTINGS -> Icons.Outlined.Settings
    }
}

fun MainTab.label(strings: AppStrings): String {
    return when (this) {
        MainTab.ROUTES -> strings.tabRoutes
        MainTab.PROFILE -> strings.tabProfile
        MainTab.MAP -> strings.tabMap
        MainTab.FEED -> strings.tabFeed
        MainTab.SETTINGS -> strings.tabSettings
    }
}
