package com.allterra.presentation.gear

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.config.AppConfig
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraIconButton
import com.allterra.presentation.common.components.redesign.AllterraButtonVariant
import com.allterra.presentation.common.components.redesign.AllterraCard
import com.allterra.presentation.common.components.redesign.AllterraInput
import com.allterra.presentation.common.components.redesign.AllterraSheet
import com.allterra.presentation.common.components.redesign.allterraClickable
import com.allterra.presentation.theme.AllterraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearInventoryScreen(
    viewModel: GearViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val filteredItems = remember(state.items, state.selectedCategory) {
        state.items.filter { state.selectedCategory == null || it.category == state.selectedCategory }
    }
    val totalWeight = remember(filteredItems) { filteredItems.sumOf { it.weightKg } }
    val totalUses = remember(filteredItems) { filteredItems.sumOf { it.usesCount } }
    val categories = remember(state.items) { state.items.map { it.category }.distinct().sorted() }

    LaunchedEffect(Unit) {
        println("ALLTERRA_GEAR_DEBUG screenOpened baseUrl=${AppConfig.baseUrl}")
    }

    LaunchedEffect(state.successMessage) {
        val message = state.successMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.consumeSuccessMessage()
    }

    Box(modifier = Modifier.fillMaxSize().background(AllterraTheme.colors.bg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
                    .padding(top = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AllterraIconButton(
                        onClick = onBack,
                        modifier = Modifier.size(42.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = AllterraTheme.categorical.gear.color,
                        )
                    }
                    Column {
                        Text("Inventory", style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
                        Text(
                            "${filteredItems.size} • ${String.format("%.2f", totalWeight)} kg",
                            style = AllterraTheme.typography.small,
                            color = AllterraTheme.colors.muted,
                        )
                    }
                }
                AllterraIconButton(onClick = viewModel::openAddForm) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = AllterraTheme.categorical.gear.color,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AllterraCard(
                modifier = Modifier.padding(horizontal = AllterraTheme.spacing.screenPaddingX)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatTile(state.items.size.toString(), "items")
                    StatTile(totalUses.toString(), "uses")
                    StatTile(String.format("%.2f", totalWeight), "kg")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.padding(horizontal = AllterraTheme.spacing.screenPaddingX),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CategoryChip("All", state.selectedCategory == null) { viewModel.onCategorySelected(null) }
                categories.forEach { category ->
                    CategoryChip(category, state.selectedCategory == category) { viewModel.onCategorySelected(category) }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredItems.isEmpty()) {
                AllterraCard {
                    Text("No gear items yet", style = AllterraTheme.typography.body, color = AllterraTheme.colors.muted)
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = AllterraTheme.spacing.screenPaddingX)) {
                    filteredItems.groupBy { it.category }.forEach { (category, items) ->
                    Text(category, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.categorical.gear.ink)
                    Spacer(modifier = Modifier.height(8.dp))
                    items.forEach { item ->
                        GearItemRow(item = item, onClick = { viewModel.openDetail(item.id) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                }
            }
            Spacer(modifier = Modifier.height(90.dp))
        }

        val selected = state.items.firstOrNull { it.id == state.selectedItemId }
        if (selected != null) {
            AllterraSheet(onDismiss = viewModel::closeDetail) {
                GearDetailSheet(
                    item = selected,
                    onEdit = { viewModel.openEditForm(selected.id) },
                    onDelete = { viewModel.deleteGear(selected.id) },
                )
            }
        }

        if (state.isEditorOpen) {
            val editing = state.items.firstOrNull { it.id == state.editorItemId }
            AllterraSheet(onDismiss = viewModel::closeEditor) {
                GearEditorSheet(
                    initial = editing,
                    errorMessage = state.editorError,
                    onSave = viewModel::saveGear,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

@Composable
private fun GearItemRow(item: GearItemUiModel, onClick: () -> Unit) {
    AllterraCard(modifier = Modifier.fillMaxWidth().allterraClickable { onClick() }) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AllterraTheme.categorical.gear.soft),
                contentAlignment = Alignment.Center
            ) {
                Icon(AllterraIcons.Scales, contentDescription = null, tint = AllterraTheme.categorical.gear.color)
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(item.name, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
                Text("${item.category} · ${String.format("%.2f", item.weightKg)} kg", style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
            }
            StatusDot(item.status)
        }
    }
}

@Composable
private fun StatusDot(status: GearStatus) {
    val color = when (status) {
        GearStatus.NEW -> Color(0xFF2E7D32)
        GearStatus.GOOD -> Color(0xFF1565C0)
        GearStatus.WORN -> Color(0xFFF57C00)
        GearStatus.BROKEN -> AllterraTheme.colors.muted2
    }
    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
}

@Composable
private fun StatTile(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = AllterraTheme.typography.displayM, color = AllterraTheme.categorical.gear.color)
        Text(label, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
    }
}

@Composable
private fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) AllterraTheme.categorical.gear.color else AllterraTheme.colors.surface2)
            .allterraClickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = if (selected) Color.White else AllterraTheme.colors.ink, style = AllterraTheme.typography.smallStrong)
    }
}

@Composable
private fun GearDetailSheet(
    item: GearItemUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(item.name, style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
        Text("${item.category} · ${String.format("%.2f", item.weightKg)} kg", style = AllterraTheme.typography.body, color = AllterraTheme.colors.muted)
        HorizontalDivider(color = AllterraTheme.colors.line2)
        Text("Uses: ${item.usesCount}", style = AllterraTheme.typography.small, color = AllterraTheme.colors.ink)
        Text("Condition: ${item.status.name}", style = AllterraTheme.typography.small, color = AllterraTheme.colors.ink)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AllterraButton(text = "Edit", modifier = Modifier.weight(1f), onClick = onEdit)
            AllterraButton(text = "Delete", variant = AllterraButtonVariant.Secondary, modifier = Modifier.weight(1f), onClick = onDelete)
        }
    }
}

@Composable
private fun GearEditorSheet(
    initial: GearItemUiModel?,
    errorMessage: String?,
    onSave: (GearDraft) -> Unit,
) {
    var name by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var category by remember(initial) { mutableStateOf(initial?.category ?: "") }
    var weight by remember(initial) { mutableStateOf(initial?.weightKg?.toString() ?: "") }
    var status by remember(initial) { mutableStateOf(initial?.status ?: GearStatus.GOOD) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (initial == null) "Add gear" else "Edit gear", style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
        AllterraInput(value = name, onValueChange = { name = it }, placeholder = "Name")
        AllterraInput(value = category, onValueChange = { category = it }, placeholder = "Category")
        AllterraInput(value = weight, onValueChange = { weight = it }, placeholder = "Weight (kg)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GearStatus.entries.forEach {
                CategoryChip(it.name, status == it) { status = it }
            }
        }
        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                style = AllterraTheme.typography.small,
                color = Color(0xFFB3261E),
            )
        }
        AllterraButton(
            text = "Save",
            onClick = {
                val w = weight.toDoubleOrNull() ?: 0.0
                onSave(GearDraft(name = name, category = category, weightKg = w, status = status))
            }
        )
    }
}
