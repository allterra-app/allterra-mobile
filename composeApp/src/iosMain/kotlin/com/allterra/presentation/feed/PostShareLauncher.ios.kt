package com.allterra.presentation.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSString
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

private class IosPostShareLauncher : PostShareLauncher {
    override fun share(title: String, body: String) {
        val presenter = topViewController() ?: return
        val text = listOf(title.trim(), body.trim())
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n\n")
            .ifBlank { title.ifBlank { body } }
        val controller = UIActivityViewController(
            activityItems = listOf(text as NSString),
            applicationActivities = null,
        )
        presenter.presentViewController(controller, animated = true, completion = null)
    }

    private fun topViewController(): UIViewController? {
        var top = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return null
        while (top.presentedViewController != null) {
            top = top.presentedViewController ?: break
        }
        return top
    }
}

@Composable
actual fun rememberPostShareLauncher(): PostShareLauncher {
    return remember { IosPostShareLauncher() }
}
