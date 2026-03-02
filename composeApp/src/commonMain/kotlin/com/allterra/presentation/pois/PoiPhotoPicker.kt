package com.allterra.presentation.pois

import androidx.compose.runtime.Composable

interface PoiPhotoPicker {
    fun launch(maxSelection: Int)
}

@Composable
expect fun rememberPoiPhotoPicker(
    onPhotosPicked: (List<String>) -> Unit,
    onReadError: () -> Unit,
): PoiPhotoPicker
