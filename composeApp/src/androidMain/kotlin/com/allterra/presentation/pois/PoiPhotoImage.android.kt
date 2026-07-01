package com.allterra.presentation.pois

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.allterra.data.local.TokenStorage
import com.allterra.network.media.toAbsoluteMediaUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

@Composable
actual fun PoiPhotoImage(
    source: String,
    modifier: Modifier,
) {
    val tokenStorage = koinInject<TokenStorage>()
    val bitmap by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = source) {
        val accessToken = runCatching { tokenStorage.getAccessToken() }.getOrNull()
        value = loadImage(source, accessToken)
    }
    val imageBitmap = bitmap
    if (imageBitmap == null) {
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
        return
    }

    Image(
        bitmap = imageBitmap.asImageBitmap(),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}

private suspend fun loadImage(source: String, accessToken: String?): android.graphics.Bitmap? = withContext(Dispatchers.IO) {
    decodeImage(source, accessToken)
}

private fun decodeImage(source: String, accessToken: String?): android.graphics.Bitmap? = runCatching {
    when {
        source.startsWith("content://") || source.startsWith("file://") -> {
            val uri = Uri.parse(source)
            val context = com.allterra.data.local.AndroidAppContextHolder.appContext
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
        source.startsWith("http://") || source.startsWith("https://") -> {
            loadRemoteBitmap(source, accessToken)
        }
        source.startsWith("/") -> {
            val file = File(source)
            if (file.exists() && file.isFile) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                loadRemoteBitmap(toAbsoluteMediaUrl(source), accessToken)
            }
        }
        else -> {
            val file = File(source)
            if (!file.exists()) null else BitmapFactory.decodeFile(file.absolutePath)
        }
    }
}.getOrNull()

private fun loadRemoteBitmap(url: String, accessToken: String?): android.graphics.Bitmap? {
    val connection = URL(url).openConnection() as? HttpURLConnection ?: return null
    connection.connectTimeout = 10_000
    connection.readTimeout = 10_000
    connection.instanceFollowRedirects = true
    if (!accessToken.isNullOrBlank()) {
        connection.setRequestProperty("Authorization", "Bearer $accessToken")
    }
    return try {
        if (connection.responseCode !in 200..299) return null
        connection.inputStream.use { stream -> BitmapFactory.decodeStream(stream) }
    } finally {
        connection.disconnect()
    }
}
