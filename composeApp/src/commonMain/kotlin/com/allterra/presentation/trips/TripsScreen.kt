package com.allterra.presentation.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.presentation.common.components.redesign.AllterraSheet
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.presentation.trips.components.TripDetailScreen
import com.allterra.presentation.trips.components.TripListContent
import com.allterra.presentation.wallet.WalletItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    viewModel: TripViewModel,
    routes: List<RouteUiModel>,
    walletItems: List<WalletItem>,
    onRoutesClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTrip by remember { mutableStateOf<TripUiModel?>(null) }
    var showCreateSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(AllterraTheme.colors.bg)) {
        selectedTrip?.let { trip ->
            TripDetailScreen(
                trip = trip,
                route = routes.find { it.id == trip.routeId },
                docs = walletItems.filter { it.id in listOf("1", "2") }, // Mock link logic for now
                onBack = { selectedTrip = null }
            )
        } ?: TripListContent(
            trips = state.items,
            onTripClick = { selectedTrip = it },
            onCreateTrip = { showCreateSheet = true }
        )

        if (showCreateSheet) {
            AllterraSheet(onDismiss = { showCreateSheet = false }) {
                // TripCreateSheet will be fully implemented in Task 6 final polish
                Box(modifier = Modifier.fillMaxWidth().height(400.dp).padding(24.dp)) {
                    Text("Create Trip Wizard Placeholder", style = AllterraTheme.typography.displayM)
                }
            }
        }
    }
}
