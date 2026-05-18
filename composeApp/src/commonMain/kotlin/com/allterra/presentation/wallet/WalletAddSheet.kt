package com.allterra.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun WalletAddSheet(
    onImportPDF: () -> Unit,
    onImportPhoto: () -> Unit,
    onImportEmail: () -> Unit,
    onScan: () -> Unit,
    onManual: () -> Unit,
    onWalletPass: () -> Unit,
) {
    val strings = appStrings()
    val moss = AllterraTheme.categorical.wallet

    val sources = listOf(
        SourceData(strings.walletImportPdf, AllterraIcons.Wallet, onImportPDF),
        SourceData(strings.walletImportPhoto, AllterraIcons.Map, onImportPhoto),
        SourceData(strings.walletImportEmail, AllterraIcons.Sharing, onImportEmail),
        SourceData(strings.walletImportScan, AllterraIcons.Route, onScan),
        SourceData(strings.walletImportPass, AllterraIcons.Ticket, onWalletPass),
        SourceData(strings.walletImportManual, AllterraIcons.Home, onManual),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AllterraTheme.spacing.screenPaddingX)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = strings.walletAddTitle,
            style = AllterraTheme.typography.title,
            color = AllterraTheme.colors.ink,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3),
            verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3),
            modifier = Modifier.height(240.dp)
        ) {
            items(sources) { source ->
                SourceTile(source, moss.soft, moss.color)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Supported: Booking, Airbnb, PKP, LOT, Lufthansa", // TODO: Localize or move to constants
            style = AllterraTheme.typography.small,
            color = AllterraTheme.colors.muted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SourceTile(data: SourceData, backgroundColor: androidx.compose.ui.graphics.Color, iconColor: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(AllterraTheme.radius.md))
            .background(backgroundColor)
            .allterraClickable { data.onClick() }
            .padding(AllterraTheme.spacing.s2),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.label,
                style = AllterraTheme.typography.caption,
                color = AllterraTheme.colors.ink,
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class SourceData(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)
