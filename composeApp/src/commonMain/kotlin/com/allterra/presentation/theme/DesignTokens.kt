package com.allterra.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.manrope_regular
import allterra.composeapp.generated.resources.manrope_medium
import allterra.composeapp.generated.resources.manrope_semibold
import allterra.composeapp.generated.resources.manrope_bold
import allterra.composeapp.generated.resources.spacegrotesk_semibold
import allterra.composeapp.generated.resources.jetbrainsmono_medium
import androidx.compose.runtime.Composable

@Immutable
data class AllterraColors(
    val bg: Color,
    val bgSub: Color,
    val surface: Color,
    val surface2: Color,
    val ink: Color,
    val ink2: Color,
    val muted: Color,
    val muted2: Color,
    val line: Color,
    val line2: Color,
    val moss: Color,
    val moss2: Color,
    val mossSoft: Color,
    val terra: Color,
    val terraSoft: Color,
    val ochre: Color,
    val ochreSoft: Color,
    val sky: Color,
    val skySoft: Color,
    val good: Color,
    val warn: Color,
    val bad: Color,
    val isLight: Boolean
)

@Immutable
data class CategoricalColor(
    val color: Color,
    val color2: Color,
    val soft: Color,
    val ink: Color
)

@Immutable
data class AllterraCategoricalColors(
    val wallet: CategoricalColor,
    val route: CategoricalColor,
    val gear: CategoricalColor,
    val social: CategoricalColor
)

val LightColors = AllterraColors(
    bg = Color(0xFFFAF8F3),
    bgSub = Color(0xFFF1EDE4),
    surface = Color(0xFFFFFFFF),
    surface2 = Color(0xFFF7F4EC),
    ink = Color(0xFF1C1D1A),
    ink2 = Color(0xFF3A3C36),
    muted = Color(0xFF6B6D63),
    muted2 = Color(0xFF9A9B91),
    line = Color(0x141C1D1A),
    line2 = Color(0x241C1D1A),
    moss = Color(0xFF4A6B3A),
    moss2 = Color(0xFF5D8048),
    mossSoft = Color(0xFFE6EDE0),
    terra = Color(0xFFB85A35),
    terraSoft = Color(0xFFF5E2D6),
    ochre = Color(0xFFC89B3C),
    ochreSoft = Color(0xFFF6EAD0),
    sky = Color(0xFF4D7589),
    skySoft = Color(0xFFDDE7EC),
    good = Color(0xFF4A6B3A),
    warn = Color(0xFFC89B3C),
    bad = Color(0xFFB85A35),
    isLight = true
)

val DarkColors = AllterraColors(
    bg = Color(0xFF14150F),
    bgSub = Color(0xFF1C1D17),
    surface = Color(0xFF1F201A),
    surface2 = Color(0xFF262720),
    ink = Color(0xFFF4F2EA),
    ink2 = Color(0xFFD8D6CD),
    muted = Color(0xFF9A9B91),
    muted2 = Color(0xFF6B6D63),
    line = Color(0x14F4F2EA),
    line2 = Color(0x24F4F2EA),
    moss = Color(0xFF88A875),
    moss2 = Color(0xFFA3BF8E),
    mossSoft = Color(0x2E88A875),
    terra = Color(0xFFD97A55),
    terraSoft = Color(0x2ED97A55),
    ochre = Color(0xFFE0B765),
    ochreSoft = Color(0x2EE0B765),
    sky = Color(0xFF7EA4B7),
    skySoft = Color(0x2E7EA4B7),
    good = Color(0xFF88A875),
    warn = Color(0xFFE0B765),
    bad = Color(0xFFD97A55),
    isLight = false
)

val LightCategorical = AllterraCategoricalColors(
    wallet = CategoricalColor(Color(0xFF4A6B3A), Color(0xFF5D8048), Color(0xFFE6EDE0), Color(0xFF2F4524)),
    route = CategoricalColor(Color(0xFFB85A35), Color(0xFFD97A55), Color(0xFFF5E2D6), Color(0xFF7A3A1F)),
    gear = CategoricalColor(Color(0xFFC89B3C), Color(0xFFE0B765), Color(0xFFF6EAD0), Color(0xFF7A5E22)),
    social = CategoricalColor(Color(0xFF4D7589), Color(0xFF7EA4B7), Color(0xFFDDE7EC), Color(0xFF2D4A59))
)

val DarkCategorical = AllterraCategoricalColors(
    wallet = CategoricalColor(Color(0xFF88A875), Color(0xFFA3BF8E), Color(0x2E88A875), Color(0xFFB8D0A3)),
    route = CategoricalColor(Color(0xFFD97A55), Color(0xFFE89876), Color(0x2ED97A55), Color(0xFFF0A886)),
    gear = CategoricalColor(Color(0xFFE0B765), Color(0xFFEBCD89), Color(0x2EE0B765), Color(0xFFECD29A)),
    social = CategoricalColor(Color(0xFF7EA4B7), Color(0xFFA0BFCE), Color(0x2E7EA4B7), Color(0xFFB1CAD6))
)

