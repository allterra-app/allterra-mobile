package com.allterra.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
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
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun WalletScreen(
    items: List<WalletItem>,
    onAddItem: () -> Unit,
    onItemClick: (WalletItem) -> Unit
) {
    val strings = appStrings()
    var selectedCategory by remember { mutableStateOf(WalletCategory.ALL) }
    
    val filteredItems = remember(items, selectedCategory) {
        if (selectedCategory == WalletCategory.ALL) items
        else items.filter { item ->
            when (selectedCategory) {
                WalletCategory.TRANSPORT -> item.type == DocumentType.TICKET
                WalletCategory.STAY -> item.type == DocumentType.BOOKING
                WalletCategory.DOCS -> item.type == DocumentType.INSURANCE || item.type == DocumentType.OTHER
                else -> true
            }
        }
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
            Text(strings.walletTitle, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
            Row {
                AllterraIconButton(onClick = {}) {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = AllterraTheme.colors.ink)
                }
                Spacer(modifier = Modifier.width(8.dp))
                AllterraIconButton(onClick = onAddItem) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = AllterraTheme.colors.moss)
                }
            }
        }

        // Stats Row
        Row(
            modifier = Modifier
                .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${items.size} documents · ${items.count { it.isOffline }} offline",
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted
            )
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategory.ordinal,
            containerColor = Color.Transparent,
            contentColor = AllterraTheme.colors.moss,
            divider = {},
            indicator = {},
            edgePadding = AllterraTheme.spacing.screenPaddingX
        ) {
            WalletCategory.entries.forEach { category ->
                val selected = selectedCategory == category
                Tab(
                    selected = selected,
                    onClick = { selectedCategory = category },
                    text = {
                        Text(
                            text = category.name.lowercase().capitalize(),
                            style = if (selected) AllterraTheme.typography.bodyStrong else AllterraTheme.typography.body,
                            color = if (selected) AllterraTheme.colors.moss else AllterraTheme.colors.muted
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredItems.isEmpty()) {
            WalletEmptyState(onAddItem)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = AllterraTheme.spacing.screenPaddingX,
                    end = AllterraTheme.spacing.screenPaddingX,
                    bottom = 100.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3),
                verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)
            ) {
                items(filteredItems) { item ->
                    DocumentCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

@Composable
private fun DocumentCard(item: WalletItem, onClick: () -> Unit) {
    val moss = AllterraTheme.categorical.wallet
    
    Box(
        modifier = Modifier
            .aspectRatio(0.8f)
            .shadow(2.dp, RoundedCornerShape(AllterraTheme.radius.md))
            .clip(RoundedCornerShape(AllterraTheme.radius.md))
            .background(AllterraTheme.colors.surface)
            .allterraClickable { onClick() }
    ) {
        Column {
            // Preview / Type Icon area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(moss.soft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.type.icon(),
                    contentDescription = null,
                    tint = moss.color,
                    modifier = Modifier.size(40.dp)
                )
                
                // Offline dot
                if (item.isOffline) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AllterraTheme.colors.good)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            
            // Info area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AllterraTheme.spacing.s3)
            ) {
                Text(
                    text = item.title,
                    style = AllterraTheme.typography.bodyStrong,
                    color = AllterraTheme.colors.ink,
                    maxLines = 1
                )
                Text(
                    text = item.date,
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted
                )
                if (item.tripName != null) {
                    Text(
                        text = item.tripName,
                        style = AllterraTheme.typography.caption,
                        color = moss.color,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletEmptyState(onAddItem: () -> Unit) {
    val strings = appStrings()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AllterraTheme.spacing.screenPaddingX),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            AllterraIcons.Wallet,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = AllterraTheme.colors.line2
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(strings.walletEmptyTitle, style = AllterraTheme.typography.title, textAlign = TextAlign.Center)
        Text(
            strings.walletEmptyBody,
            style = AllterraTheme.typography.body,
            color = AllterraTheme.colors.muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
        AllterraButton(text = strings.walletEmptyAction, onClick = onAddItem)
    }
}

private fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
