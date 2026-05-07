package com.allterra.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun AllterraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    category: AllterraCategory = AllterraCategory.Neutral,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val categorical = if (darkTheme) DarkCategorical else LightCategorical
    val typography = allterraTypography()
    val elevation = if (darkTheme) DarkElevation else LightElevation

    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.moss,
            onPrimary = colors.ink,
            secondary = colors.terra,
            surface = colors.bg,
            onSurface = colors.ink,
            background = colors.bg,
            onBackground = colors.ink
        )
    } else {
        lightColorScheme(
            primary = colors.moss,
            onPrimary = androidx.compose.ui.graphics.Color.White,
            secondary = colors.terra,
            surface = colors.bg,
            onSurface = colors.ink,
            background = colors.bg,
            onBackground = colors.ink
        )
    }

    CompositionLocalProvider(
        LocalAllterraColors provides colors,
        LocalAllterraCategoricalColors provides categorical,
        LocalAllterraCategory provides category,
        LocalAllterraTypography provides typography,
        LocalAllterraRadius provides AllterraRadius(),
        LocalAllterraSpacing provides AllterraSpacing(),
        LocalAllterraElevation provides elevation
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = Typography(
                bodyLarge = typography.body,
                titleLarge = typography.title,
                labelLarge = typography.caption
            ),
            content = content
        )
    }
}

object AllterraTheme {
    val colors: AllterraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAllterraColors.current

    val categorical: AllterraCategoricalColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAllterraCategoricalColors.current

    val category: AllterraCategory
        @Composable
        @ReadOnlyComposable
        get() = LocalAllterraCategory.current

    val currentCategoryColors: CategoricalColor
        @Composable
        @ReadOnlyComposable
        get() = when (category) {
            AllterraCategory.Wallet -> categorical.wallet
            AllterraCategory.Route -> categorical.route
            AllterraCategory.Gear -> categorical.gear
            AllterraCategory.Social -> categorical.social
            AllterraCategory.Neutral -> CategoricalColor(colors.ink, colors.ink2, colors.line, colors.ink)
        }

    val typography: AllterraTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAllterraTypography.current

    val radius: AllterraRadius
        @Composable
        @ReadOnlyComposable
        get() = LocalAllterraRadius.current

    val spacing: AllterraSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAllterraSpacing.current

    val elevation: AllterraElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalAllterraElevation.current
}

