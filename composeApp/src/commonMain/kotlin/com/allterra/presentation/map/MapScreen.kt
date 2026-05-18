package com.allterra.presentation.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme

private enum class MapLayer(val label: String) {
    Topo("Topo"),
    Satellite("Sat"),
    Dark("Dark"),
}

@Composable
fun MapScreen(
    onOpenRoutes: () -> Unit = {},
) {
    val strings = appStrings()
    val terra = AllterraTheme.categorical.route
    var layer by remember { mutableStateOf(MapLayer.Topo) }
    var showPois by remember { mutableStateOf(true) }
    var showRoutes by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bgSub),
    ) {
        // Map Placeholder
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                "MapLibre Engine Placeholder", 
                modifier = Modifier.align(Alignment.Center),
                color = AllterraTheme.colors.muted2
            )
        }

        // Top UI: Search and Filters
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AllterraTheme.colors.surface.copy(alpha = 0.92f))
                        .border(1.dp, AllterraTheme.colors.line2, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = AllterraTheme.colors.muted, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Find a place...", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                }
                AllterraIconButton(onClick = { showPois = !showPois }, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Outlined.FilterList, contentDescription = null, tint = terra.color)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AllterraChip(text = "POIs", categorical = if (showPois) terra else null)
                AllterraChip(text = strings.tabRoutes, categorical = if (showRoutes) terra else null, modifier = Modifier.allterraClickable { showRoutes = !showRoutes })
            }
        }

        // Right side controls
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 120.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AllterraIconButton(onClick = { layer = layer.next() }, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Outlined.Layers, contentDescription = null, tint = terra.color)
            }
            AllterraIconButton(onClick = {}, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Outlined.MyLocation, contentDescription = null, tint = terra.color)
            }
        }

        // Layer selection pills
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 220.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MapLayer.entries.forEach { item ->
                LayerPill(label = item.label, selected = item == layer, onClick = { layer = item })
            }
        }

        // Active Route Card
        ActiveRouteCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 100.dp),
            onOpenRoutes = onOpenRoutes,
        )
    }
}

@Composable
private fun LayerPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val terra = AllterraTheme.categorical.route
    Text(
        text = label,
        style = AllterraTheme.typography.tab,
        color = if (selected) Color.White else terra.color,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) terra.color else AllterraTheme.colors.surface.copy(alpha = 0.9f))
            .border(1.dp, terra.color.copy(alpha = 0.24f), RoundedCornerShape(999.dp))
            .allterraClickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
    )
}

@Composable
private fun ActiveRouteCard(modifier: Modifier, onOpenRoutes: () -> Unit) {
    val terra = AllterraTheme.categorical.route
    AllterraCard(
        modifier = modifier.fillMaxWidth(), 
        backgroundColor = AllterraTheme.colors.surface, 
        hasShadow = true
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "ROUTE", style = AllterraTheme.typography.caption, color = terra.color)
                    Text(text = "No route selected", style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
                }
                AllterraButton(
                    text = "Library",
                    isSmall = true,
                    variant = AllterraButtonVariant.Primary,
                    leadingIcon = { Icon(AllterraIcons.Route, null, modifier = Modifier.size(16.dp)) },
                    onClick = onOpenRoutes,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pick a route to see distance, progress and elevation.",
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted,
            )
        }
    }
}

private fun MapLayer.next(): MapLayer {
    val entries = MapLayer.entries
    return entries[(ordinal + 1) % entries.size]
}
