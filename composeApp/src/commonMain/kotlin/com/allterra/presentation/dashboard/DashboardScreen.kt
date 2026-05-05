package com.allterra.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun DashboardScreen(
    userName: String,
    onLogout: () -> Unit,
    onOpenPacking: () -> Unit,
    onNewPost: () -> Unit,
    onAddDoc: () -> Unit
) {
    val strings = appStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bgSub)
            .verticalScroll(rememberScrollState())
            .padding(AllterraTheme.spacing.screenPaddingX)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${strings.dashboardGreetingPrefix}, $userName",
                    style = AllterraTheme.typography.displayM,
                    color = AllterraTheme.colors.ink
                )
                Text(
                    text = strings.dashboardGreetingBody,
                    style = AllterraTheme.typography.body,
                    color = AllterraTheme.colors.muted
                )
            }
            AllterraAvatar(initials = userName.take(1), size = AllterraAvatarSize.Medium)
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.sectionGap))

        // Upcoming Trip
        Text(
            text = strings.dashboardUpcomingTrip,
            style = AllterraTheme.typography.caption,
            color = AllterraTheme.colors.muted
        )
        Spacer(modifier = Modifier.height(8.dp))
        AllterraCard(
            backgroundColor = AllterraTheme.categorical.route.soft,
            borderColor = AllterraTheme.categorical.route.color.copy(alpha = 0.1f)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AllterraChip("In 3 days", categorical = AllterraTheme.categorical.route)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.Cloud, contentDescription = null, tint = AllterraTheme.categorical.route.color)
                    Text("12°C", style = AllterraTheme.typography.smallStrong, modifier = Modifier.padding(start = 4.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Tatra Mountains High Trail", style = AllterraTheme.typography.title)
                Text("Zakopane, Poland", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(strings.dashboardPackingProgress, style = AllterraTheme.typography.caption)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AllterraProgressBar(progress = 0.65f, modifier = Modifier.weight(1f))
                    Text("65%", style = AllterraTheme.typography.tab, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.sectionGap))

        // Quick Actions
        Text(
            text = strings.dashboardQuickActions,
            style = AllterraTheme.typography.caption,
            color = AllterraTheme.colors.muted
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionItem(
                icon = Icons.Outlined.Inventory,
                label = strings.dashboardPackingAction,
                modifier = Modifier.weight(1f),
                onClick = onOpenPacking
            )
            QuickActionItem(
                icon = Icons.Outlined.PostAdd,
                label = strings.dashboardNewPostAction,
                modifier = Modifier.weight(1f),
                onClick = onNewPost
            )
            QuickActionItem(
                icon = Icons.Outlined.Add,
                label = strings.dashboardAddDocAction,
                modifier = Modifier.weight(1f),
                onClick = onAddDoc
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.sectionGap))

        // Recent Trips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.dashboardRecentTrips,
                style = AllterraTheme.typography.caption,
                color = AllterraTheme.colors.muted
            )
            Text(strings.dashboardSeeAll, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.moss)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 18.dp)
        ) {
            items(listOf("Alps 2025", "Forest Hike", "River Kayaking")) { trip ->
                AllterraCard(modifier = Modifier.width(140.dp)) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(AllterraTheme.colors.bgSub))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(trip, style = AllterraTheme.typography.smallStrong)
                        Text("Completed", style = AllterraTheme.typography.tab, color = AllterraTheme.colors.good)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        
        AllterraButton(
            text = strings.logoutAction,
            variant = AllterraButtonVariant.Ghost,
            modifier = Modifier.fillMaxWidth(),
            onClick = onLogout
        )
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AllterraCard(
        modifier = modifier.allterraClickable { onClick() },
        hasShadow = false,
        backgroundColor = AllterraTheme.colors.surface2
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = AllterraTheme.colors.moss)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.ink)
        }
    }
}
