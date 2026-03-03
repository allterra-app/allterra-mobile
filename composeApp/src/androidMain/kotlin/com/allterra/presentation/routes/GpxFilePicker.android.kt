package com.allterra.presentation.routes

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private class AndroidGpxFilePicker(
    private val launchPicker: () -> Unit,
) : GpxFilePicker {
    override fun launch() {
        launchPicker()
    }
}

@Composable
actual fun rememberGpxFilePicker(
    onFileSelected: (fileName: String, contentType: String, fileBytes: ByteArray) -> Unit,
    onFileReadError: () -> Unit,
): GpxFilePicker {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val fileBytes = runCatching {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        }.getOrNull()

        if (fileBytes == null || fileBytes.isEmpty()) {
            onFileReadError()
            return@rememberLauncherForActivityResult
        }

        val fileName = context.contentResolver.queryFileName(uri) ?: "track.gpx"
        val contentType = context.contentResolver.getType(uri)
            ?: guessContentType(fileName)
        onFileSelected(fileName, contentType, fileBytes)
    }

    return remember(launcher) {
        AndroidGpxFilePicker(
            launchPicker = {
                launcher.launch(arrayOf("application/gpx+xml", "application/xml", "text/xml", "*/*"))
            },
        )
    }
}

private fun ContentResolver.queryFileName(uri: Uri): String? {
    val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
    return query(uri, projection, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
    }
}

private fun guessContentType(fileName: String): String {
    return if (fileName.lowercase().endsWith(".gpx")) {
        "application/gpx+xml"
    } else {
        "application/octet-stream"
    }
}
