package com.allterra.platform

import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.allterra.data.local.AndroidAppContextHolder
import java.io.File

actual object LocalFileAccess {
    actual fun readBytes(pathOrUri: String): ByteArray? {
        return runCatching {
            when {
                pathOrUri.startsWith("content://") || pathOrUri.startsWith("file://") -> {
                    val uri = Uri.parse(pathOrUri)
                    AndroidAppContextHolder.appContext.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }

                else -> File(pathOrUri).takeIf { it.exists() }?.readBytes()
            }
        }.getOrNull()
    }

    actual fun fileName(pathOrUri: String): String {
        return when {
            pathOrUri.startsWith("content://") || pathOrUri.startsWith("file://") -> {
                val uri = Uri.parse(pathOrUri)
                readDisplayName(uri)
                    ?: uri.lastPathSegment?.substringAfterLast('/')
                    ?: "file_${System.currentTimeMillis()}"
            }

            else -> File(pathOrUri).name.ifBlank { "file_${System.currentTimeMillis()}" }
        }
    }

    actual fun contentType(pathOrUri: String): String {
        if (pathOrUri.startsWith("content://") || pathOrUri.startsWith("file://")) {
            val uri = Uri.parse(pathOrUri)
            AndroidAppContextHolder.appContext.contentResolver.getType(uri)?.let { return it }
        }

        val extension = fileName(pathOrUri).substringAfterLast('.', "").lowercase()
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        return mimeType ?: "application/octet-stream"
    }

    private fun readDisplayName(uri: Uri): String? {
        val resolver = AndroidAppContextHolder.appContext.contentResolver
        return resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            val nameColumn = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameColumn >= 0 && cursor.moveToFirst()) {
                cursor.getString(nameColumn)
            } else {
                null
            }
        }
    }
}
