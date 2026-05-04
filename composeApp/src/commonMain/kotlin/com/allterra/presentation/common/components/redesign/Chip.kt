package com.allterra.presentation.common.components.redesign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.presentation.theme.CategoricalColor

@Composable
fun AllterraChip(
    text: String,
    modifier: Modifier = Modifier,
    categorical: CategoricalColor? = null,
    backgroundColor: Color = AllterraTheme.colors.bgSub,
    contentColor: Color = AllterraTheme.colors.ink
) {
    val bg = categorical?.soft ?: backgroundColor
    val fg = categorical?.color ?: contentColor

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AllterraTheme.typography.smallStrong,
            color = fg
        )
    }
}
