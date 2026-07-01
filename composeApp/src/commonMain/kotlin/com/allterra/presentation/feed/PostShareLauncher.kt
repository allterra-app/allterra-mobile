package com.allterra.presentation.feed

import androidx.compose.runtime.Composable

interface PostShareLauncher {
    fun share(title: String, body: String)
}

@Composable
expect fun rememberPostShareLauncher(): PostShareLauncher
