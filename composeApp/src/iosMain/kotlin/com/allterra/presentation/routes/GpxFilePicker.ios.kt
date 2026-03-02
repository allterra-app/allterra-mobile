package com.allterra.presentation.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private class UnsupportedGpxFilePicker(
    private val onFileReadError: () -> Unit,
) : GpxFilePicker {
    override fun launch() {
        onFileReadError()
    }
}

@Composable
actual fun rememberGpxFilePicker(
    onFileSelected: (fileName: String, content: String) -> Unit,
    onFileReadError: () -> Unit,
): GpxFilePicker {
    return remember(onFileReadError) {
        UnsupportedGpxFilePicker(onFileReadError = onFileReadError)
    }
}