@Immutable
data class AllterraTypography(
    val displayXL: TextStyle,
    val displayL: TextStyle,
    val displayM: TextStyle,
    val title: TextStyle,
    val body: TextStyle,
    val bodyStrong: TextStyle,
    val small: TextStyle,
    val smallStrong: TextStyle,
    val caption: TextStyle,
    val tab: TextStyle,
    val mono: TextStyle
)

@Composable
fun allterraTypography(): AllterraTypography {
    val sans = FontFamily(
        Font(Res.font.manrope_regular, FontWeight.Normal),
        Font(Res.font.manrope_medium, FontWeight.Medium),
        Font(Res.font.manrope_semibold, FontWeight.SemiBold),
        Font(Res.font.manrope_bold, FontWeight.Bold)
    )
    val display = FontFamily(
        Font(Res.font.spacegrotesk_semibold, FontWeight.SemiBold)
    )
    val mono = FontFamily(
        Font(Res.font.jetbrainsmono_medium, FontWeight.Medium)
    )

    return AllterraTypography(
        displayXL = TextStyle(fontFamily = display, fontSize = 32.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.025).sp, lineHeight = 33.6.sp),
        displayL = TextStyle(fontFamily = display, fontSize = 26.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.02).sp, lineHeight = 28.6.sp),
        displayM = TextStyle(fontFamily = display, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.02).sp, lineHeight = 26.4.sp),
        title = TextStyle(fontFamily = sans, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.01).sp, lineHeight = 22.5.sp),
        body = TextStyle(fontFamily = sans, fontSize = 15.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.005).sp, lineHeight = 21.75.sp),
        bodyStrong = TextStyle(fontFamily = sans, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.005).sp, lineHeight = 21.75.sp),
        small = TextStyle(fontFamily = sans, fontSize = 13.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.sp, lineHeight = 18.2.sp),
        smallStrong = TextStyle(fontFamily = sans, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp, lineHeight = 18.2.sp),
        caption = TextStyle(fontFamily = sans, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.44.sp, lineHeight = 14.3.sp),
        tab = TextStyle(fontFamily = sans, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp, lineHeight = 12.sp),
        mono = TextStyle(fontFamily = mono, fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.sp, lineHeight = 18.2.sp)
    )
}

@Immutable
data class AllterraRadius(
    val xs: Dp = 6.dp,
    val sm: Dp = 10.dp,
    val md: Dp = 14.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 28.dp,
    val btn: Dp = 12.dp,
    val phoneScreen: Dp = 48.dp,
    val tabbar: Dp = 22.dp,
    val icon: Dp = 12.dp,
    val sheetTop: Dp = 24.dp
)

@Immutable
data class AllterraSpacing(
    val s0: Dp = 0.dp,
    val s1: Dp = 4.dp,
    val s2: Dp = 8.dp,
    val s3: Dp = 12.dp,
    val s4: Dp = 16.dp,
    val s5: Dp = 20.dp,
    val s6: Dp = 24.dp,
    val s7: Dp = 28.dp,
    val s8: Dp = 32.dp,
    val s10: Dp = 40.dp,
    val s12: Dp = 48.dp,
    val screenPaddingX: Dp = 18.dp,
    val cardPadding: Dp = 14.dp,
    val tabbarMargin: Dp = 12.dp,
    val sectionGap: Dp = 18.dp
)

@Immutable
data class AllterraElevation(
    val sh1: String,
    val sh2: String,
    val sh3: String
)

val LightElevation = AllterraElevation(
    sh1 = "sh1_light", // Placeholder for elevation implementation
    sh2 = "sh2_light",
    sh3 = "sh3_light"
)

val DarkElevation = AllterraElevation(
    sh1 = "sh1_dark",
    sh2 = "sh2_dark",
    sh3 = "sh3_dark"
)

val LocalAllterraColors = staticCompositionLocalOf<AllterraColors> {
    error("No AllterraColors provided")
}

val LocalAllterraCategoricalColors = staticCompositionLocalOf<AllterraCategoricalColors> {
    error("No AllterraCategoricalColors provided")
}

val LocalAllterraTypography = staticCompositionLocalOf<AllterraTypography> {
    error("No AllterraTypography provided")
}

val LocalAllterraRadius = staticCompositionLocalOf { AllterraRadius() }
val LocalAllterraSpacing = staticCompositionLocalOf { AllterraSpacing() }
val LocalAllterraElevation = staticCompositionLocalOf<AllterraElevation> {
    error("No AllterraElevation provided")
}
