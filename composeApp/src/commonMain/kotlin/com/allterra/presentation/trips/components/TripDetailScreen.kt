package com.allterra.presentation.trips.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.presentation.trips.TripUiModel
import com.allterra.presentation.wallet.WalletItem

private enum class TripTab {
    Overview, Route, Gear, Docs, Journal
}

@Composable
fun TripDetailScreen(
    trip: TripUiModel,
    route: RouteUiModel?,
    docs: List<WalletItem>,
    onOpenPacking: (String) -> Unit,
    onBack: () -> Unit
) {
    val terra = AllterraTheme.categorical.route
    var selectedTab by remember(trip.id) { mutableStateOf(TripTab.Overview) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 112.dp),
    ) {
        // Hero Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(AllterraTheme.colors.bgSub)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AllterraIconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = terra.color)
                }
                AllterraIconButton(onClick = {}) {
                    Icon(Icons.Outlined.Share, null, tint = terra.color)
                }
            }
            
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(AllterraTheme.spacing.s4)
            ) {
                AllterraChip(text = trip.status.name, categorical = terra)
                Spacer(modifier = Modifier.height(8.dp))
                Text(trip.title, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent,
            contentColor = terra.color,
            indicator = {},
            edgePadding = AllterraTheme.spacing.screenPaddingX
        ) {
            TripTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                Tab(
                    selected = selected,
                    onClick = {
                        println("DEBUG: Tab clicked: ${tab.name}")
                        if (tab == TripTab.Gear) {
                            onOpenPacking(trip.id)
                        } else {
                            selectedTab = tab
                        }
                    },
                    text = {
                        Text(
                            text = tab.name,
                            style = if (selected) AllterraTheme.typography.bodyStrong else AllterraTheme.typography.body,
                            color = if (selected) terra.color else AllterraTheme.colors.muted
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        Column(modifier = Modifier.padding(horizontal = AllterraTheme.spacing.screenPaddingX)) {
            when (selectedTab) {
                TripTab.Overview -> TripOverview(trip)
                else -> Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                    Text("${selectedTab.name} implementation in progress", color = AllterraTheme.colors.muted)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AllterraButton(
                text = "Start Trip",
                variant = AllterraButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Navigation, null, modifier = Modifier.size(20.dp)) },
                onClick = {}
            )
        }
    }
}

@Composable
private fun TripOverview(trip: TripUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AllterraCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
                OverviewStat("Ready", "${(trip.readiness.docsReady * 100 / trip.readiness.docsTotal.coerceAtLeast(1))}%")
                OverviewStat("Km", trip.distanceKm.toString())
                OverviewStat("Group", trip.members.size.toString())
            }
        }
        
        Text(
            text = "Description Placeholder. Link documents, gear and routes to see them here.",
            style = AllterraTheme.typography.body,
            color = AllterraTheme.colors.muted
        )
    }
}

@Composable
private fun OverviewStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
        Text(value, style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
    }
}
