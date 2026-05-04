package com.allterra.presentation.common.components.redesign

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.allterra.presentation.theme.AllterraTheme

fun Modifier.allterraClickable(
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by actualInteractionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 80)
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = actualInteractionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}
