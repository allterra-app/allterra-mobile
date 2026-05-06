package com.allterra.presentation.trips

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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import com.allterra.presentation.wallet.WalletCategory
import com.allterra.presentation.wallet.WalletItem
import kotlin.math.roundToInt

private enum class TripFilter(val label: String) {
    Planned("Planned"),
    Active("Active"),
    Done("Done"),
}

private enum class TripDetailTab(val label: String) {
    Overview("Overview"),
    Route("Route"),
    Gear("Gear"),
    Docs("Docs"),
    Journal("Journal"),
}

private enum class TripStatus {
    Planned,
    Active,
    Done,
    Cancelled,
}

private data class TripReadiness(
    val docsReady: Int,
    val docsTotal: Int,
    val gearReady: Int,
    val gearTotal: Int,
    val routeReady: Boolean,
    val briefingReady: Boolean,
)

private data class TripBudget(
    val spent: Int,
    val planned: Int,
)

private data class TripWaypoint(
    val title: String,
    val meta: String,
)

private data class TripNote(
    val title: String,
    val body: String,
    val date: String,
    val author: String,
)

private data class TripUiModel(
    val id: String,
    val title: String,
    val region: String,
    val status: TripStatus,
    val dates: String,
    val daysLeft: Int? = null,
    val members: List<String>,
    val routeId: String?,
    val distanceKm: Double,
    val elevationM: Int,
    val durationLabel: String,
    val docsCount: Int,
    val gearCount: Int,
    val pinsCount: Int,
    val notesCount: Int,
    val readiness: TripReadiness,
    val budget: TripBudget,
    val weatherLabel: String,
    val activeProgress: Float? = null,
    val activeDayLabel: String? = null,
    val notes: List<TripNote> = emptyList(),
    val waypoints: List<TripWaypoint> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    routes: List<RouteUiModel>,
    walletItems: List<WalletItem>,
    onOpenRouteLibrary: () -> Unit,
    onOpenWallet: () -> Unit,
) {
    val trips = remember { emptyList<TripUiModel>() }
    var selectedFilter by remember { mutableStateOf(TripFilter.Planned) }
    var selectedTrip by remember { mutableStateOf<TripUiModel?>(null) }
    var showCreateSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.categorical.route.soft),
    ) {
        if (selectedTrip != null) {
            TripDetailScreen(
                trip = selectedTrip!!,
                route = routes.firstOrNull { it.id == selectedTrip!!.routeId },
                docs = walletItems.take(selectedTrip!!.docsCount),
                onBack = { selectedTrip = null },
                onOpenRouteLibrary = onOpenRouteLibrary,
                onOpenWallet = onOpenWallet,
            )
        } else {
            TripsListScreen(
                trips = trips,
                filter = selectedFilter,
                onFilterChange = { selectedFilter = it },
                onTripClick = { selectedTrip = it },
                onCreateTrip = { showCreateSheet = true },
            )
        }

        if (showCreateSheet) {
            AllterraSheet(onDismiss = { showCreateSheet = false }) {
                TripCreateSheet(
                    onClose = { showCreateSheet = false },
                    onCreateSuggestion = {
                        showCreateSheet = false
                    },
                )
            }
        }
    }
}

