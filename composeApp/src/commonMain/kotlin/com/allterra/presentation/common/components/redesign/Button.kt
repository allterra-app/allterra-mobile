package com.allterra.presentation.common.components.redesign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme

enum class AllterraButtonVariant {
    Primary, Secondary, Ghost, Terra
}

@Composable
fun AllterraButton(
    text: String,
    modifier: Modifier = Modifier,
    variant: AllterraButtonVariant = AllterraButtonVariant.Primary,
    enabled: Boolean = true,
    isSmall: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    val height = if (isSmall) 36.dp else 48.dp
    val backgroundColor = when (variant) {
        AllterraButtonVariant.Primary -> AllterraTheme.currentCategoryColors.color
        AllterraButtonVariant.Secondary -> AllterraTheme.colors.surface
        AllterraButtonVariant.Ghost -> Color.Transparent
        AllterraButtonVariant.Terra -> AllterraTheme.colors.terra
    }
    val contentColor = when (variant) {
        AllterraButtonVariant.Primary -> if (AllterraTheme.colors.isLight) Color.White else AllterraTheme.colors.ink
        AllterraButtonVariant.Secondary -> AllterraTheme.colors.ink
        AllterraButtonVariant.Ghost -> AllterraTheme.colors.ink
        AllterraButtonVariant.Terra -> Color.White
    }
    val borderColor = if (variant == AllterraButtonVariant.Secondary) AllterraTheme.colors.line2 else Color.Transparent

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(AllterraTheme.radius.btn))
            .background(backgroundColor)
            .then(
                if (borderColor != Color.Transparent) Modifier.border(1.dp, borderColor, RoundedCornerShape(AllterraTheme.radius.btn))
                else Modifier
            )
            .allterraClickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = AllterraTheme.spacing.s4),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = if (isSmall) AllterraTheme.typography.smallStrong else AllterraTheme.typography.bodyStrong,
                color = contentColor.copy(alpha = if (enabled) 1f else 0.5f)
            )
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingIcon()
            }
        }
    }
}
