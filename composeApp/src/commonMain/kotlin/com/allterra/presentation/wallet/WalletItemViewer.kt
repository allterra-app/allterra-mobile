package com.allterra.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun WalletItemViewer(
    item: WalletItem,
    onOpenOriginal: () -> Unit,
    onShare: () -> Unit
) {
    val strings = appStrings()
    val moss = AllterraTheme.categorical.wallet

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AllterraTheme.spacing.screenPaddingX)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(AllterraTheme.radius.lg))
                .background(moss.color),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = item.type.icon(),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.title,
                    style = AllterraTheme.typography.title,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QR / Barcode Placeholder
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(AllterraTheme.radius.md))
                .background(AllterraTheme.colors.surface2)
                .allterraClickable { /* TODO: Full screen QR */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.QrCode2,
                contentDescription = "QR Code",
                modifier = Modifier.size(160.dp),
                tint = AllterraTheme.colors.ink
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Meta Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AllterraTheme.radius.md))
                .background(AllterraTheme.colors.surface2)
                .padding(AllterraTheme.spacing.s4),
            verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s4)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MetaItem(strings.dateLabel, item.date, Modifier.weight(1f))
                MetaItem("Category", item.type.name, Modifier.weight(1f))
            }
            if (item.tripName != null) {
                MetaItem("Linked Trip", item.tripName, Modifier.fillMaxWidth())
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Actions
        Column(verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
            AllterraButton(
                text = strings.walletOpenPdf,
                variant = AllterraButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.OpenInNew, null, modifier = Modifier.size(20.dp)) },
                onClick = onOpenOriginal
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
                AllterraButton(
                    text = strings.shareAction,
                    variant = AllterraButtonVariant.Secondary,
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Outlined.Share, null, modifier = Modifier.size(20.dp)) },
                    onClick = onShare
                )
                AllterraButton(
                    text = "Add to Apple Wallet", // TODO: Platform dependent
                    variant = AllterraButtonVariant.Secondary,
                    modifier = Modifier.weight(1.5f),
                    leadingIcon = { Icon(Icons.Outlined.AddCard, null, modifier = Modifier.size(20.dp)) },
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun MetaItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label.uppercase(), style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted2)
        Text(text = value, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
    }
}