@Composable
private fun TripsListScreen(
    trips: List<TripUiModel>,
    filter: TripFilter,
    onFilterChange: (TripFilter) -> Unit,
    onTripClick: (TripUiModel) -> Unit,
    onCreateTrip: () -> Unit,
) {
    val filteredTrips = trips.filter {
        when (filter) {
            TripFilter.Planned -> it.status == TripStatus.Planned
            TripFilter.Active -> it.status == TripStatus.Active
            TripFilter.Done -> it.status == TripStatus.Done
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
            bottom = 110.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Trips", style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
                    Text(
                        text = "${trips.size} outings · ${trips.count { it.status == TripStatus.Active }} active",
                        style = AllterraTheme.typography.small,
                        color = AllterraTheme.colors.muted,
                    )
                }
                AllterraIconButton(onClick = {}, modifier = Modifier.size(42.dp)) {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = AllterraTheme.categorical.route.color)
                }
                Spacer(modifier = Modifier.width(8.dp))
                AllterraButton(
                    text = "Trip",
                    isSmall = true,
                    variant = AllterraButtonVariant.Terra,
                    leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp)) },
                    onClick = onCreateTrip,
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TripFilter.entries.forEach { item ->
                    FilterChip(
                        text = item.label,
                        selected = filter == item,
                        count = trips.count {
                            when (item) {
                                TripFilter.Planned -> it.status == TripStatus.Planned
                                TripFilter.Active -> it.status == TripStatus.Active
                                TripFilter.Done -> it.status == TripStatus.Done
                            }
                        },
                        onClick = { onFilterChange(item) },
                    )
                }
            }
        }

        if (filteredTrips.isEmpty()) {
            item {
                EmptyTripState(filter = filter, onCreateTrip = onCreateTrip)
            }
        } else if (filter == TripFilter.Active) {
            items(filteredTrips, key = { it.id }) { trip ->
                ActiveTripBanner(trip = trip, onClick = { onTripClick(trip) })
            }
        } else {
            items(filteredTrips, key = { it.id }) { trip ->
                TripRow(trip = trip, onClick = { onTripClick(trip) })
            }
        }

        item {
            AllterraCard(
                modifier = Modifier.fillMaxWidth().allterraClickable(onClick = onCreateTrip),
                backgroundColor = AllterraTheme.categorical.route.soft,
                borderColor = AllterraTheme.categorical.route.color.copy(alpha = 0.28f),
                hasShadow = false,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(AllterraTheme.categorical.route.color, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White)
                    }
                    Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                        Text("Plan a new trip", style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.categorical.route.ink)
                        Text("Import a ticket, route, or start from scratch", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTripState(filter: TripFilter, onCreateTrip: () -> Unit) {
    val strings = appStrings()
    val title = when (filter) {
        TripFilter.Planned -> strings.tripsEmptyPlannedTitle
        TripFilter.Active -> strings.tripsEmptyActiveTitle
        TripFilter.Done -> strings.tripsEmptyDoneTitle
    }
    val body = when (filter) {
        TripFilter.Planned -> strings.tripsEmptyPlannedBody
        TripFilter.Active -> strings.tripsEmptyActiveBody
        TripFilter.Done -> strings.tripsEmptyDoneBody
    }

    AllterraCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = AllterraTheme.colors.surface,
        borderColor = AllterraTheme.colors.line,
        hasShadow = false,
    ) {
        Column {
            Text(title, style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
            Spacer(modifier = Modifier.height(6.dp))
            Text(body, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
            Spacer(modifier = Modifier.height(14.dp))
            AllterraButton(
                text = strings.tripsCreateAction,
                isSmall = true,
                variant = AllterraButtonVariant.Terra,
                onClick = onCreateTrip,
            )
        }
    }
}

@Composable
private fun TripDetailScreen(
    trip: TripUiModel,
    route: RouteUiModel?,
    docs: List<WalletItem>,
    onBack: () -> Unit,
    onOpenRouteLibrary: () -> Unit,
    onOpenWallet: () -> Unit,
) {
    var selectedTab by remember(trip.id) { mutableStateOf(TripDetailTab.Overview) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .verticalScroll(rememberScrollState()),
    ) {
        TripHero(trip = trip, onBack = onBack)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AllterraTheme.colors.surface)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TripDetailTab.entries.forEach { tab ->
                TabChip(
                    text = tab.label,
                    selected = selectedTab == tab,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = tab },
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = AllterraTheme.spacing.screenPaddingX, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when (selectedTab) {
                TripDetailTab.Overview -> TripOverviewTab(trip = trip, onOpenTab = { selectedTab = it })
                TripDetailTab.Route -> TripRouteTab(trip = trip, route = route, onOpenRouteLibrary = onOpenRouteLibrary)
                TripDetailTab.Gear -> TripGearTab(trip = trip)
                TripDetailTab.Docs -> TripDocsTab(docs = docs, onOpenWallet = onOpenWallet)
                TripDetailTab.Journal -> TripJournalTab(notes = trip.notes)
            }
        }
    }
}

@Composable
private fun TripHero(trip: TripUiModel, onBack: () -> Unit) {
    val route = AllterraTheme.categorical.route
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AllterraTheme.colors.surface)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(bottom = 16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AllterraIconButton(onClick = onBack, modifier = Modifier.size(42.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = route.color)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AllterraChip(text = "Offline", categorical = route)
                    AllterraIconButton(onClick = {}, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = route.color)
                    }
                    AllterraIconButton(onClick = {}, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = route.color)
                    }
                }
            }

            Box(modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth().height(190.dp).background(AllterraTheme.colors.bgSub, RoundedCornerShape(18.dp))) {
                TripHeroMap(modifier = Modifier.fillMaxSize())
                StatusBadge(
                    status = trip.status,
                    modifier = Modifier.align(Alignment.TopStart).padding(10.dp),
                )
                AllterraButton(
                    text = "Open map",
                    isSmall = true,
                    variant = AllterraButtonVariant.Secondary,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
                    leadingIcon = { Icon(Icons.Outlined.Map, contentDescription = null, tint = AllterraTheme.colors.ink, modifier = Modifier.size(14.dp)) },
                    onClick = {},
                )
            }

            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                Text(trip.title, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
                Text("${trip.dates} · ${trip.region}", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HeroStat("Ready", "${trip.readiness.percent()}%", Modifier.weight(1f))
                    HeroStat("Group", trip.members.size.toString(), Modifier.weight(1f))
                    HeroStat("Weather", trip.weatherLabel, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, modifier: Modifier = Modifier) {
    AllterraCard(modifier = modifier, hasShadow = false, backgroundColor = AllterraTheme.colors.surface2) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.muted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
        }
    }
}

