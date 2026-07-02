package com.allterra.presentation.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Backpack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraButtonVariant
import com.allterra.presentation.common.components.redesign.AllterraChip
import com.allterra.presentation.common.components.redesign.AllterraInput
import com.allterra.presentation.common.model.ActivityTypeUi
import com.allterra.presentation.common.model.PostAudienceUi
import com.allterra.presentation.common.model.PostTypeUi
import com.allterra.presentation.common.model.PoiUiModel
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.pois.PoiPhotoImage
import com.allterra.presentation.profile.ProfileUiState
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.presentation.trips.TripUiModel

import androidx.compose.foundation.layout.PaddingValues

const val MAX_POST_PHOTOS = 9

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostComposerScreen(
    state: ProfileUiState,
    trips: List<TripUiModel>,
    routes: List<RouteUiModel>,
    pois: List<PoiUiModel>,
    onBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onTypeSelected: (PostTypeUi) -> Unit,
    onActivitySelected: (ActivityTypeUi) -> Unit,
    onAudienceSelected: (PostAudienceUi) -> Unit,
    onTripSelected: (String?) -> Unit,
    onRouteSelected: (String?) -> Unit,
    onTogglePoi: (String) -> Unit,
    onAddPhotos: (Int) -> Unit,
    onRemovePhoto: (String) -> Unit,
    onSave: () -> Unit,
) {
    val strings = appStrings()
    val draft = state.postCreateDraft
    val selectedTrip = trips.firstOrNull { it.id == draft.selectedTripId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(AllterraTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AllterraTheme.spacing.screenPaddingX, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(AllterraTheme.radius.sm))
                    .background(AllterraTheme.colors.surface)
                    .border(1.dp, AllterraTheme.colors.line2, RoundedCornerShape(AllterraTheme.radius.sm))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                    tint = AllterraTheme.colors.ink,
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = strings.postCreateTitle,
                    style = AllterraTheme.typography.title,
                    color = AllterraTheme.colors.ink,
                )
                Text(
                    text = strings.postNewSubtitle,
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted,
                )
            }

            AllterraButton(
                text = strings.postPublishAction,
                onClick = onSave,
                isSmall = true,
                variant = AllterraButtonVariant.Primary,
                enabled = !state.isSaving && draft.title.isNotBlank(),
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        Text(
            text = strings.postTypeLabel,
            style = AllterraTheme.typography.caption,
            color = AllterraTheme.colors.muted,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AllterraTheme.spacing.s2),
            horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3),
        ) {
            PostTypeSelector(
                types = listOf(
                    PostTypeUi.CHECK_IN to strings.postTypeCheckIn,
                    PostTypeUi.ROUTE to strings.postTypeRoute,
                    PostTypeUi.NOTE to strings.postTypeNote,
                    PostTypeUi.GEAR to strings.postTypeGear,
                ),
                selectedType = draft.type,
                onSelect = onTypeSelected,
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(AllterraTheme.radius.lg))
                .background(
                    Brush.linearGradient(
                        colors = listOf(AllterraTheme.colors.skySoft, AllterraTheme.colors.ochreSoft)
                    )
                )
                .border(1.dp, AllterraTheme.colors.line2, RoundedCornerShape(AllterraTheme.radius.lg)) // TODO: Dash border
                .clickable { onAddPhotos((MAX_POST_PHOTOS - draft.photoUris.size).coerceAtLeast(0)) },
            contentAlignment = Alignment.Center,
        ) {
            if (draft.photoUris.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = AllterraTheme.colors.sky,
                            modifier = Modifier.size(24.dp)
                        )
                        Icon(
                            imageVector = Icons.Outlined.Upload,
                            contentDescription = null,
                            tint = AllterraTheme.colors.sky,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = strings.postPhotoUploadTitle,
                        style = AllterraTheme.typography.bodyStrong,
                        color = AllterraTheme.colors.sky,
                    )
                    Text(
                        text = strings.postPhotoUploadLimit,
                        style = AllterraTheme.typography.small,
                        color = AllterraTheme.colors.muted,
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(draft.photoUris, key = { it }) { uri ->
                        Box(
                            modifier = Modifier
                                .size(156.dp)
                                .clip(RoundedCornerShape(AllterraTheme.radius.md)),
                        ) {
                            PoiPhotoImage(source = uri, modifier = Modifier.fillMaxSize())
                            IconButton(
                                onClick = { onRemovePhoto(uri) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
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
                        Box(
                            modifier = Modifier
                                .size(156.dp)
                                .clip(RoundedCornerShape(AllterraTheme.radius.md))
                                .background(AllterraTheme.colors.surface)
                                .border(1.dp, AllterraTheme.colors.line2, RoundedCornerShape(AllterraTheme.radius.md))
                                .clickable { onAddPhotos((MAX_POST_PHOTOS - draft.photoUris.size).coerceAtLeast(0)) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = AllterraTheme.colors.muted,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s5))

        Text(
            text = strings.locationLabel,
            style = AllterraTheme.typography.caption,
            color = AllterraTheme.colors.muted,
        )
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s2))

        AllterraInput(
            value = draft.title,
            onValueChange = onTitleChanged,
            placeholder = strings.postLocationPlaceholder,
        )

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s4))

        AllterraInput(
            value = draft.description,
            onValueChange = onDescriptionChanged,
            placeholder = strings.postBodyPlaceholder,
            singleLine = false,
            modifier = Modifier.height(140.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = "${draft.description.length} / 1500",
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted2,
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s5))

        Text(
            text = strings.postAudienceLabel,
            style = AllterraTheme.typography.caption,
            color = AllterraTheme.colors.muted,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AllterraTheme.spacing.s2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PostAudienceUi.entries.forEach { audience ->
                val selected = audience == draft.audience
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (selected) AllterraTheme.categorical.social.color else AllterraTheme.colors.surface)
                        .border(
                            width = if (selected) 0.dp else 1.dp,
                            color = AllterraTheme.colors.line2,
                            shape = RoundedCornerShape(999.dp),
                        )
                        .clickable { onAudienceSelected(audience) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = when (audience) {
                            PostAudienceUi.PUBLIC -> strings.postAudiencePublic
                            PostAudienceUi.FRIENDS -> strings.postAudienceFriends
                            PostAudienceUi.CLUB -> strings.postAudienceClub
                        },
                        style = AllterraTheme.typography.smallStrong,
                        color = if (selected) Color.White else AllterraTheme.colors.ink,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s5))

        // Trip selector
        if (trips.isNotEmpty()) {
            Text(
                text = strings.postTripLabel,
                style = AllterraTheme.typography.caption,
                color = AllterraTheme.colors.muted,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AllterraTheme.spacing.s2)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                trips.forEach { trip ->
                    val selected = trip.id == draft.selectedTripId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (selected) AllterraTheme.categorical.social.color else AllterraTheme.colors.surface)
                            .border(
                                width = if (selected) 0.dp else 1.dp,
                                color = AllterraTheme.colors.line2,
                                shape = RoundedCornerShape(999.dp),
                            )
                            .clickable {
                                onTripSelected(if (selected) null else trip.id)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = trip.title,
                            style = AllterraTheme.typography.smallStrong,
                            color = if (selected) Color.White else AllterraTheme.colors.ink,
                            maxLines = 1,
                        )
                    }
                }
            }
        } else {
            Text(
                text = strings.postNoTrips,
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s5))

        // Route selector
        if (routes.isNotEmpty()) {
            Text(
                text = strings.postRouteLabel,
                style = AllterraTheme.typography.caption,
                color = AllterraTheme.colors.muted,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AllterraTheme.spacing.s2)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                routes.forEach { route ->
                    val selected = route.id == draft.selectedRouteId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (selected) AllterraTheme.colors.ochre else AllterraTheme.colors.surface)
                            .border(
                                width = if (selected) 0.dp else 1.dp,
                                color = AllterraTheme.colors.line2,
                                shape = RoundedCornerShape(999.dp),
                            )
                            .clickable {
                                onRouteSelected(if (selected) null else route.id)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = route.title,
                            style = AllterraTheme.typography.smallStrong,
                            color = if (selected) Color.White else AllterraTheme.colors.ink,
                            maxLines = 1,
                        )
                    }
                }
            }
        } else {
            Text(
                text = strings.postNoRoutes,
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s5))

        if (selectedTrip != null) {
            PostLinkedItemCard(
                title = selectedTrip.title,
                subtitle = "${selectedTrip.dates} · ${selectedTrip.region}",
                icon = Icons.Outlined.Map,
                onEdit = { /* TODO: Open selection sheet */ }
            )
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        Text(
            text = strings.postActivityLabel,
            style = AllterraTheme.typography.caption,
            color = AllterraTheme.colors.muted,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AllterraTheme.spacing.s2)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActivityTypeChip(
                type = ActivityTypeUi.HIKE,
                label = strings.feedChipHike,
                isSelected = draft.activity == ActivityTypeUi.HIKE,
                onClick = { onActivitySelected(ActivityTypeUi.HIKE) }
            )
            ActivityTypeChip(
                type = ActivityTypeUi.BIKEPACKING,
                label = strings.feedChipBikepacking,
                isSelected = draft.activity == ActivityTypeUi.BIKEPACKING,
                onClick = { onActivitySelected(ActivityTypeUi.BIKEPACKING) }
            )
            ActivityTypeChip(
                type = ActivityTypeUi.ALPINISM,
                label = strings.feedChipClimb,
                isSelected = draft.activity == ActivityTypeUi.ALPINISM,
                onClick = { onActivitySelected(ActivityTypeUi.ALPINISM) }
            )
            ActivityTypeChip(
                type = ActivityTypeUi.TREK,
                label = strings.feedChipTrek,
                isSelected = draft.activity == ActivityTypeUi.TREK,
                onClick = { onActivitySelected(ActivityTypeUi.TREK) }
            )
            ActivityTypeChip(
                type = ActivityTypeUi.CLIMB,
                label = strings.feedChipClimbing,
                isSelected = draft.activity == ActivityTypeUi.CLIMB,
                onClick = { onActivitySelected(ActivityTypeUi.CLIMB) }
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun PostTypeSelector(
    types: List<Pair<PostTypeUi, String>>,
    selectedType: PostTypeUi,
    onSelect: (PostTypeUi) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3),
    ) {
        types.forEach { (type, label) ->
            val selected = type == selectedType
            val icon = when (type) {
                PostTypeUi.CHECK_IN -> Icons.Outlined.LocationOn
                PostTypeUi.ROUTE -> Icons.Outlined.Timeline
                PostTypeUi.NOTE -> Icons.Outlined.Description
                PostTypeUi.GEAR -> Icons.Outlined.Backpack
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(AllterraTheme.radius.md))
                    .background(if (selected) AllterraTheme.colors.terra else AllterraTheme.colors.surface)
                    .border(
                        width = if (selected) 0.dp else 1.dp,
                        color = AllterraTheme.colors.line2,
                        shape = RoundedCornerShape(AllterraTheme.radius.md)
                    )
                    .clickable { onSelect(type) }
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else AllterraTheme.colors.ink,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = label,
                    style = AllterraTheme.typography.caption,
                    color = if (selected) Color.White else AllterraTheme.colors.ink,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ActivityTypeChip(
    type: ActivityTypeUi,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val categorical = AllterraTheme.categorical.social
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (isSelected) categorical.color else AllterraTheme.colors.surface)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = AllterraTheme.colors.line2,
                shape = RoundedCornerShape(999.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // TODO: Add activity specific emoji or icons
        Text(
            text = label,
            style = AllterraTheme.typography.smallStrong,
            color = if (isSelected) Color.White else AllterraTheme.colors.ink,
        )
    }
}

@Composable
private fun PostLinkedItemCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onEdit: () -> Unit,
) {
    val strings = appStrings()
    Card(
        shape = RoundedCornerShape(AllterraTheme.radius.md),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AllterraTheme.colors.surface)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(AllterraTheme.radius.sm))
                    .background(AllterraTheme.colors.skySoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AllterraTheme.colors.sky,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.postLinkedItemLabel,
                    style = AllterraTheme.typography.caption,
                    color = AllterraTheme.colors.muted2,
                )
                Text(
                    text = title,
                    style = AllterraTheme.typography.bodyStrong,
                    color = AllterraTheme.colors.ink,
                )
                Text(
                    text = subtitle,
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted,
                )
            }
            Icon(
                imageVector = Icons.Outlined.Check, // Replace with ChevronRight if available
                contentDescription = null,
                tint = AllterraTheme.colors.muted2,
            )
        }
    }
}
