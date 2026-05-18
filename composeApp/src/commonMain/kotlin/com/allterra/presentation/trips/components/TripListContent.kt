package com.allterra.presentation.trips.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.presentation.trips.TripStatus
import com.allterra.presentation.trips.TripUiModel

@Composable
fun TripListContent(
    trips: List<TripUiModel>,
    onTripClick: (TripUiModel) -> Unit,
    onCreateTrip: () -> Unit,
) {
    val strings = appStrings()
    val terra = AllterraTheme.categorical.route
    var selectedStatus by remember { mutableStateOf<TripStatus?>(null) }
    
    val filteredTrips = remember(trips, selectedStatus) {
        if (selectedStatus == null) trips else trips.filter { it.status == selectedStatus }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
    ) {
        // Appbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
                .padding(top = 24.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(strings.tabTrips, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
                Text(
                    text = "${trips.size} outings · ${trips.count { it.status == TripStatus.Active }} active",
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted
                )
            }
            Row {
                AllterraIconButton(onClick = {}) {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = AllterraTheme.colors.ink)
                }
                Spacer(modifier = Modifier.width(8.dp))
                AllterraIconButton(onClick = onCreateTrip) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = terra.color)
                }
            }
        }

        // Quick Filters
        Row(
            modifier = Modifier
                .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("All", selectedStatus == null) { selectedStatus = null }
            FilterChip("Planned", selectedStatus == TripStatus.Planned) { selectedStatus = TripStatus.Planned }
            FilterChip("Active", selectedStatus == TripStatus.Active) { selectedStatus = TripStatus.Active }
        }

        if (filteredTrips.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(strings.tripsEmptyPlannedTitle, style = AllterraTheme.typography.body, color = AllterraTheme.colors.muted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = AllterraTheme.spacing.screenPaddingX,
                    end = AllterraTheme.spacing.screenPaddingX,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)
            ) {
                items(filteredTrips) { trip ->
                    TripCard(trip = trip, onClick = { onTripClick(trip) })
                }
            }
        }
    }
}

@Composable
private fun TripCard(trip: TripUiModel, onClick: () -> Unit) {
    val terra = AllterraTheme.categorical.route
    
    AllterraCard(
        modifier = Modifier.fillMaxWidth().allterraClickable { onClick() },
        backgroundColor = if (trip.status == TripStatus.Active) terra.color else AllterraTheme.colors.surface,
        hasShadow = true
    ) {
        Column {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = trip.title,
                    style = AllterraTheme.typography.title,
                    color = if (trip.status == TripStatus.Active) Color.White else AllterraTheme.colors.ink
                )
                if (trip.status == TripStatus.Active) {
                    Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text("ACTIVE", style = AllterraTheme.typography.caption, color = Color.White)
                    }
                }
            }
            Text(
                text = "${trip.dates} · ${trip.region}",
                style = AllterraTheme.typography.small,
                color = if (trip.status == TripStatus.Active) Color.White.copy(alpha = 0.8f) else AllterraTheme.colors.muted
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress or stats
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Docs: ${trip.readiness.docsReady}/${trip.readiness.docsTotal}",
                    style = AllterraTheme.typography.caption,
                    color = if (trip.status == TripStatus.Active) Color.White else terra.color
                )
                Text(
                    text = "Gear: ${trip.readiness.gearReady}/${trip.readiness.gearTotal}",
                    style = AllterraTheme.typography.caption,
                    color = if (trip.status == TripStatus.Active) Color.White else terra.color
                )
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val terra = AllterraTheme.categorical.route
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) terra.color else AllterraTheme.colors.bgSub)
            .allterraClickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = AllterraTheme.typography.caption,
            color = if (selected) Color.White else AllterraTheme.colors.muted
        )
    }
}
