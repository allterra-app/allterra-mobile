package com.allterra.presentation.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.posix.memcpy

private class IosGpxFilePicker(
    private val onFileSelected: (fileName: String, contentType: String, fileBytes: ByteArray) -> Unit,
    private val onFileReadError: () -> Unit,
) : GpxFilePicker {

    private val delegate = IosGpxDocumentDelegate(
        onFileSelected = onFileSelected,
        onFileReadError = onFileReadError,
    )
    private var pickerController: UIDocumentPickerViewController? = null

    override fun launch() {
        val presenter = topViewController() ?: run {
            onFileReadError()
            return
        }

        val picker = UIDocumentPickerViewController(
            documentTypes = listOf("com.topografix.gpx", "public.xml", "public.data"),
            inMode = UIDocumentPickerMode.UIDocumentPickerModeImport,
        )
        picker.delegate = delegate
        picker.allowsMultipleSelection = false
        pickerController = picker
        delegate.onFinished = { pickerController = null }

        presenter.presentViewController(picker, animated = true, completion = null)
    }

    private fun topViewController(): UIViewController? {
        var top = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return null
        while (top.presentedViewController != null) {
            top = top.presentedViewController ?: break
        }
        return top
    }
}

private class IosGpxDocumentDelegate(
    private val onFileSelected: (fileName: String, contentType: String, fileBytes: ByteArray) -> Unit,
    private val onFileReadError: () -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {

    var onFinished: () -> Unit = {}

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: run {
            onFileReadError()
            onFinished()
            return
        }

        val hasAccess = runCatching { url.startAccessingSecurityScopedResource() }.getOrDefault(false)
        val path = url.path
        val bytes = if (path.isNullOrBlank()) {
            null
        } else {
            NSFileManager.defaultManager.contentsAtPath(path)?.toByteArray()
        }
        if (hasAccess) {
            url.stopAccessingSecurityScopedResource()
        }

        if (bytes == null || bytes.isEmpty()) {
            onFileReadError()
            onFinished()
            return
        }

        val fileName = url.lastPathComponent ?: "track.gpx"
        val contentType = if (fileName.lowercase().endsWith(".gpx")) {
            "application/gpx+xml"
        } else {
            "application/octet-stream"
        }

        onFileSelected(fileName, contentType, bytes)
        onFinished()
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onFinished()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)

    val out = ByteArray(size)
    out.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
    return out
}

@Composable
actual fun rememberGpxFilePicker(
    onFileSelected: (fileName: String, contentType: String, fileBytes: ByteArray) -> Unit,
    onFileReadError: () -> Unit,
): GpxFilePicker {
    return remember(onFileSelected, onFileReadError) {
        IosGpxFilePicker(
            onFileSelected = onFileSelected,
            onFileReadError = onFileReadError,
        )
    }
}
