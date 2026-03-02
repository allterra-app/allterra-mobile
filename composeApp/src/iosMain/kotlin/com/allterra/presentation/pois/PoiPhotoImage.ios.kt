package com.allterra.presentation.pois

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun PoiPhotoImage(
    source: String,
    modifier: Modifier,
) {
    Box(
        modifier = modifier.background(Color(0xFF295B68)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Photo,
            contentDescription = null,
            tint = Color(0xFFE7EEF2),
        )
    }
}
