package com.allterra.presentation.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.routes.components.RouteDetailScreen
import com.allterra.presentation.routes.components.RouteImportSheet
import com.allterra.presentation.theme.AllterraTheme

private enum class RoutesTab {
    Mine,
    Saved,
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
    var selectedTab by remember { mutableStateOf(RoutesTab.Mine) }
    var selectedRoute by remember { mutableStateOf<RouteUiModel?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(AllterraTheme.colors.bg)) {
        selectedRoute?.let { route ->
            RouteDetailScreen(
                route = route,
                onBack = { selectedRoute = null },
            )
        } ?: Column(modifier = Modifier.fillMaxSize()) {
            // Appbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
                    .padding(top = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(strings.routesTitle, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
                Row {
                    AllterraIconButton(onClick = {}) {
                        Icon(Icons.Outlined.Search, contentDescription = null, tint = AllterraTheme.colors.ink)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AllterraIconButton(onClick = viewModel::openCreateRoute) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = AllterraTheme.categorical.route.color)
                    }
                }
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = AllterraTheme.categorical.route.color,
                divider = {},
                indicator = {},
                edgePadding = AllterraTheme.spacing.screenPaddingX
            ) {
                RoutesTab.entries.forEach { tab ->
                    val selected = selectedTab == tab
                    Tab(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.name,
                                style = if (selected) AllterraTheme.typography.bodyStrong else AllterraTheme.typography.body,
                                color = if (selected) AllterraTheme.categorical.route.color else AllterraTheme.colors.muted
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = AllterraTheme.spacing.screenPaddingX,
                    end = AllterraTheme.spacing.screenPaddingX,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)
            ) {
                items(state.items) { route ->
                    RouteLibraryCard(route = route, onClick = { selectedRoute = route })
                }
            }
        }

        if (state.isCreateOpen) {
            AllterraSheet(onDismiss = viewModel::closeCreateRoute) {
                RouteImportSheet(
                    isUploading = state.createProgress != null,
                    progressStage = state.createProgress,
                    title = state.createDraft.title,
                    description = state.createDraft.description,
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
private fun RouteLibraryCard(route: RouteUiModel, onClick: () -> Unit) {
    val terra = AllterraTheme.categorical.route
    
    AllterraCard(
        modifier = Modifier.fillMaxWidth().allterraClickable { onClick() },
        backgroundColor = AllterraTheme.colors.surface,
        hasShadow = true
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(AllterraTheme.radius.md))
                    .background(terra.soft),
                contentAlignment = Alignment.Center
            ) {
                RoutePreviewCanvas(
                    points = route.previewPoints,
                    lineColor = terra.color,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.title,
                    style = AllterraTheme.typography.bodyStrong,
                    color = AllterraTheme.colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${route.distanceKm ?: 0.0} km · ${route.date}",
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted
                )
            }
            
            Icon(
                imageVector = AllterraIcons.Route,
                contentDescription = null,
                tint = terra.color.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
