package com.allterra.presentation.pois

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.presentation.common.components.list.SwipeToDeleteItem
import com.allterra.presentation.common.model.PoiType
import com.allterra.presentation.localization.AppStrings
import com.allterra.presentation.localization.appStrings

@Composable
fun PoisScreen(
    viewModel: PoisViewModel,
    onBack: () -> Unit,
) {
    val strings = appStrings()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var previewPhoto by remember { mutableStateOf<String?>(null) }
    val photoPicker = rememberPoiPhotoPicker(
        onPhotosPicked = { uris ->
            viewModel.addCreatePhotos(
                photoUris = uris,
                maxPhotos = MAX_POI_PHOTOS,
                limitMessage = strings.poiPhotoLimitReached,
            )
        },
        onReadError = { viewModel.onCreatePhotoReadError(strings.poiPhotoReadError) },
    )

    if (state.isCreateOpen) {
        PoiCreateScreen(
            state = state,
            onBack = viewModel::closeCreatePoi,
            onNameChanged = viewModel::onCreateNameChanged,
            onTypeChanged = viewModel::onCreateTypeChanged,
            onAddPhotos = { photoPicker.launch(it) },
            onRemovePhoto = viewModel::removeCreatePhoto,
            onPhotoPreview = { previewPhoto = it },
            onSave = { viewModel.saveCreatedPoi(strings.poiCreationValidation) },
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .background(Brush.verticalGradient(listOf(Color(0xFF0A7DAF), Color(0xFFC2C7CC)))),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 8.dp, end = 8.dp),
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
                        text = strings.poisTitle,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                state.createError?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(state.items, key = { it.id }) { item ->
                        SwipeToDeleteItem(
                            enabled = item.id !in state.deletingIds,
                            deleteLabel = strings.deleteAction,
                            confirmTitle = strings.deleteConfirmTitle,
                            confirmMessage = strings.deleteConfirmMessage,
                            confirmActionLabel = strings.deleteAction,
                            cancelActionLabel = strings.cancelAction,
                            onDelete = { viewModel.deletePoi(item.id) },
                        ) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF2D8A8E))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            text = "${poiTypeLabel(item.type, strings)}  •  ${item.photoUris.size} ${strings.photosLabel}",
                                            color = Color.White.copy(alpha = 0.82f),
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                        Text(
                                            text = "${strings.addedLabel}: ${item.addedAt}   ${strings.updatedLabel}: ${item.updatedAt}",
                                            color = Color.White.copy(alpha = 0.78f),
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                    if (item.photoUris.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { previewPhoto = item.photoUris.first() },
                                        ) {
                                            PoiPhotoImage(
                                                source = item.photoUris.first(),
                                                modifier = Modifier.fillMaxSize(),
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(Color(0xFF1E7D84), CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.LocationOn,
                                                contentDescription = null,
                                                tint = Color(0xFFEAF5F6),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = viewModel::openCreatePoi,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(18.dp),
                containerColor = Color(0xFF107E76),
                contentColor = Color.White,
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
            }
        }
    }

    PhotoPreviewDialog(photo = previewPhoto, onDismiss = { previewPhoto = null })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PoiCreateScreen(
    state: PoisUiState,
    onBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onTypeChanged: (PoiType) -> Unit,
    onAddPhotos: (Int) -> Unit,
    onRemovePhoto: (String) -> Unit,
    onPhotoPreview: (String) -> Unit,
    onSave: () -> Unit,
) {
    val strings = appStrings()
    val draft = state.createDraft
    var typeExpanded by remember { mutableStateOf(false) }

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
                text = strings.poiCreateTitle,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        OutlinedTextField(
            value = draft.name,
            onValueChange = onNameChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = { Text(strings.poiNameLabel) },
            singleLine = true,
        )

        Text(
            text = strings.poiTypeLabel,
            modifier = Modifier.padding(top = 10.dp),
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
        )

        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded },
        ) {
            OutlinedTextField(
                value = poiTypeLabel(draft.type, strings),
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                },
                singleLine = true,
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false },
            ) {
                PoiType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(poiTypeLabel(type, strings)) },
                        onClick = {
                            onTypeChanged(type)
                            typeExpanded = false
                        },
                    )
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
                text = "${draft.photoUris.size}/$MAX_POI_PHOTOS",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(draft.photoUris, key = { it }) { photo ->
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onPhotoPreview(photo) },
                ) {
                    PoiPhotoImage(
                        source = photo,
                        modifier = Modifier.fillMaxSize(),
                    )
                    IconButton(
                        onClick = { onRemovePhoto(photo) },
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
                    onClick = { onAddPhotos((MAX_POI_PHOTOS - draft.photoUris.size).coerceAtLeast(0)) },
                    enabled = draft.photoUris.size < MAX_POI_PHOTOS,
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
                text = strings.poiNoPhotosSelected,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Text(
            text = strings.poiPhotosHint,
            modifier = Modifier.padding(top = 6.dp),
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall,
        )

        state.createError?.let {
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
                .padding(top = 14.dp),
        ) {
            Text(strings.saveAction)
        }
    }
}

@Composable
private fun PhotoPreviewDialog(
    photo: String?,
    onDismiss: () -> Unit,
) {
    if (photo == null) return
    val strings = appStrings()
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancelAction) }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp, max = 420.dp)
                    .clip(RoundedCornerShape(12.dp)),
            ) {
                PoiPhotoImage(
                    source = photo,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    )
}

private const val MAX_POI_PHOTOS = 5

private fun poiTypeLabel(type: PoiType, strings: AppStrings): String {
    return when (type) {
        PoiType.SHOP -> strings.poiTypeShop
        PoiType.PARKING -> strings.poiTypeParking
        PoiType.CAMPING -> strings.poiTypeCamping
        PoiType.WATER_SOURCE -> strings.poiTypeWater
        PoiType.OTHER -> strings.poiTypeOther
    }
}
