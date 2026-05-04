package com.allterra.presentation.common.components.redesign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun AllterraCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = AllterraTheme.colors.surface,
    borderColor: Color = AllterraTheme.colors.line,
    hasShadow: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .then(
                if (hasShadow) Modifier.shadow(
                    elevation = 2.dp, // sh1 placeholder
                    shape = RoundedCornerShape(AllterraTheme.radius.md),
                    ambientColor = Color.Black.copy(alpha = 0.05f),
                    spotColor = Color.Black.copy(alpha = 0.04f)
                ) else Modifier
            )
            .clip(RoundedCornerShape(AllterraTheme.radius.md))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(AllterraTheme.radius.md))
            .padding(AllterraTheme.spacing.cardPadding)
    ) {
        content()
    }
}
