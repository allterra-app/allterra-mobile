package com.allterra.presentation.common.components.redesign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun AllterraInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) AllterraTheme.colors.moss else AllterraTheme.colors.line2

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = AllterraTheme.typography.body.copy(color = AllterraTheme.colors.ink),
        cursorBrush = SolidColor(AllterraTheme.colors.moss),
        modifier = modifier
            .fillMaxWidth()
            .height(if (singleLine) 48.dp else 96.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .clip(RoundedCornerShape(AllterraTheme.radius.btn))
            .background(AllterraTheme.colors.surface)
            .border(1.dp, borderColor, RoundedCornerShape(AllterraTheme.radius.btn))
            .padding(horizontal = AllterraTheme.spacing.s4),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = AllterraTheme.typography.body,
                        color = AllterraTheme.colors.muted2
                    )
                }
                innerTextField()
            }
        }
    )
}
