package com.allterra.presentation.feed

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.allterra.data.local.AndroidAppContextHolder

private class AndroidPostShareLauncher : PostShareLauncher {
    override fun share(title: String, body: String) {
        val context = AndroidAppContextHolder.appContext
        val text = listOf(title.trim(), body.trim())
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n\n")
            .ifBlank { title.ifBlank { body } }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title.ifBlank { "Allterra" })
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

@Composable
actual fun rememberPostShareLauncher(): PostShareLauncher {
    return remember { AndroidPostShareLauncher() }
}
