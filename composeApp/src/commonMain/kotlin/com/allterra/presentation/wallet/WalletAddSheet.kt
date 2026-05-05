package com.allterra.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.AllterraIconButton
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

    Column {
        Text(
            text = strings.walletAddTitle,
            style = AllterraTheme.typography.title,
            color = AllterraTheme.colors.ink,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ImportOption(icon = Icons.Outlined.PictureAsPdf, label = strings.walletImportPdf, onClick = onImportPDF)
                ImportOption(icon = Icons.Outlined.PhotoLibrary, label = strings.walletImportPhoto, onClick = onImportPhoto)
                ImportOption(icon = Icons.Outlined.Email, label = strings.walletImportEmail, onClick = onImportEmail)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ImportOption(icon = Icons.Outlined.QrCodeScanner, label = strings.walletImportScan, onClick = onScan)
                ImportOption(icon = Icons.Outlined.EditNote, label = strings.walletImportManual, onClick = onManual)
                ImportOption(icon = Icons.Outlined.AccountBalanceWallet, label = strings.walletImportPass, onClick = onWalletPass)
            }
        }
    }
}

@Composable
private fun ImportOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AllterraIconButton(
            onClick = onClick,
            modifier = Modifier.size(60.dp)
        ) {
            Icon(icon, contentDescription = null, tint = AllterraTheme.colors.moss, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = AllterraTheme.typography.tab, color = AllterraTheme.colors.muted)
    }
}
