package com.allterra.presentation.routes.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme
import kotlin.math.roundToInt

private enum class DetailTab {
    Overview,
    Elevation,
    Waypoints,
}

@Composable
fun RouteDetailScreen(
    route: RouteUiModel,
    onBack: () -> Unit,
) {
    val strings = appStrings()
    val terra = AllterraTheme.categorical.route
    var tab by remember(route.id) { mutableStateOf(DetailTab.Overview) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 112.dp),
    ) {
        // Hero Image / Map Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(AllterraTheme.colors.bgSub)
        ) {
            // Hero actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AllterraIconButton(onClick = onBack, modifier = Modifier.size(42.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = terra.color)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AllterraIconButton(onClick = {}, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = terra.color)
                    }
                    AllterraIconButton(onClick = {}, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = terra.color)
                    }
                }
            }
            
            // Bottom badges on hero
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AllterraChip(text = "Difficult", categorical = terra)
                AllterraChip(text = route.date)
            }
        }

        Column(modifier = Modifier.padding(horizontal = AllterraTheme.spacing.screenPaddingX, vertical = 16.dp)) {
            Text(text = "Tatry, High Tatras", style = AllterraTheme.typography.caption, color = terra.color)
            Text(
                text = route.title,
                style = AllterraTheme.typography.displayM,
                color = AllterraTheme.colors.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Metrics
            Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
                DetailStat(strings.routeDistanceLabel, route.distanceKm?.toString() ?: "0.0", "km", Modifier.weight(1f))
                DetailStat("Elevation", "1,240", "m", Modifier.weight(1f))
                DetailStat(strings.routeDurationLabel, "5.5", "h", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Segmented Control Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AllterraTheme.radius.md))
                    .background(AllterraTheme.colors.surface2)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                DetailTab.entries.forEach { item ->
                    val selected = tab == item
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(AllterraTheme.radius.sm))
                            .background(if (selected) AllterraTheme.colors.surface else Color.Transparent)
                            .allterraClickable { tab = item },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.name,
                            style = if (selected) AllterraTheme.typography.bodyStrong else AllterraTheme.typography.body,
                            color = if (selected) AllterraTheme.colors.ink else AllterraTheme.colors.muted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Content
            when (tab) {
                DetailTab.Overview -> RouteOverview(route)
                DetailTab.Elevation -> ElevationProfileCard()
                DetailTab.Waypoints -> WaypointsList()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Actions
            AllterraButton(
                text = "Navigate Route",
                variant = AllterraButtonVariant.Primary, // Will use terra because theme is Route
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Navigation, null, modifier = Modifier.size(20.dp)) },
                onClick = {}
            )
        }
    }
}

@Composable
private fun DetailStat(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    AllterraCard(modifier = modifier, hasShadow = false, backgroundColor = AllterraTheme.colors.surface2) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
            Text(text = value, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
            Text(text = unit, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
        }
    }
}

@Composable
private fun RouteOverview(route: RouteUiModel) {
    Column {
        Text(
            text = route.description.ifBlank { "No description provided for this route." },
            style = AllterraTheme.typography.body,
            color = AllterraTheme.colors.ink2
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AllterraChip("#trekking")
            AllterraChip("#poland")
            AllterraChip("#high-peaks")
        }
    }
}

@Composable
private fun ElevationProfileCard() {
    val terra = AllterraTheme.categorical.route
    AllterraCard(hasShadow = false, backgroundColor = AllterraTheme.colors.surface2) {
        Column {
            Text(text = "Elevation Profile", style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.ink)
            Spacer(modifier = Modifier.height(12.dp))
            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                val points = listOf(1000f, 1100f, 1050f, 1300f, 1500f, 1400f, 1800f, 2200f, 2000f, 1800f)
                val max = points.max()
                val min = points.min()
                val range = max - min
                val widthStep = size.width / (points.size - 1)
                
                val path = Path()
                points.forEachIndexed { i, p ->
                    val x = i * widthStep
                    val y = size.height - ((p - min) / range * size.height)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, terra.color, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
            }
        }
    }
}

@Composable
private fun WaypointsList() {
    val terra = AllterraTheme.categorical.route
    Column(verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
        repeat(3) { i ->
            AllterraCard(hasShadow = false, backgroundColor = AllterraTheme.colors.surface2) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(terra.soft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${i + 1}", style = AllterraTheme.typography.mono, color = terra.color)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Waypoint Name ${i+1}", style = AllterraTheme.typography.bodyStrong)
                        Text("4.2 km · 1,540 m", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                    }
                }
            }
        }
    }
}
