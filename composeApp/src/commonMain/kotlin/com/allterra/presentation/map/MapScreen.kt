package com.allterra.presentation.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraButtonVariant
import com.allterra.presentation.common.components.redesign.AllterraCard
import com.allterra.presentation.common.components.redesign.AllterraChip
import com.allterra.presentation.common.components.redesign.AllterraIconButton
import com.allterra.presentation.common.components.redesign.allterraClickable
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
    val mapBackground = when (layer) {
        MapLayer.Topo -> AllterraTheme.colors.bgSub
        MapLayer.Satellite -> Color(0xFF667060)
        MapLayer.Dark -> Color(0xFF151713)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mapBackground),
    ) {
        TopoMapCanvas(
            modifier = Modifier.fillMaxSize(),
            layer = layer,
            showPois = showPois,
            showRoutes = showRoutes,
        )

        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 10.dp),
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
                AllterraChip(text = "Offline ready", categorical = terra)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 108.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AllterraIconButton(onClick = { layer = layer.next() }, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Outlined.Layers, contentDescription = null, tint = terra.color)
            }
            AllterraIconButton(onClick = {}, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Outlined.MyLocation, contentDescription = null, tint = terra.color)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 210.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MapLayer.entries.forEach { item ->
                LayerPill(label = item.label, selected = item == layer, onClick = { layer = item })
            }
        }

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
    AllterraCard(modifier = modifier.fillMaxWidth(), backgroundColor = AllterraTheme.colors.surface, borderColor = AllterraTheme.colors.line2) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "ROUTES", style = AllterraTheme.typography.caption, color = terra.color)
                    Text(text = "No active route", style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
                }
                AllterraButton(
                    text = "Open",
                    isSmall = true,
                    variant = AllterraButtonVariant.Terra,
                    leadingIcon = { Icon(Icons.Outlined.Route, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp)) },
                    onClick = onOpenRoutes,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Saved routes will appear here when they are loaded from the route library.",
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted,
            )
        }
    }
}

@Composable
private fun TopoMapCanvas(
    modifier: Modifier,
    layer: MapLayer,
    showPois: Boolean,
    showRoutes: Boolean,
) {
    val terra = AllterraTheme.categorical.route
    val sky = AllterraTheme.categorical.social
    val ink = AllterraTheme.colors.ink
    val contour = when (layer) {
        MapLayer.Dark -> Color.White.copy(alpha = 0.12f)
        MapLayer.Satellite -> Color.White.copy(alpha = 0.16f)
        MapLayer.Topo -> AllterraTheme.colors.muted.copy(alpha = 0.22f)
    }

    Canvas(modifier = modifier) {
        repeat(18) { index ->
            val radius = 70f + index * 24f
            drawOval(
                color = contour.copy(alpha = if (index % 4 == 0) 0.34f else contour.alpha),
                topLeft = Offset(size.width * 0.18f - radius * 0.2f, size.height * 0.42f - radius * 0.62f),
                size = androidx.compose.ui.geometry.Size(radius * 1.8f, radius * 1.12f),
                style = Stroke(width = if (index % 4 == 0) 2.1f else 1.1f),
            )
        }
        repeat(14) { index ->
            val radius = 50f + index * 22f
            drawOval(
                color = contour,
                topLeft = Offset(size.width * 0.72f - radius * 0.7f, size.height * 0.24f - radius),
                size = androidx.compose.ui.geometry.Size(radius * 1.35f, radius * 1.9f),
                style = Stroke(width = 1.1f),
            )
        }

        val river = Path().apply {
            moveTo(0f, size.height * 0.72f)
            quadraticTo(size.width * 0.28f, size.height * 0.68f, size.width * 0.45f, size.height * 0.73f)
            quadraticTo(size.width * 0.7f, size.height * 0.79f, size.width, size.height * 0.75f)
        }
        drawPath(river, color = sky.color.copy(alpha = 0.55f), style = Stroke(width = 3f, cap = StrokeCap.Round))

        if (showRoutes) {
            val trail = Path().apply {
                moveTo(size.width * 0.15f, size.height * 0.82f)
                quadraticTo(size.width * 0.25f, size.height * 0.7f, size.width * 0.36f, size.height * 0.57f)
                quadraticTo(size.width * 0.47f, size.height * 0.45f, size.width * 0.58f, size.height * 0.38f)
                quadraticTo(size.width * 0.68f, size.height * 0.32f, size.width * 0.78f, size.height * 0.3f)
            }
            drawPath(trail, color = terra.color.copy(alpha = 0.46f), style = Stroke(width = 7f, cap = StrokeCap.Round))
            drawPath(trail, color = terra.color, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
        }

        if (showPois) {
            listOf(
                Offset(size.width * 0.15f, size.height * 0.82f),
                Offset(size.width * 0.36f, size.height * 0.57f),
                Offset(size.width * 0.58f, size.height * 0.38f),
                Offset(size.width * 0.78f, size.height * 0.3f),
            ).forEachIndexed { index, point ->
                drawCircle(color = Color.White, radius = 12f, center = point)
                drawCircle(color = terra.color, radius = if (index == 0) 7f else 5f, center = point)
            }
        }

        drawCircle(color = terra.color.copy(alpha = 0.18f), radius = 24f, center = Offset(size.width * 0.25f, size.height * 0.7f))
        drawCircle(color = terra.color, radius = 7f, center = Offset(size.width * 0.25f, size.height * 0.7f))
    }
}

private fun MapLayer.next(): MapLayer {
    return when (this) {
        MapLayer.Topo -> MapLayer.Satellite
        MapLayer.Satellite -> MapLayer.Dark
        MapLayer.Dark -> MapLayer.Topo
    }
}
