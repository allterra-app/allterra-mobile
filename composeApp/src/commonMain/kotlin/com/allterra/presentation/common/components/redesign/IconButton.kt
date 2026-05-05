package com.allterra.presentation.common.components.redesign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun AllterraIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(AllterraTheme.colors.surface, RoundedCornerShape(12.dp))
            .border(1.dp, AllterraTheme.colors.line2, RoundedCornerShape(12.dp))
            .allterraClickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
