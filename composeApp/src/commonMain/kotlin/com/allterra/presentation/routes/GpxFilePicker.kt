package com.allterra.presentation.routes

import androidx.compose.runtime.Composable

interface GpxFilePicker {
    fun launch()
}

@Composable
expect fun rememberGpxFilePicker(
    onFileSelected: (fileName: String, contentType: String, fileBytes: ByteArray) -> Unit,
    onFileReadError: () -> Unit,
): GpxFilePicker
