package com.allterra.presentation.routes

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.domain.repository.RouteCreateProgressStage
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraButtonVariant
import com.allterra.presentation.common.components.redesign.AllterraCard
import com.allterra.presentation.common.components.redesign.AllterraChip
import com.allterra.presentation.common.components.redesign.AllterraIconButton
import com.allterra.presentation.common.components.redesign.AllterraInput
import com.allterra.presentation.common.components.redesign.AllterraSheet
import com.allterra.presentation.common.components.redesign.allterraClickable
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme
import kotlin.math.roundToInt

private enum class RoutesTab {
    Mine,
    Saved,
}

private enum class DetailTab {
    Overview,
    Elevation,
    Waypoints,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(
    viewModel: RoutesViewModel,
    onBack: () -> Unit,
) {
    val strings = appStrings()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gpxFilePicker = rememberGpxFilePicker(
        onFileSelected = viewModel::onCreateGpxImported,
        onFileReadError = { viewModel.onCreateGpxImportFailed(strings.routeImportFailed) },
    )
    var tab by remember { mutableStateOf(RoutesTab.Mine) }
    var selectedRoute by remember { mutableStateOf<RouteUiModel?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(AllterraTheme.categorical.route.soft)) {
        selectedRoute?.let { route ->
            RouteDetailScreen(
                route = route,
                onBack = { selectedRoute = null },
            )
        } ?: RoutesListContent(
            state = state,
            selectedTab = tab,
            onTabSelected = { tab = it },
            onBack = onBack,
            onOpenImport = viewModel::openCreateRoute,
            onRouteClick = { selectedRoute = it },
        )

        if (state.isCreateOpen) {
            AllterraSheet(onDismiss = viewModel::closeCreateRoute) {
                RouteImportSheet(
                    state = state,
                    onTitleChanged = viewModel::onCreateTitleChanged,
                    onDescriptionChanged = viewModel::onCreateDescriptionChanged,
                    onImportGpx = gpxFilePicker::launch,
                    onSave = { viewModel.saveCreatedRoute(strings.routeCreationValidation) },
                )
            }
        }
    }
}

@Composable
private fun RoutesListContent(
    state: RoutesUiState,
    selectedTab: RoutesTab,
    onTabSelected: (RoutesTab) -> Unit,
    onBack: () -> Unit,
    onOpenImport: () -> Unit,
    onRouteClick: (RouteUiModel) -> Unit,
) {
    val strings = appStrings()
    val terra = AllterraTheme.categorical.route
    val routes = remember(state.items, selectedTab) {
        when (selectedTab) {
            RoutesTab.Mine -> state.items
            RoutesTab.Saved -> state.items.take(2)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = AllterraTheme.spacing.screenPaddingX,
            end = AllterraTheme.spacing.screenPaddingX,
            top = 10.dp,
            bottom = 112.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = terra.color)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = strings.routesTitle, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
                    Text(
                        text = "${state.items.size} routes - ${state.items.sumOf { it.distanceKm ?: 0.0 }.roundToOne()} km",
                        style = AllterraTheme.typography.small,
                        color = AllterraTheme.colors.muted,
                    )
                }
                AllterraIconButton(onClick = onOpenImport, modifier = Modifier.size(42.dp)) {
                    Icon(Icons.Outlined.FileUpload, contentDescription = null, tint = terra.color)
                }
                Spacer(modifier = Modifier.width(8.dp))
                AllterraIconButton(onClick = onOpenImport, modifier = Modifier.size(42.dp)) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = terra.color)
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AllterraTheme.colors.surface)
                    .border(1.dp, AllterraTheme.colors.line, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                RoutesTabButton("Mine", selectedTab == RoutesTab.Mine, Modifier.weight(1f)) { onTabSelected(RoutesTab.Mine) }
                RoutesTabButton("Saved", selectedTab == RoutesTab.Saved, Modifier.weight(1f)) { onTabSelected(RoutesTab.Saved) }
            }
        }

        state.createError?.let { message ->
            item {
                AllterraCard(backgroundColor = AllterraTheme.colors.bad.copy(alpha = 0.12f), borderColor = AllterraTheme.colors.bad.copy(alpha = 0.28f), hasShadow = false) {
                    Text(text = message, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.bad)
                }
            }
        }

        if (routes.isEmpty() && !state.isLoading) {
            item {
                AllterraCard(hasShadow = false) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = strings.postNoRoutes, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                        Spacer(modifier = Modifier.height(12.dp))
                        AllterraButton(text = strings.importGpxAction, variant = AllterraButtonVariant.Terra, onClick = onOpenImport)
                    }
                }
            }
        }

        items(routes, key = { it.id }) { route ->
            RouteCard(route = route, onClick = { onRouteClick(route) })
        }
    }
}

@Composable
private fun RoutesTabButton(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val terra = AllterraTheme.categorical.route
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) terra.color else Color.Transparent)
            .allterraClickable(onClick = onClick)
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = AllterraTheme.typography.smallStrong, color = if (selected) Color.White else AllterraTheme.colors.muted)
    }
}

