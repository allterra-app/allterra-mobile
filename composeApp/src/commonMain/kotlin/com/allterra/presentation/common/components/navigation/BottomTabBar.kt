package com.allterra.presentation.common.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
                val tint = if (selected) tab.accentColor() else AllterraTheme.colors.muted2

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .allterraClickable { onTabSelected(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = tab.icon(),
                        contentDescription = tab.label(strings),
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = tab.label(strings),
                        style = AllterraTheme.typography.tab,
                        color = tint,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

private fun MainTab.icon(): androidx.compose.ui.graphics.vector.ImageVector {
    return when (this) {
        MainTab.HOME -> AllterraIcons.Home
        MainTab.FEED -> AllterraIcons.Feed
        MainTab.MAP -> AllterraIcons.Map
        MainTab.WALLET -> AllterraIcons.Wallet
        MainTab.TRIPS -> AllterraIcons.Route
    }
}

fun MainTab.label(strings: AppStrings): String {
    return when (this) {
        MainTab.HOME -> strings.tabHome
        MainTab.FEED -> strings.tabFeed
        MainTab.MAP -> strings.tabMap
        MainTab.WALLET -> strings.tabWallet
        MainTab.TRIPS -> strings.tabTrips
    }
}

@Composable
private fun MainTab.accentColor(): Color {
    return when (this) {
        MainTab.HOME -> AllterraTheme.colors.moss
        MainTab.FEED -> AllterraTheme.categorical.social.color
        MainTab.MAP -> AllterraTheme.categorical.route.color
        MainTab.WALLET -> AllterraTheme.categorical.wallet.color
        MainTab.TRIPS -> AllterraTheme.categorical.route.color
    }
}