@Composable
private fun TripOverviewTab(trip: TripUiModel, onOpenTab: (TripDetailTab) -> Unit) {
    val checklist = listOf(
        ChecklistItem("Bookings & Docs", trip.readiness.docsReady, trip.readiness.docsTotal, TripDetailTab.Docs, Icons.Outlined.FilePresent),
        ChecklistItem("Gear", trip.readiness.gearReady, trip.readiness.gearTotal, TripDetailTab.Gear, Icons.Outlined.Inventory2),
        ChecklistItem("Route", if (trip.readiness.routeReady) 1 else 0, 1, TripDetailTab.Route, Icons.Outlined.Route),
        ChecklistItem("Briefing", if (trip.readiness.briefingReady) 1 else 0, 1, TripDetailTab.Overview, Icons.Outlined.Groups),
    )

    AllterraCard(hasShadow = false) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("TRIP READINESS", style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
            checklist.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().allterraClickable { onOpenTab(item.targetTab) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(if (item.ready) AllterraTheme.categorical.route.color else AllterraTheme.colors.surface2, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(item.icon, contentDescription = null, tint = if (item.ready) Color.White else AllterraTheme.categorical.route.color, modifier = Modifier.size(16.dp))
                    }
                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(item.title, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                        Text("${item.readyCount}/${item.totalCount}", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                    }
                    Text(if (item.ready) "Ready" else "${item.percent}%", style = AllterraTheme.typography.smallStrong, color = if (item.ready) AllterraTheme.colors.good else AllterraTheme.colors.warn)
                }
            }
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MetricTile("Distance", trip.distanceKm.roundToOne(), "km", Modifier.weight(1f))
        MetricTile("Climb", trip.elevationM.toString(), "m", Modifier.weight(1f))
        MetricTile("Duration", trip.durationLabel, "", Modifier.weight(1f))
    }

    AllterraCard(hasShadow = false) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Budget", style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                Spacer(modifier = Modifier.weight(1f))
                Text("${trip.budget.spent} / ${trip.budget.planned} PLN", style = AllterraTheme.typography.mono, color = AllterraTheme.colors.ink)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(AllterraTheme.colors.line, RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((trip.budget.spent.toFloat() / trip.budget.planned.toFloat()).coerceAtMost(1f))
                        .height(6.dp)
                        .background(AllterraTheme.categorical.route.color, RoundedCornerShape(999.dp)),
                )
            }
        }
    }
}

