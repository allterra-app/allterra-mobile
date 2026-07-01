package com.allterra.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewComfy
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.presentation.common.components.cards.ActivityCard
import com.allterra.presentation.common.components.list.SwipeToDeleteItem
import com.allterra.presentation.common.model.PoiUiModel
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.post.MAX_POST_PHOTOS
import com.allterra.presentation.post.PostComposerScreen
import com.allterra.presentation.pois.PoiPhotoImage
import com.allterra.presentation.pois.rememberPoiPhotoPicker
import com.allterra.presentation.trips.TripUiModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    availableTrips: List<TripUiModel>,
    availableRoutes: List<RouteUiModel>,
    availablePois: List<PoiUiModel>,
    onOpenPois: () -> Unit,
    onOpenRoutes: () -> Unit,
    onOpenWardrobe: () -> Unit,
    onLogout: () -> Unit,
    onOpenPost: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = appStrings()
    val photoPicker = rememberPoiPhotoPicker(
        onPhotosPicked = { uris ->
            viewModel.addCreatePhotos(
                photoUris = uris,
                maxPhotos = MAX_POST_PHOTOS,
                limitMessage = strings.postPhotoLimitReached,
            )
        },
        onReadError = { viewModel.onPostPhotoReadError(strings.postPhotoReadError) },
    )

    if (state.isCreatePostOpen) {
        PostComposerScreen(
            state = state,
            trips = availableTrips,
            routes = availableRoutes,
            pois = availablePois,
            onBack = viewModel::closeCreatePost,
            onTitleChanged = viewModel::onPostTitleChanged,
            onDescriptionChanged = viewModel::onPostDescriptionChanged,
            onTypeSelected = viewModel::onPostTypeSelected,
            onActivitySelected = viewModel::onPostActivitySelected,
            onAudienceSelected = viewModel::onPostAudienceSelected,
            onTripSelected = viewModel::onPostTripSelected,
            onRouteSelected = viewModel::onPostRouteSelected,
            onTogglePoi = viewModel::togglePostPoi,
            onAddPhotos = { photoPicker.launch(it) },
            onRemovePhoto = viewModel::removeCreatePhoto,
            onSave = {
                viewModel.saveCreatedPost(
                    validationMessage = strings.postCreationValidation,
                    availableTrips = availableTrips,
                    availableRoutes = availableRoutes,
                    availablePois = availablePois,
                )
            },
        )
        return
    }

    val filteredActivities = when (state.journalFilter) {
        JournalFilter.ALL -> state.activities
        JournalFilter.TRIP_LINKED -> state.activities.filter { it.tripId != null }
        JournalFilter.STANDALONE -> state.activities.filter { it.tripId == null }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A7DAF), Color(0xFFC2C7CC))
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(onClick = onLogout, modifier = Modifier.padding(end = 6.dp)) {
                    Text(strings.logoutAction)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8BCF66)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(84.dp),
                )
            }

            if (state.userName.isNotBlank()) {
                Text(
                    text = state.userName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                )
            }

            Text(
                text = strings.aboutTitle,
                color = Color.White,
                modifier = Modifier.padding(top = 10.dp, start = 14.dp),
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = state.about,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    color = Color(0xFF22323A),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ProfileActionButton(icon = Icons.Outlined.LocationOn, onClick = onOpenPois)
                ProfileActionButton(icon = Icons.Outlined.Work, onClick = onOpenWardrobe)
                ProfileActionButton(icon = Icons.Outlined.Map, onClick = onOpenRoutes)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = state.journalFilter == JournalFilter.ALL,
                            onClick = { viewModel.setJournalFilter(JournalFilter.ALL) },
                            label = { Text(strings.journalAllFilter) },
                        )
                    }
                    item {
                        FilterChip(
                            selected = state.journalFilter == JournalFilter.TRIP_LINKED,
                            onClick = { viewModel.setJournalFilter(JournalFilter.TRIP_LINKED) },
                            label = { Text(strings.journalTripFilter) },
                        )
                    }
                    item {
                        FilterChip(
                            selected = state.journalFilter == JournalFilter.STANDALONE,
                            onClick = { viewModel.setJournalFilter(JournalFilter.STANDALONE) },
                            label = { Text(strings.journalStandaloneFilter) },
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { viewModel.setLayout(ProfileFeedLayout.LIST) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ViewList,
                            contentDescription = null,
                            tint = if (state.feedLayout == ProfileFeedLayout.LIST) Color(0xFF0B7A74) else Color(0xFF5A6E77),
                            modifier = Modifier.size(26.dp),
                        )
                    }
                    IconButton(onClick = { viewModel.setLayout(ProfileFeedLayout.GRID) }) {
                        Icon(
                            imageVector = Icons.Outlined.ViewComfy,
                            contentDescription = null,
                            tint = if (state.feedLayout == ProfileFeedLayout.GRID) Color(0xFF0B7A74) else Color(0xFF5A6E77),
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
            }

            state.postCreateError?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }

            if (filteredActivities.isEmpty() && !state.isLoading) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.journalEmptyTitle,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = strings.journalEmptyBody,
                            modifier = Modifier.padding(top = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF5A6E77),
                        )
                    }
                }
            } else if (state.feedLayout == ProfileFeedLayout.LIST) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filteredActivities, key = { it.id }) { item ->
                        SwipeToDeleteItem(
                            enabled = item.id !in state.deletingPostIds,
                            deleteLabel = strings.deleteAction,
                            confirmTitle = strings.deleteConfirmTitle,
                            confirmMessage = strings.deleteConfirmMessage,
                            confirmActionLabel = strings.deleteAction,
                            cancelActionLabel = strings.cancelAction,
                            onDelete = { viewModel.deletePost(item.id) },
                        ) {
                            ActivityCard(
                                item = item,
                                strings = strings,
                                onToggleLike = viewModel::toggleLike,
                                onToggleBookmark = viewModel::toggleBookmark,
                                onOpen = onOpenPost,
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    gridItems(filteredActivities, key = { it.id }) { item ->
                        SwipeToDeleteItem(
                            enabled = item.id !in state.deletingPostIds,
                            deleteLabel = strings.deleteAction,
                            confirmTitle = strings.deleteConfirmTitle,
                            confirmMessage = strings.deleteConfirmMessage,
                            confirmActionLabel = strings.deleteAction,
                            cancelActionLabel = strings.cancelAction,
                            onDelete = { viewModel.deletePost(item.id) },
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenPost(item.id) }
                            ) {
                                Text(
                                    text = item.title,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    text = item.description,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    maxLines = 4,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = viewModel::openCreatePost,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF107E76),
            contentColor = Color.White,
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
        }
    }
}

@Composable
private fun ProfileActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(62.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFE6EEF0),
        )
    }
}
