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
import com.allterra.presentation.common.components.redesign.AllterraCard
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun WalletItemViewer(
    item: WalletItem,
    onOpenOriginal: () -> Unit,
    onShare: () -> Unit
) {
    val moss = AllterraTheme.categorical.wallet
    val strings = appStrings()

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
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                FakeQrPattern()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = item.title, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
        Text(text = item.subTitle, style = AllterraTheme.typography.body, color = AllterraTheme.colors.muted)

        Spacer(modifier = Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            WalletMeta(strings.dateLabel, item.date, Modifier.weight(1f))
            WalletMeta(strings.timeLabel, item.time, Modifier.weight(1f))
            WalletMeta(strings.locationLabel, item.location, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AllterraButton(
                text = strings.walletOpenPdf,
                variant = AllterraButtonVariant.Primary,
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Outlined.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
                onClick = onOpenOriginal
            )
            AllterraButton(
                text = strings.shareAction,
                variant = AllterraButtonVariant.Secondary,
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Outlined.Share, contentDescription = null, tint = AllterraTheme.colors.ink, modifier = Modifier.size(18.dp)) },
                onClick = onShare
            )
        }
    }
}

@Composable
private fun WalletMeta(label: String, value: String, modifier: Modifier = Modifier) {
    AllterraCard(modifier = modifier, hasShadow = false, backgroundColor = AllterraTheme.colors.surface2) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = AllterraTheme.typography.smallStrong, color = AllterraTheme.colors.ink)
        }
    }
}

@Composable
private fun FakeQrPattern() {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(9) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(9) { column ->
                    val filled = row < 2 && column < 2 ||
                        row < 2 && column > 6 ||
                        row > 6 && column < 2 ||
                        (row * 7 + column * 5) % 4 == 0
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (filled) Color.Black else Color.White)
                    )
                }
            }
        }
    }
}