@Composable
private fun TripRouteTab(trip: TripUiModel, route: RouteUiModel?, onOpenRouteLibrary: () -> Unit) {
    val routeColor = AllterraTheme.categorical.route.color
    AllterraCard(hasShadow = false) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Route", style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                Spacer(modifier = Modifier.weight(1f))
                AllterraButton(
                    text = "Library",
                    isSmall = true,
                    variant = AllterraButtonVariant.Secondary,
                    onClick = onOpenRouteLibrary,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(AllterraTheme.colors.bgSub, RoundedCornerShape(16.dp)),
            ) {
                TripHeroMap(modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(route?.title ?: trip.title, style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
            Text("${trip.distanceKm.roundToOne()} km · ${trip.elevationM} m ↑", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
        }
    }

    AllterraCard(hasShadow = false) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Elevation profile", style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                val pts = listOf(80f, 72f, 60f, 42f, 28f, 18f, 30f, 62f)
                val path = Path()
                pts.forEachIndexed { index, y ->
                    val x = size.width / (pts.lastIndex) * index
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color = routeColor.copy(alpha = 0.28f), style = Stroke(width = 8f, cap = StrokeCap.Round))
                drawPath(path, color = routeColor, style = Stroke(width = 3f, cap = StrokeCap.Round))
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        trip.waypoints.forEachIndexed { index, waypoint ->
            AllterraCard(hasShadow = false) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(28.dp).background(AllterraTheme.categorical.route.soft, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${index + 1}", style = AllterraTheme.typography.mono, color = AllterraTheme.categorical.route.color)
                    }
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(waypoint.title, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                        Text(waypoint.meta, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun TripGearTab(trip: TripUiModel) {
    val categories = listOf(
        "Base" to listOf("Backpack 45L", "Sleeping bag -5C", "Tent 2P"),
        "Clothing" to listOf("Shell jacket", "Fleece", "Gloves"),
        "Food & Water" to listOf("Water filter", "Stove", "Freeze-dried meals"),
    )

    AllterraCard(hasShadow = false, backgroundColor = if (trip.readiness.gearReady == trip.readiness.gearTotal) AllterraTheme.categorical.wallet.soft else AllterraTheme.colors.surface) {
        Column {
            Text("${trip.readiness.gearReady} / ${trip.readiness.gearTotal}", style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
            Text("packed · approx 7.4 kg", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
        }
    }

    categories.forEachIndexed { _, item ->
        AllterraCard(hasShadow = false) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.first, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${item.second.size}/${item.second.size}", style = AllterraTheme.typography.mono, color = AllterraTheme.colors.muted)
                }
                item.second.forEach { label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = AllterraTheme.categorical.gear.color, modifier = Modifier.size(16.dp))
                        Text(label, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.ink, modifier = Modifier.padding(start = 10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TripDocsTab(docs: List<WalletItem>, onOpenWallet: () -> Unit) {
    AllterraCard(hasShadow = false) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AllterraButton(
                text = "Add document",
                variant = AllterraButtonVariant.Primary,
                modifier = Modifier.weight(1f),
                onClick = onOpenWallet,
            )
            AllterraButton(
                text = "Wallet",
                variant = AllterraButtonVariant.Secondary,
                onClick = onOpenWallet,
            )
        }
    }

    docs.forEach { doc ->
        AllterraCard(hasShadow = false) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(38.dp).background(AllterraTheme.categorical.wallet.soft, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        when (doc.category) {
                            WalletCategory.TICKET -> Icons.Outlined.Hiking
                            WalletCategory.BOOKING -> Icons.Outlined.FilePresent
                            WalletCategory.INSURANCE -> Icons.Outlined.Groups
                            WalletCategory.ID -> Icons.Outlined.NoteAlt
                            WalletCategory.OTHER -> Icons.Outlined.FilePresent
                        },
                        contentDescription = null,
                        tint = AllterraTheme.categorical.wallet.color,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(doc.title, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${doc.date} · ${doc.time}", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                }
            }
        }
    }
}

@Composable
private fun TripJournalTab(notes: List<TripNote>) {
    AllterraCard(
        hasShadow = false,
        backgroundColor = AllterraTheme.categorical.route.soft,
        borderColor = AllterraTheme.categorical.route.color.copy(alpha = 0.28f),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).background(AllterraTheme.categorical.route.color, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("New note", style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.categorical.route.ink)
                Text("Idea, observation, or group check", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
            }
        }
    }

    notes.forEach { note ->
        AllterraCard(hasShadow = false) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(note.title, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink, modifier = Modifier.weight(1f))
                    Text(note.date, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(note.body, style = AllterraTheme.typography.body, color = AllterraTheme.colors.ink2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(note.author, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.muted)
            }
        }
    }
}

@Composable
private fun TripCreateSheet(onClose: () -> Unit, onCreateSuggestion: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("2026-08-20") }
    var finish by remember { mutableStateOf("2026-08-22") }
    val sources = listOf(
        SourceTile("From scratch", "Name it, pick dates, set region", AllterraTheme.categorical.route.color),
        SourceTile("From ticket", "Detect dates from imported docs", AllterraTheme.categorical.wallet.color),
        SourceTile("From route", "Use a GPX track as a starting point", AllterraTheme.categorical.route.color2),
        SourceTile("Duplicate", "Copy an older trip and adapt it", AllterraTheme.categorical.social.color),
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("New trip", style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink, modifier = Modifier.weight(1f))
            AllterraIconButton(onClick = onClose, modifier = Modifier.size(38.dp)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = AllterraTheme.colors.muted)
            }
        }

        AllterraCard(backgroundColor = AllterraTheme.categorical.wallet.color, borderColor = Color.Transparent, hasShadow = false) {
            Column {
                Text("SYSTEM SUGGESTS", style = AllterraTheme.typography.caption, color = Color.White.copy(alpha = 0.9f))
                Text("Tatry 20-22 Aug?", style = AllterraTheme.typography.displayM, color = Color.White)
                Text("Ticket, hostel booking, and parking line up into one outing.", style = AllterraTheme.typography.small, color = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AllterraButton(text = "Create", variant = AllterraButtonVariant.Secondary, onClick = onCreateSuggestion)
                    AllterraButton(text = "Not now", variant = AllterraButtonVariant.Ghost, onClick = {})
                }
            }
        }

        Text("Creation path", style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sources.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { item ->
                        AllterraCard(
                            modifier = Modifier.weight(1f),
                            hasShadow = false,
                            borderColor = item.color.copy(alpha = 0.24f),
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier.size(36.dp).background(item.color.copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Outlined.Navigation, contentDescription = null, tint = item.color, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(item.title, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                                Text(item.subtitle, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                            }
                        }
                    }
                }
            }
        }

        Text("Quick setup", style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
        AllterraInput(value = title, onValueChange = { title = it }, placeholder = "Trip title")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AllterraInput(value = start, onValueChange = { start = it }, modifier = Modifier.weight(1f), placeholder = "Start")
            AllterraInput(value = finish, onValueChange = { finish = it }, modifier = Modifier.weight(1f), placeholder = "Finish")
        }
        AllterraButton(
            text = "Create trip",
            variant = AllterraButtonVariant.Terra,
            modifier = Modifier.fillMaxWidth(),
            onClick = onCreateSuggestion,
        )
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, count: Int, onClick: () -> Unit) {
    Text(
        text = "$text $count",
        style = AllterraTheme.typography.smallStrong,
        color = if (selected) Color.White else AllterraTheme.colors.ink2,
        modifier = Modifier
            .background(if (selected) AllterraTheme.colors.ink else AllterraTheme.colors.surface, RoundedCornerShape(999.dp))
            .border(1.dp, if (selected) Color.Transparent else AllterraTheme.colors.line, RoundedCornerShape(999.dp))
            .allterraClickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun ActiveTripBanner(trip: TripUiModel, onClick: () -> Unit) {
    AllterraCard(
        modifier = Modifier.fillMaxWidth().allterraClickable(onClick = onClick),
        backgroundColor = AllterraTheme.categorical.route.color,
        borderColor = Color.Transparent,
        hasShadow = false,
    ) {
        Column {
            Text("ACTIVE NOW", style = AllterraTheme.typography.caption, color = Color.White.copy(alpha = 0.9f))
            Text(trip.title, style = AllterraTheme.typography.displayM, color = Color.White)
            Text(trip.activeDayLabel ?: trip.dates, style = AllterraTheme.typography.small, color = Color.White.copy(alpha = 0.9f))
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(999.dp))) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(trip.activeProgress ?: 0.5f)
                        .height(4.dp)
                        .background(Color.White, RoundedCornerShape(999.dp)),
                )
            }
        }
    }
}

@Composable
private fun TripRow(trip: TripUiModel, onClick: () -> Unit) {
    AllterraCard(modifier = Modifier.fillMaxWidth().allterraClickable(onClick = onClick), hasShadow = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MiniMap(modifier = Modifier.size(68.dp))
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(status = trip.status)
                    if (trip.daysLeft != null && trip.daysLeft <= 14) {
                        Text("in ${trip.daysLeft} d", style = AllterraTheme.typography.smallStrong, color = AllterraTheme.categorical.route.color)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(trip.title, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                Text("${trip.dates} · ${trip.region}", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
                Spacer(modifier = Modifier.height(6.dp))
                Text("${trip.distanceKm.roundToOne()} km · ${trip.elevationM} m", style = AllterraTheme.typography.mono, color = AllterraTheme.colors.muted)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: TripStatus, modifier: Modifier = Modifier) {
    val (text, color, bg) = when (status) {
        TripStatus.Planned -> Triple("Planned", AllterraTheme.categorical.social.color, AllterraTheme.categorical.social.soft)
        TripStatus.Active -> Triple("Active", AllterraTheme.categorical.route.color, AllterraTheme.categorical.route.soft)
        TripStatus.Done -> Triple("Done", AllterraTheme.categorical.wallet.color, AllterraTheme.categorical.wallet.soft)
        TripStatus.Cancelled -> Triple("Cancelled", AllterraTheme.colors.muted, AllterraTheme.colors.surface2)
    }
    AllterraChip(text = text, categorical = null, backgroundColor = bg, contentColor = color, modifier = modifier)
}

@Composable
private fun TabChip(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(if (selected) AllterraTheme.categorical.route.soft else Color.Transparent, RoundedCornerShape(10.dp))
            .allterraClickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = AllterraTheme.typography.smallStrong, color = if (selected) AllterraTheme.categorical.route.color else AllterraTheme.colors.muted)
    }
}

@Composable
private fun MetricTile(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    AllterraCard(modifier = modifier, hasShadow = false) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.muted)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(unit, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.muted)
                }
            }
        }
    }
}

@Composable
private fun TripHeroMap(modifier: Modifier = Modifier) {
    val routeColor = AllterraTheme.categorical.route.color
    val gearColor = AllterraTheme.categorical.gear.color
    Canvas(modifier = modifier) {
        repeat(8) { index ->
            val y = 26f + index * 18f
            drawPath(
                path = Path().apply {
                    moveTo(0f, y)
                    quadraticTo(size.width * 0.25f, y - 10f, size.width * 0.5f, y - 4f)
                    quadraticTo(size.width * 0.75f, y + 6f, size.width, y - 2f)
                },
                color = Color.Black.copy(alpha = 0.15f),
                style = Stroke(width = 1f),
            )
        }
        val route = Path().apply {
            moveTo(size.width * 0.1f, size.height * 0.78f)
            quadraticTo(size.width * 0.3f, size.height * 0.66f, size.width * 0.4f, size.height * 0.54f)
            quadraticTo(size.width * 0.58f, size.height * 0.38f, size.width * 0.72f, size.height * 0.28f)
            quadraticTo(size.width * 0.84f, size.height * 0.18f, size.width * 0.92f, size.height * 0.16f)
        }
        drawPath(route, color = routeColor.copy(alpha = 0.3f), style = Stroke(width = 7f, cap = StrokeCap.Round))
        drawPath(route, color = routeColor, style = Stroke(width = 3f, cap = StrokeCap.Round))
        drawCircle(Color.White, radius = 8f, center = Offset(size.width * 0.1f, size.height * 0.78f))
        drawCircle(routeColor, radius = 5f, center = Offset(size.width * 0.1f, size.height * 0.78f))
        drawCircle(Color.White, radius = 8f, center = Offset(size.width * 0.92f, size.height * 0.16f))
        drawCircle(gearColor, radius = 5f, center = Offset(size.width * 0.92f, size.height * 0.16f))
    }
}

@Composable
private fun MiniMap(modifier: Modifier = Modifier) {
    val routeColor = AllterraTheme.categorical.route.color
    Canvas(
        modifier = modifier
            .background(AllterraTheme.colors.surface2, RoundedCornerShape(12.dp))
            .border(1.dp, AllterraTheme.colors.line, RoundedCornerShape(12.dp)),
    ) {
        val path = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.74f)
            quadraticTo(size.width * 0.32f, size.height * 0.6f, size.width * 0.45f, size.height * 0.52f)
            quadraticTo(size.width * 0.62f, size.height * 0.42f, size.width * 0.84f, size.height * 0.18f)
        }
        drawPath(path, color = routeColor, style = Stroke(width = 2.2f, cap = StrokeCap.Round))
        drawCircle(Color.White, radius = 5f, center = Offset(size.width * 0.12f, size.height * 0.74f))
        drawCircle(routeColor, radius = 3f, center = Offset(size.width * 0.12f, size.height * 0.74f))
    }
}

private data class ChecklistItem(
    val title: String,
    val readyCount: Int,
    val totalCount: Int,
    val targetTab: TripDetailTab,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    val ready: Boolean get() = readyCount >= totalCount
    val percent: Int get() = if (totalCount == 0) 100 else (readyCount.toFloat() / totalCount.toFloat() * 100f).roundToInt()
}

private data class SourceTile(
    val title: String,
    val subtitle: String,
    val color: Color,
)

private fun TripReadiness.percent(): Int {
    val parts = listOf(
        if (docsTotal == 0) 1f else docsReady.toFloat() / docsTotal.toFloat(),
        if (gearTotal == 0) 1f else gearReady.toFloat() / gearTotal.toFloat(),
        if (routeReady) 1f else 0f,
        if (briefingReady) 1f else 0f,
    )
    return (parts.average() * 100f).roundToInt()
}

private fun Double.roundToOne(): String {
    return (this * 10.0).roundToInt().let { "${it / 10}.${it % 10}" }
}
