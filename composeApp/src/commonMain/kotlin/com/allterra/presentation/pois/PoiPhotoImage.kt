package com.allterra.presentation.pois

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PoiPhotoImage(
    source: String,
    modifier: Modifier = Modifier,
)
