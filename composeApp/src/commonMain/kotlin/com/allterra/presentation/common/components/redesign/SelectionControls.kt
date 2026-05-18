package com.allterra.presentation.common.components.redesign

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun AllterraCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(7.dp)
    val background = if (checked) AllterraTheme.currentCategoryColors.color else AllterraTheme.colors.surface
    val border = if (checked) AllterraTheme.currentCategoryColors.color else AllterraTheme.colors.line2

    Box(
        modifier = modifier
            .size(22.dp)
            .clip(shape)
            .background(background.copy(alpha = if (enabled) 1f else 0.45f))
            .border(1.5.dp, border.copy(alpha = if (enabled) 1f else 0.45f), shape)
            .allterraClickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = if (AllterraTheme.colors.isLight) Color.White else AllterraTheme.colors.ink,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
fun AllterraToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 18.dp else 2.dp,
        animationSpec = tween(durationMillis = 180),
    )
    val background = if (checked) AllterraTheme.currentCategoryColors.color else AllterraTheme.colors.line2

    Box(
        modifier = modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background.copy(alpha = if (enabled) 1f else 0.45f))
            .allterraClickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(22.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White)
                .border(1.dp, AllterraTheme.colors.line, RoundedCornerShape(999.dp))
        )
    }
}
