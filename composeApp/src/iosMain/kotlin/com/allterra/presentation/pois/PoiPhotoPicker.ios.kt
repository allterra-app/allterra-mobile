package com.allterra.presentation.pois

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private class UnsupportedPoiPhotoPicker(
    private val onReadError: () -> Unit,
) : PoiPhotoPicker {
    override fun launch(maxSelection: Int) {
        onReadError()
    }
}

@Composable
actual fun rememberPoiPhotoPicker(
    onPhotosPicked: (List<String>) -> Unit,
    onReadError: () -> Unit,
): PoiPhotoPicker {
    return remember(onReadError) {
        UnsupportedPoiPhotoPicker(onReadError = onReadError)
    }
}
