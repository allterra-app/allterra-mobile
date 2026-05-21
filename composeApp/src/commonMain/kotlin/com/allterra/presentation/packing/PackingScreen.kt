package com.allterra.presentation.packing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.allterra.core.ui.UiState
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.theme.AllterraCategory
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun PackingScreen(
    viewModel: PackingViewModel,
    onBack: () -> Unit
) {
    AllterraTheme(category = AllterraCategory.Gear) {
        Scaffold(
            topBar = {
                PackingTopBar(
                    onBack = onBack,
                    progress = viewModel.getProgress(),
                    totalWeight = viewModel.getTotalWeight()
                )
            },
            containerColor = AllterraTheme.colors.bg
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (val state = viewModel.state) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = AllterraTheme.colors.ochre
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = state.message,
                            modifier = Modifier.align(Alignment.Center),
                            color = AllterraTheme.colors.danger
                        )
                    }
                    is UiState.Success -> {
                        PackingList(
                            items = state.data,
                            onToggle = viewModel::togglePackedStatus
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun PackingTopBar(
    onBack: () -> Unit,
    progress: Pair<Int, Int>,
    totalWeight: Double
) {
    Column(
        modifier = Modifier
            .background(AllterraTheme.colors.surface)
            .statusBarsPadding()
            .padding(horizontal = AllterraTheme.spacing.screenPaddingX, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AllterraIconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = AllterraTheme.colors.ink
                )
            }
            Spacer(modifier = Modifier.width(AllterraTheme.spacing.s3))
            Column {
                Text(
                    text = "Packing List",
                    style = AllterraTheme.typography.displayM,
                    color = AllterraTheme.colors.ink
                )
                Text(
                    text = "${progress.first}/${progress.second} • ${totalWeight} kg",
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        val progressValue = if (progress.second > 0) progress.first.toFloat() / progress.second else 0f
        AllterraProgressBar(
            progress = progressValue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PackingList(
    items: List<PackingItemUiModel>,
    onToggle: (String, Boolean) -> Unit
) {
    val grouped = items.groupBy { it.category }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = AllterraTheme.spacing.screenPaddingX,
            vertical = AllterraTheme.spacing.sectionGap
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        grouped.forEach { (category, categoryItems) ->
            item {
                Text(
                    text = category.uppercase(),
                    style = AllterraTheme.typography.caption,
                    color = AllterraTheme.colors.muted,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(categoryItems) { item ->
                PackingItemRow(item, onToggle)
            }
        }
    }
}

@Composable
private fun PackingItemRow(
    item: PackingItemUiModel,
    onToggle: (String, Boolean) -> Unit
) {
    AllterraCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            AllterraCheckbox(
                checked = item.packed,
                onCheckedChange = { onToggle(item.gearId, item.packed) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = AllterraTheme.typography.bodyStrong,
                    color = if (item.packed) AllterraTheme.colors.muted else AllterraTheme.colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.weightKg} kg",
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted2
                )
            }
            if (item.packed) {
                AllterraChip(
                    text = "Packed",
                    categorical = AllterraTheme.categorical.gear
                )
            }
        }
    }
}
