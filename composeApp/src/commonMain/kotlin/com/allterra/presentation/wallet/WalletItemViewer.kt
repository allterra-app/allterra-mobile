package com.allterra.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraButtonVariant
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun WalletItemViewer(
    item: WalletItem,
    onOpenOriginal: () -> Unit,
    onShare: () -> Unit
) {
    val moss = AllterraTheme.categorical.wallet

    Column(modifier = Modifier.fillMaxWidth()) {
        // Hero Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(AllterraTheme.radius.lg))
                .background(moss.soft),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for QR Code
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) // Simulated QR
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = item.title, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
        Text(text = item.subTitle, style = AllterraTheme.typography.body, color = AllterraTheme.colors.muted)

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AllterraButton(
                text = "Open PDF",
                variant = AllterraButtonVariant.Primary,
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Outlined.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                onClick = onOpenOriginal
            )
            AllterraButton(
                text = "Share",
                variant = AllterraButtonVariant.Secondary,
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Outlined.Share, contentDescription = null, tint = AllterraTheme.colors.ink, modifier = Modifier.size(18.dp)) },
                onClick = onShare
            )
        }
    }
}
