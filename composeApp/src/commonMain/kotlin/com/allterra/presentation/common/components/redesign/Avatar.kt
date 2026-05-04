package com.allterra.presentation.common.components.redesign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme

enum class AllterraAvatarSize(val dp: Dp) {
    Small(26.dp), Medium(36.dp), Large(88.dp)
}

@Composable
fun AllterraAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: AllterraAvatarSize = AllterraAvatarSize.Medium,
    backgroundColor: Color = AllterraTheme.colors.surface2,
    contentColor: Color = AllterraTheme.colors.ink
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, AllterraTheme.colors.surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.uppercase(),
            style = if (size == AllterraAvatarSize.Large) AllterraTheme.typography.displayL else AllterraTheme.typography.bodyStrong,
            color = contentColor
        )
    }
}
