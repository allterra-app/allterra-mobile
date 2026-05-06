package com.allterra.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun WalletScreen(
    items: List<WalletItem>,
    onAddItem: () -> Unit,
    onItemClick: (WalletItem) -> Unit
) {
    val moss = AllterraTheme.categorical.wallet
    val strings = appStrings()
    val groups = listOf(
        strings.walletTicketsGroup to items.filter { it.category == WalletCategory.TICKET },
        strings.walletBookingsGroup to items.filter { it.category == WalletCategory.BOOKING },
        strings.walletInsuranceGroup to items.filter { it.category == WalletCategory.INSURANCE },
        strings.walletOtherGroup to items.filter { it.category == WalletCategory.ID || it.category == WalletCategory.OTHER },
    ).filter { it.second.isNotEmpty() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(moss.soft)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = AllterraTheme.spacing.screenPaddingX,
                end = AllterraTheme.spacing.screenPaddingX,
                top = 24.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.walletTitle,
                        style = AllterraTheme.typography.displayM,
                        color = moss.ink
                    )
                    AllterraChip(
                        text = strings.walletOfflineStatus,
                        categorical = moss
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            groups.forEach { (title, groupItems) ->
                item {
                    Text(
                        text = title.uppercase(),
                        style = AllterraTheme.typography.caption,
                        color = moss.ink.copy(alpha = 0.72f),
                        modifier = Modifier.padding(top = AllterraTheme.spacing.s2)
                    )
                }
                items(groupItems) { item ->
                    WalletItemRow(item = item, onClick = { onItemClick(item) })
                }
            }

            if (groups.isEmpty()) {
                item {
                    AllterraCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = AllterraTheme.colors.surface,
                        borderColor = AllterraTheme.colors.line,
                        hasShadow = false,
                    ) {
                        Column {
                            Text(
                                text = strings.walletEmptyTitle,
                                style = AllterraTheme.typography.title,
                                color = AllterraTheme.colors.ink,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.walletEmptyBody,
                                style = AllterraTheme.typography.small,
                                color = AllterraTheme.colors.muted,
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            AllterraButton(
                                text = strings.walletEmptyAction,
                                variant = AllterraButtonVariant.Primary,
                                isSmall = true,
                                onClick = onAddItem,
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 110.dp, end = 24.dp)
        ) {
            AllterraIconButton(
                onClick = onAddItem,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = AllterraTheme.colors.moss)
            }
        }
    }
}

@Composable
fun WalletItemRow(
    item: WalletItem,
    onClick: () -> Unit
) {
    val moss = AllterraTheme.categorical.wallet

    AllterraCard(
        modifier = Modifier.fillMaxWidth().allterraClickable { onClick() },
        backgroundColor = AllterraTheme.colors.surface,
        borderColor = AllterraTheme.colors.line
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(moss.soft, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.category.icon(),
                    contentDescription = null,
                    tint = moss.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Column(
                modifier = Modifier.padding(start = 14.dp).weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = AllterraTheme.typography.bodyStrong,
                    color = AllterraTheme.colors.ink
                )
                Text(
                    text = item.subTitle,
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted
                )
            }

            if (item.isOfflineAvailable) {
                Column(horizontalAlignment = Alignment.End) {
                    AllterraChip(text = item.status, categorical = moss)
                    Spacer(modifier = Modifier.height(6.dp))
                    Icon(
                        Icons.Outlined.FileDownloadDone,
                        contentDescription = null,
                        tint = moss.color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