@Composable
private fun RouteCard(route: RouteUiModel, onClick: () -> Unit) {
    val terra = AllterraTheme.categorical.route
    AllterraCard(modifier = Modifier.fillMaxWidth().allterraClickable(onClick = onClick), borderColor = terra.color.copy(alpha = 0.14f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AllterraChip(text = route.source.ifBlank { "GPX" }, categorical = terra)
                    AllterraChip(text = route.date)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = route.title, style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (route.description.isNotBlank()) {
                    Text(text = route.description, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RouteStat("km", route.distanceKm?.roundToOne() ?: "18.4")
                    RouteStat("m", "+${route.estimatedElevationMeters()}")
                    RouteStat("min", route.durationMinutes?.toString() ?: "330")
                }
            }
            RoutePreviewCanvas(
                points = route.previewPoints,
                lineColor = terra.color,
                modifier = Modifier.padding(start = 12.dp).size(width = 104.dp, height = 78.dp),
            )
        }
    }
}

@Composable
private fun RouteStat(unit: String, value: String) {
    Column {
        Text(text = value, style = AllterraTheme.typography.mono, color = AllterraTheme.colors.ink)
        Text(text = unit, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.muted)
    }
}

@Composable
private fun RouteDetailScreen(route: RouteUiModel, onBack: () -> Unit) {
    val strings = appStrings()
    val terra = AllterraTheme.categorical.route
    var tab by remember(route.id) { mutableStateOf(DetailTab.Overview) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(bottom = 112.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(AllterraTheme.colors.bgSub)) {
            RouteHeroCanvas(modifier = Modifier.fillMaxSize())
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
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
            Row(modifier = Modifier.align(Alignment.BottomStart).padding(14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AllterraChip(text = "Hard - T3", categorical = terra)
                AllterraChip(text = route.date)
            }
        }

        Column(modifier = Modifier.padding(horizontal = AllterraTheme.spacing.screenPaddingX, vertical = 16.dp)) {
            Text(text = "Tatry, PL", style = AllterraTheme.typography.caption, color = terra.color)
            Text(text = route.title, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)

            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailStat(strings.routeDistanceLabel, route.distanceKm?.roundToOne() ?: "18.4", "km", Modifier.weight(1f))
                DetailStat(strings.routePointsLabel, route.estimatedElevationMeters().toString(), "m", Modifier.weight(1f))
                DetailStat(strings.routeDurationLabel, route.durationMinutes?.let { (it / 60.0).roundToOne() } ?: "5.5", "h", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AllterraTheme.colors.surface2)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                DetailTab.entries.forEach { item ->
                    RoutesTabButton(item.label(), tab == item, Modifier.weight(1f)) { tab = item }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            when (tab) {
                DetailTab.Overview -> RouteOverview(route)
                DetailTab.Elevation -> ElevationProfileCard()
                DetailTab.Waypoints -> WaypointsList()
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AllterraButton(
                    text = strings.onboardingStartAction,
                    variant = AllterraButtonVariant.Terra,
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Outlined.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) },
                    onClick = {},
                )
                AllterraButton(
                    text = strings.shareAction,
                    variant = AllterraButtonVariant.Secondary,
                    leadingIcon = { Icon(Icons.Outlined.Share, contentDescription = null, tint = AllterraTheme.colors.ink, modifier = Modifier.size(16.dp)) },
                    onClick = {},
                )
            }
        }
    }
}

@Composable
private fun DetailStat(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    AllterraCard(modifier = modifier, hasShadow = false) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.muted)
            Text(text = value, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
            Text(text = unit, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.muted)
        }
    }
}

@Composable
private fun RouteOverview(route: RouteUiModel) {
    AllterraCard(hasShadow = false) {
        Column {
            Text(
                text = route.description.ifBlank {
                    "Classic high trail with lakes, rocky sections, and a steady climb. Save offline before the start and keep the group route in sync."
                },
                style = AllterraTheme.typography.body,
                color = AllterraTheme.colors.ink2,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AllterraChip(text = "#rocks")
                AllterraChip(text = "#chains")
                AllterraChip(text = "#GOPR")
            }
        }
    }
}

