package com.allterra.presentation.profile

import androidx.compose.foundation.background
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
import com.allterra.presentation.common.model.PoiUiModel
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.pois.PoiPhotoImage
import com.allterra.presentation.pois.rememberPoiPhotoPicker

private const val MAX_POST_PHOTOS = 10

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    availableRoutes: List<RouteUiModel>,
    availablePois: List<PoiUiModel>,
    onOpenPois: () -> Unit,
    onOpenRoutes: () -> Unit,
    onOpenWardrobe: () -> Unit,
    onLogout: () -> Unit,
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
        PostCreateScreen(
            state = state,
            routes = availableRoutes,
            pois = availablePois,
            onBack = viewModel::closeCreatePost,
            onTitleChanged = viewModel::onPostTitleChanged,
            onDescriptionChanged = viewModel::onPostDescriptionChanged,
            onRouteSelected = viewModel::onPostRouteSelected,
            onTogglePoi = viewModel::togglePostPoi,
            onAddPhotos = { photoPicker.launch(it) },
            onRemovePhoto = viewModel::removeCreatePhoto,
            onSave = { viewModel.saveCreatedPost(strings.postCreationValidation, availableRoutes, availablePois) },
        )
        return
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

            if (state.feedLayout == ProfileFeedLayout.LIST) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.activities, key = { it.id }) { item ->
                        ActivityCard(
                            item = item,
                            strings = strings,
                            onToggleLike = viewModel::toggleLike,
                            onToggleBookmark = viewModel::toggleBookmark,
                            onOpen = {},
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    gridItems(state.activities, key = { it.id }) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostCreateScreen(
    state: ProfileUiState,
    routes: List<RouteUiModel>,
    pois: List<PoiUiModel>,
    onBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onRouteSelected: (String?) -> Unit,
    onTogglePoi: (String) -> Unit,
    onAddPhotos: (Int) -> Unit,
    onRemovePhoto: (String) -> Unit,
    onSave: () -> Unit,
) {
    val strings = appStrings()
    val draft = state.postCreateDraft
    var routeExpanded by remember { mutableStateOf(false) }
    var poisExpanded by remember { mutableStateOf(false) }
    val selectedRoute = routes.firstOrNull { it.id == draft.selectedRouteId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(Brush.verticalGradient(listOf(Color(0xFF0A7DAF), Color(0xFFC2C7CC))))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFFE6EEF2),
                )
            }
            Text(
                text = strings.postCreateTitle,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        OutlinedTextField(
            value = draft.title,
            onValueChange = onTitleChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = { Text(strings.postTitleLabel) },
            singleLine = true,
        )

        OutlinedTextField(
            value = draft.description,
            onValueChange = onDescriptionChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = { Text(strings.postDescriptionLabel) },
            minLines = 4,
        )

        Text(
            text = strings.postRouteLabel,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 12.dp),
        )

        if (routes.isEmpty()) {
            Text(
                text = strings.postNoRoutes,
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            ExposedDropdownMenuBox(
                expanded = routeExpanded,
                onExpandedChange = { routeExpanded = !routeExpanded },
            ) {
                OutlinedTextField(
                    value = selectedRoute?.title ?: strings.noneLabel,
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = routeExpanded) },
                )
                ExposedDropdownMenu(
                    expanded = routeExpanded,
                    onDismissRequest = { routeExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = {
                            SelectionItemRow(
                                title = strings.noneLabel,
                                date = "",
                            )
                        },
                        onClick = {
                            onRouteSelected(null)
                            routeExpanded = false
                        },
                    )
                    routes.forEach { route ->
                        DropdownMenuItem(
                            text = {
                                SelectionItemRow(
                                    title = route.title,
                                    date = route.date,
                                )
                            },
                            onClick = {
                                onRouteSelected(route.id)
                                routeExpanded = false
                            },
                        )
                    }
                }
            }
        }

        Text(
            text = strings.postPoisLabel,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 12.dp),
        )

        if (pois.isEmpty()) {
            Text(
                text = strings.postNoPois,
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            ExposedDropdownMenuBox(
                expanded = poisExpanded,
                onExpandedChange = { poisExpanded = !poisExpanded },
            ) {
                OutlinedTextField(
                    value = if (draft.selectedPoiIds.isEmpty()) strings.noneLabel else "${draft.selectedPoiIds.size}/${pois.size}",
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = poisExpanded) },
                )
                ExposedDropdownMenu(
                    expanded = poisExpanded,
                    onDismissRequest = { poisExpanded = false },
                ) {
                    pois.forEach { poi ->
                        val selected = poi.id in draft.selectedPoiIds
                        DropdownMenuItem(
                            text = {
                                SelectionItemRow(
                                    title = poi.title,
                                    date = poi.addedAt,
                                )
                            },
                            onClick = { onTogglePoi(poi.id) },
                            trailingIcon = {
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF0B7A74),
                                    )
                                }
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(strings.cancelAction) },
                        onClick = { poisExpanded = false },
                    )
                }
            }
            if (draft.selectedPoiIds.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(pois.filter { it.id in draft.selectedPoiIds }, key = { it.id }) { poi ->
                        FilterChip(
                            selected = true,
                            onClick = { onTogglePoi(poi.id) },
                            label = { Text(poi.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = strings.photosLabel,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = "${draft.photoUris.size}/$MAX_POST_PHOTOS",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(draft.photoUris, key = { it }) { uri ->
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(10.dp)),
                ) {
                    PoiPhotoImage(
                        source = uri,
                        modifier = Modifier.fillMaxSize(),
                    )
                    IconButton(
                        onClick = { onRemovePhoto(uri) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(Color(0x9A10252D), CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
            item {
                IconButton(
                    onClick = { onAddPhotos((MAX_POST_PHOTOS - draft.photoUris.size).coerceAtLeast(0)) },
                    enabled = draft.photoUris.size < MAX_POST_PHOTOS,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF107E76), CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }

        if (draft.photoUris.isEmpty()) {
            Text(
                text = strings.postNoPhotosSelected,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Text(
            text = strings.postPhotosHint,
            modifier = Modifier.padding(top = 6.dp),
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall,
        )

        state.postCreateError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            enabled = !state.isSaving,
        ) {
            Text(if (state.isSaving) strings.loadingText else strings.saveAction)
        }
    }
}

@Composable
private fun SelectionItemRow(
    title: String,
    date: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (date.isNotBlank()) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF5D7A86),
                modifier = Modifier.padding(start = 10.dp),
            )
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
