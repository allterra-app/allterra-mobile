package com.allterra.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.localization.appStrings

@Composable
fun MapScreen() {
    val strings = appStrings()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A7DAF), Color(0xFFC2C7CC)),
                )
            ),
    ) {
        Icon(
            imageVector = Icons.Outlined.Map,
            contentDescription = strings.mapTitle,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 20.dp),
        )

        Text(
            text = strings.mapTitle,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 56.dp),
        )
    }
}
