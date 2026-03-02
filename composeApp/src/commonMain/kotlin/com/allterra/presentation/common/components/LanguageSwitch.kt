package com.allterra.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun LanguageSwitchLabel(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "$title ▲",
        modifier = modifier.clickable(onClick = onClick),
        color = Color(0xFF0E7E8A),
        textDecoration = TextDecoration.Underline,
    )
}
