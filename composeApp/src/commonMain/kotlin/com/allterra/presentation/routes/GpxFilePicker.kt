package com.allterra.presentation.routes

import androidx.compose.runtime.Composable

interface GpxFilePicker {
    fun launch()
}

@Composable
expect fun rememberGpxFilePicker(
    onFileSelected: (fileName: String, content: String) -> Unit,
    onFileReadError: () -> Unit,
): GpxFilePicker
