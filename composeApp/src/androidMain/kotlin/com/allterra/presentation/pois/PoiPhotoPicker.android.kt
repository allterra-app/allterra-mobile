package com.allterra.presentation.pois

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import kotlin.random.Random

private class AndroidPoiPhotoPicker(
    private val launchPicker: (Int) -> Unit,
) : PoiPhotoPicker {
    override fun launch(maxSelection: Int) {
        launchPicker(maxSelection)
    }
}

@Composable
actual fun rememberPoiPhotoPicker(
    onPhotosPicked: (List<String>) -> Unit,
    onReadError: () -> Unit,
): PoiPhotoPicker {
    var maxSelection by remember { mutableIntStateOf(1) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult

        val resolved = uris
            .take(maxSelection)
            .mapNotNull { uri -> resolvePickedPhotoToCache(uri) }

        if (resolved.isEmpty()) {
            onReadError()
            return@rememberLauncherForActivityResult
        }

        onPhotosPicked(resolved)
    }

    return remember(launcher) {
        AndroidPoiPhotoPicker { requestedMax ->
            maxSelection = requestedMax.coerceAtLeast(1)
            launcher.launch("image/*")
        }
    }
}

private fun resolvePickedPhotoToCache(uri: Uri): String? {
    return compressImageToCache(uri) ?: copyOriginalToCache(uri)
}

private fun compressImageToCache(uri: Uri): String? {
    val context = com.allterra.data.local.AndroidAppContextHolder.appContext
    return runCatching {
        val sourceBitmap = decodeBitmap(context, uri) ?: return null
        val resized = sourceBitmap.scaleToMax(MAX_DIMENSION)
        val outputDir = File(context.cacheDir, "poi_images").apply { mkdirs() }
        val outputFile = File(
            outputDir,
            "poi_${System.currentTimeMillis()}_${Random.nextInt(10_000)}.jpg",
        )
        FileOutputStream(outputFile).use { stream ->
            resized.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            stream.flush()
        }

        if (resized !== sourceBitmap) sourceBitmap.recycle()
        resized.recycle()
        outputFile.absolutePath
    }.getOrNull()
}

private fun copyOriginalToCache(uri: Uri): String? {
    val context = com.allterra.data.local.AndroidAppContextHolder.appContext
    return runCatching {
        val outputDir = File(context.cacheDir, "poi_images").apply { mkdirs() }
        val extension = detectExtension(context, uri)
        val outputFile = File(
            outputDir,
            "poi_${System.currentTimeMillis()}_${Random.nextInt(10_000)}.$extension",
        )
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
                output.flush()
            }
        } ?: return null
        outputFile.absolutePath
    }.getOrNull()
}

private fun detectExtension(context: Context, uri: Uri): String {
    val mimeType = context.contentResolver.getType(uri)
    val fromMime = mimeType?.substringAfterLast('/')?.lowercase()
        ?.takeIf { it.isNotBlank() }
        ?: MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    return fromMime?.takeIf { it.isNotBlank() } ?: "jpg"
}

private fun decodeBitmap(context: Context, uri: Uri): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, bounds)
    } ?: return null

    val options = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(
            srcWidth = bounds.outWidth,
            srcHeight = bounds.outHeight,
            reqWidth = MAX_DIMENSION,
            reqHeight = MAX_DIMENSION,
        )
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    return context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, options)
    }
}

private fun calculateInSampleSize(
    srcWidth: Int,
    srcHeight: Int,
    reqWidth: Int,
    reqHeight: Int,
): Int {
    var inSampleSize = 1
    if (srcHeight > reqHeight || srcWidth > reqWidth) {
        var halfHeight = srcHeight / 2
        var halfWidth = srcWidth / 2
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
            halfHeight /= 2
            halfWidth /= 2
        }
    }
    return inSampleSize.coerceAtLeast(1)
}

private fun Bitmap.scaleToMax(maxDimension: Int): Bitmap {
    if (width <= maxDimension && height <= maxDimension) return this

    val ratio = min(maxDimension.toFloat() / width.toFloat(), maxDimension.toFloat() / height.toFloat())
    val outWidth = (width * ratio).toInt().coerceAtLeast(1)
    val outHeight = (height * ratio).toInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(this, outWidth, outHeight, true)
}

private const val MAX_DIMENSION = 1600
private const val JPEG_QUALITY = 82
