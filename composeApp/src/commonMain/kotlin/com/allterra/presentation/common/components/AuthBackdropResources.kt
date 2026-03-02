package com.allterra.presentation.common.components

import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.bg_login_1
import allterra.composeapp.generated.resources.bg_login_2
import org.jetbrains.compose.resources.DrawableResource

val authBackdropResources: List<DrawableResource> = listOf(
    Res.drawable.bg_login_1,
    Res.drawable.bg_login_2,
)

fun resolveBackdrop(index: Int): DrawableResource {
    val safeIndex = if (index < 0) 0 else index % authBackdropResources.size
    return authBackdropResources[safeIndex]
}
