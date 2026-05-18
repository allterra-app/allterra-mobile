package com.allterra.presentation.dashboard

import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.mountain_color_no_bg
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun DashboardScreen(
    userName: String,
    onOpenProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenWallet: () -> Unit,
    onNewPost: () -> Unit,
    onOpenTripCreate: () -> Unit,
    onOpenGear: () -> Unit,
    onOpenMap: () -> Unit,
    onSeeAllTrips: () -> Unit,
) {
    val strings = appStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
    ) {
        // Appbar Replacement / Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AllterraTheme.spacing.s6),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.allterraClickable { onOpenProfile() }
            ) {
                Image(
                    painter = painterResource(Res.drawable.mountain_color_no_bg),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(AllterraTheme.spacing.s3))
                Column {
                    Text(
                        text = "Good morning,", // TODO: Dynamic by time
                        style = AllterraTheme.typography.small,
                        color = AllterraTheme.colors.muted
                    )
                    Text(
                        text = userName,
                        style = AllterraTheme.typography.title,
                        color = AllterraTheme.colors.ink
                    )
                }
            }
            
            AllterraIconButton(onClick = onOpenNotifications) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = AllterraTheme.colors.ink,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Hero: Upcoming Trip
        AllterraCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = AllterraTheme.categorical.route.color,
            hasShadow = true
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "2 days until",
                            style = AllterraTheme.typography.small,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "High Tatra Peaks", // TODO: Real data
                            style = AllterraTheme.typography.displayM,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("12°C", style = AllterraTheme.typography.smallStrong, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        // Quick Actions 2x2 Grid
        Text(
            text = strings.dashboardQuickActions,
            style = AllterraTheme.typography.smallStrong,
            color = AllterraTheme.colors.muted,
            modifier = Modifier.padding(bottom = AllterraTheme.spacing.s3)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
                QuickActionItem(
                    icon = AllterraIcons.Wallet,
                    label = "Wallet+",
                    color = AllterraTheme.categorical.wallet.color,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenWallet
                )
                QuickActionItem(
                    icon = AllterraIcons.Feed,
                    label = "Post",
                    color = AllterraTheme.categorical.social.color,
                    modifier = Modifier.weight(1f),
                    onClick = onNewPost
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
                QuickActionItem(
                    icon = AllterraIcons.Route,
                    label = "Trip+",
                    color = AllterraTheme.categorical.route.color,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenTripCreate
                )
                QuickActionItem(
                    icon = AllterraIcons.Scales,
                    label = "Gear",
                    color = AllterraTheme.categorical.gear.color,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenGear
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
                QuickActionItem(
                    icon = AllterraIcons.Map,
                    label = "Map",
                    color = AllterraTheme.categorical.route.color,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenMap
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s8))

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AllterraTheme.radius.md))
                .background(AllterraTheme.colors.surface2)
                .padding(AllterraTheme.spacing.s4),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem("142", "km", AllterraTheme.categorical.route.color)
            StatItem("2.4k", "elevation", AllterraTheme.categorical.route.color)
            StatItem("12", "trips", AllterraTheme.categorical.wallet.color)
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s8))

        // Recent Trips Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.dashboardRecentTrips,
                style = AllterraTheme.typography.smallStrong,
                color = AllterraTheme.colors.muted
            )
            Text(
                text = strings.dashboardSeeAll,
                style = AllterraTheme.typography.smallStrong,
                color = AllterraTheme.categorical.route.color,
                modifier = Modifier.allterraClickable { onSeeAllTrips() }
            )
        }
        
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s3))
        
        // Example Recent Trip Row
        RecentTripRow("Valley of Five Lakes", "Jan 12, 2026")

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(AllterraTheme.radius.md))
            .clip(RoundedCornerShape(AllterraTheme.radius.md))
            .background(AllterraTheme.colors.surface)
            .allterraClickable { onClick() }
            .padding(AllterraTheme.spacing.s4),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(AllterraTheme.spacing.s1))
            Text(label, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.ink)
        }
    }
}

@Composable
private fun StatItem(value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = AllterraTheme.typography.displayM, color = color)
        Text(unit, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
    }
}

@Composable
private fun RecentTripRow(title: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AllterraTheme.spacing.s2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(AllterraTheme.radius.sm))
                .background(AllterraTheme.colors.surface2)
        ) // TODO: Mini-map placeholder
        
        Spacer(modifier = Modifier.width(AllterraTheme.spacing.s3))
        
        Column {
            Text(title, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
            Text(date, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
        }
    }
}
