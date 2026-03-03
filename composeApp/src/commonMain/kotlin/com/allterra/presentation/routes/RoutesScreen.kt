package com.allterra.presentation.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.presentation.common.components.list.SwipeToDeleteItem
import com.allterra.presentation.localization.appStrings

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

    if (state.isCreateOpen) {
        RouteCreateScreen(
            state = state,
            onBack = viewModel::closeCreateRoute,
            onTitleChanged = viewModel::onCreateTitleChanged,
            onDescriptionChanged = viewModel::onCreateDescriptionChanged,
            onImportGpx = gpxFilePicker::launch,
            onSave = { viewModel.saveCreatedRoute(strings.routeCreationValidation) },
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(Brush.verticalGradient(listOf(Color(0xFF0A7DAF), Color(0xFFC2C7CC)))),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = strings.routesTitle,
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
                            onDelete = { viewModel.deleteRoute(item.id) },
                    ) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2D8A8E))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    if (item.description.isNotBlank()) {
                                        Text(
                                            text = item.description,
                                            color = Color.White.copy(alpha = 0.88f),
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 2,
                                        )
                                    }
                                    Text(
                                        text = listOfNotNull(
                                            item.date,
                                            item.distanceKm?.let { "${it} km" },
                                            item.durationMinutes?.let { "${it} min" },
                                        ).joinToString("  •  "),
                                        color = Color.White.copy(alpha = 0.78f),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                RoutePreviewCanvas(
                                    points = item.previewPoints,
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .size(width = 96.dp, height = 66.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = viewModel::openCreateRoute,
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

@Composable
private fun RouteCreateScreen(
    state: RoutesUiState,
    onBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onImportGpx: () -> Unit,
    onSave: () -> Unit,
) {
    val strings = appStrings()
    val draft = state.createDraft

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
                text = strings.routeCreateTitle,
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
            label = { Text(strings.routeTitleLabel) },
            singleLine = true,
        )

        OutlinedTextField(
            value = draft.description,
            onValueChange = onDescriptionChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = { Text(strings.routeDescriptionLabel) },
            minLines = 3,
        )

        Button(
            onClick = onImportGpx,
            enabled = !state.isSaving,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text(strings.importGpxAction)
        }

        Text(
            text = "${strings.routeGpxLabel}: ${draft.importedFileName ?: strings.routeNoFileSelected}",
            modifier = Modifier.padding(top = 8.dp),
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyMedium,
        )

        state.createError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        if (state.isSaving) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
            )
            val progressLabel = when (state.createProgress) {
                com.allterra.domain.repository.RouteCreateProgressStage.UPLOADING -> strings.routeUploadingLabel
                com.allterra.domain.repository.RouteCreateProgressStage.PROCESSING -> strings.routeProcessingLabel
                null -> strings.loadingText
            }
            Text(
                text = progressLabel,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Button(
            onClick = onSave,
            enabled = !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 8.dp),
        ) {
            Text(strings.saveAction)
        }
    }
}
