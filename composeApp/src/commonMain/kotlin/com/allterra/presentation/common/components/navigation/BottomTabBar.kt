package com.allterra.presentation.common.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.allterraClickable
import com.allterra.presentation.localization.AppStrings
import com.allterra.presentation.theme.AllterraTheme

enum class MainTab {
    HOME,
    FEED,
    MAP,
    WALLET,
    TRIPS,
}

@Composable
fun BottomTabBar(
    modifier: Modifier = Modifier,
    selectedTab: MainTab,
    strings: AppStrings,
    onTabSelected: (MainTab) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(horizontal = AllterraTheme.spacing.tabbarMargin)
            .padding(bottom = 18.dp) // marginBottom from tokens
            .windowInsetsPadding(WindowInsets.navigationBars)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(AllterraTheme.radius.tabbar),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(AllterraTheme.radius.tabbar))
            .background(AllterraTheme.colors.surface)
            .height(64.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainTab.entries.forEach { tab ->
                val selected = tab == selectedTab
                val tint = if (selected) AllterraTheme.colors.moss else AllterraTheme.colors.muted2

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .allterraClickable { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon(),
                        contentDescription = tab.label(strings),
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

private fun MainTab.icon(): androidx.compose.ui.graphics.vector.ImageVector {
    return when (this) {
        MainTab.HOME -> Icons.Outlined.GridView
        MainTab.FEED -> Icons.Outlined.DynamicFeed
        MainTab.MAP -> Icons.Outlined.Map
        MainTab.WALLET -> Icons.Outlined.AccountBalanceWallet
        MainTab.TRIPS -> Icons.Outlined.Hiking
    }
}

fun MainTab.label(strings: AppStrings): String {
    // Note: Temporary labels using existing strings or defaults
    return when (this) {
        MainTab.HOME -> "Home"
        MainTab.FEED -> strings.tabFeed
        MainTab.MAP -> strings.tabMap
        MainTab.WALLET -> "Wallet"
        MainTab.TRIPS -> strings.tabRoutes
    }
}