@Composable
private fun ElevationProfileCard() {
    val terra = AllterraTheme.categorical.route
    val lineColor = AllterraTheme.colors.line
    AllterraCard(hasShadow = false) {
        Column {
            Text(text = "Elevation profile - 0 to 18.4 km", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(modifier = Modifier.fillMaxWidth().height(142.dp)) {
                val values = listOf(1080f, 1200f, 1330f, 1410f, 1583f, 1410f, 1620f, 1820f, 2200f, 2499f, 2100f, 1700f, 1200f)
                val min = values.minOrNull() ?: 0f
                val max = values.maxOrNull() ?: 1f
                val step = size.width / (values.lastIndex.coerceAtLeast(1))
                val path = Path()
                values.forEachIndexed { index, value ->
                    val x = step * index
                    val y = size.height - ((value - min) / (max - min)) * (size.height - 18f) - 9f
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                repeat(3) { line ->
                    val y = (line + 1) * size.height / 4f
                    drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                }
                drawPath(path, terra.color.copy(alpha = 0.3f), style = Stroke(width = 9f, cap = StrokeCap.Round))
                drawPath(path, terra.color, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            }
        }
    }
}

@Composable
private fun WaypointsList() {
    val terra = AllterraTheme.categorical.route
    val waypoints = listOf(
        "Czarny Staw" to "4.2 km - 1583 m",
        "Schronisko PTTK" to "6.8 km - 1410 m",
        "Rysy summit" to "12.0 km - 2499 m",
        "Bivouac spot" to "14.5 km - 2100 m",
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        waypoints.forEachIndexed { index, item ->
            AllterraCard(hasShadow = false) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(terra.soft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "${index + 1}", style = AllterraTheme.typography.mono, color = terra.color)
                    }
                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(text = item.first, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                        Text(text = item.second, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteImportSheet(
    state: RoutesUiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onImportGpx: () -> Unit,
    onSave: () -> Unit,
) {
    val strings = appStrings()
    val terra = AllterraTheme.categorical.route
    val draft = state.createDraft

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(text = strings.importGpxAction, style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(AllterraTheme.colors.surface2)
                .border(2.dp, AllterraTheme.colors.line2, RoundedCornerShape(18.dp))
                .allterraClickable(enabled = !state.isSaving, onClick = onImportGpx)
                .padding(vertical = 28.dp, horizontal = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(58.dp).clip(RoundedCornerShape(16.dp)).background(terra.soft), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.FileUpload, contentDescription = null, tint = terra.color, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = draft.importedFileName ?: strings.routeNoFileSelected, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                Text(text = ".gpx - up to 10 MB", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
            }
        }

        if (state.isSaving) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = terra.color,
                trackColor = terra.soft,
            )
            val progressLabel = when (state.createProgress) {
                RouteCreateProgressStage.UPLOADING -> strings.routeUploadingLabel
                RouteCreateProgressStage.PROCESSING -> strings.routeProcessingLabel
                null -> strings.loadingText
            }
            Text(text = progressLabel, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
        }

        if (draft.importedFileName != null) {
            AllterraCard(hasShadow = false, backgroundColor = terra.soft) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = terra.color)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Ready to save", style = AllterraTheme.typography.bodyStrong, color = terra.ink)
                }
            }
        }

        AllterraInput(value = draft.title, onValueChange = onTitleChanged, placeholder = strings.routeTitleLabel)
        AllterraInput(value = draft.description, onValueChange = onDescriptionChanged, placeholder = strings.routeDescriptionLabel, singleLine = false)

        state.createError?.let {
            Text(text = it, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.bad)
        }

        AllterraButton(
            text = strings.saveAction,
            variant = AllterraButtonVariant.Terra,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving,
            onClick = onSave,
        )
    }
}

@Composable
private fun RouteHeroCanvas(modifier: Modifier = Modifier) {
    val terra = AllterraTheme.categorical.route
    val line = AllterraTheme.colors.line2
    Canvas(modifier = modifier) {
        repeat(12) { index ->
            val radius = 35f + index * 18f
            drawOval(
                color = line.copy(alpha = 0.72f),
                topLeft = Offset(size.width * 0.68f - radius, size.height * 0.38f - radius * 0.72f),
                size = androidx.compose.ui.geometry.Size(radius * 1.8f, radius * 1.22f),
                style = Stroke(width = if (index % 3 == 0) 2f else 1f),
            )
        }
        val path = Path().apply {
            moveTo(size.width * 0.08f, size.height * 0.84f)
            quadraticTo(size.width * 0.26f, size.height * 0.68f, size.width * 0.42f, size.height * 0.62f)
            quadraticTo(size.width * 0.6f, size.height * 0.54f, size.width * 0.78f, size.height * 0.3f)
        }
        drawPath(path, color = terra.color.copy(alpha = 0.3f), style = Stroke(width = 8f, cap = StrokeCap.Round))
        drawPath(path, color = terra.color, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
        drawCircle(Color.White, radius = 10f, center = Offset(size.width * 0.08f, size.height * 0.84f))
        drawCircle(terra.color, radius = 6f, center = Offset(size.width * 0.08f, size.height * 0.84f))
        drawCircle(Color.White, radius = 10f, center = Offset(size.width * 0.78f, size.height * 0.3f))
        drawCircle(terra.color, radius = 6f, center = Offset(size.width * 0.78f, size.height * 0.3f))
    }
}

private fun DetailTab.label(): String {
    return when (this) {
        DetailTab.Overview -> "Overview"
        DetailTab.Elevation -> "Elevation"
        DetailTab.Waypoints -> "Waypoints"
    }
}

private fun RouteUiModel.estimatedElevationMeters(): Int {
    return ((distanceKm ?: 12.0) * 64).roundToInt().coerceAtLeast(180)
}

private fun Double.roundToOne(): String {
    return (this * 10.0).roundToInt().let { "${it / 10}.${it % 10}" }
}
