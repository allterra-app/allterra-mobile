package com.allterra.presentation.common.components.navigation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AllterraIcons {
    val Home = ImageVector.Builder(
        name = "Home",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(4f, 11f)
        lineTo(12f, 4f)
        lineTo(20f, 7f)
        verticalLineTo(19f)
        curveTo(20f, 19.55f, 19.55f, 20f, 19f, 20f)
        horizontalLineTo(16f)
        verticalLineTo(14f)
        horizontalLineTo(8f)
        verticalLineTo(20f)
        horizontalLineTo(5f)
        curveTo(4.45f, 20f, 4f, 19.55f, 4f, 19f)
        verticalLineTo(11f)
        close()
    }.build()

    val Map = ImageVector.Builder(
        name = "Map",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(9f, 4f)
        lineTo(4f, 6f)
        verticalLineTo(20f)
        lineTo(9f, 18f)
        lineTo(15f, 20f)
        lineTo(20f, 18f)
        verticalLineTo(4f)
        lineTo(15f, 6f)
        lineTo(9f, 4f)
        close()
        moveTo(9f, 4f)
        verticalLineTo(18f)
        moveTo(15f, 6f)
        verticalLineTo(20f)
    }.build()

    val Route = ImageVector.Builder(
        name = "Route",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        // Circle 1
        moveTo(6f, 6f)
        moveToRelative(-2.5f, 0f)
        arcTo(2.5f, 2.5f, 0f, true, true, 8.5f, 6f)
        arcTo(2.5f, 2.5f, 0f, true, true, 3.5f, 6f)
        // Circle 2
        moveTo(18f, 18f)
        moveToRelative(-2.5f, 0f)
        arcTo(2.5f, 2.5f, 0f, true, true, 20.5f, 18f)
        arcTo(2.5f, 2.5f, 0f, true, true, 15.5f, 18f)
        // Path
        moveTo(6f, 8.5f)
        verticalLineTo(11.5f)
        curveTo(6f, 13.71f, 7.79f, 15.5f, 10f, 15.5f)
        horizontalLineTo(14f)
        curveTo(16.21f, 15.5f, 18f, 17.29f, 18f, 19.5f)
    }.build()

    val Wallet = ImageVector.Builder(
        name = "Wallet",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(5f, 4f)
        horizontalLineTo(16f)
        curveTo(17.66f, 4f, 19f, 5.34f, 19f, 7f)
        verticalLineTo(20f)
        horizontalLineTo(8f)
        curveTo(6.34f, 20f, 5f, 18.66f, 5f, 17f)
        verticalLineTo(4f)
        close()
        moveTo(5f, 17f)
        horizontalLineTo(19f)
    }.build()

    val Feed = ImageVector.Builder(
        name = "Feed",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        // Custom simple feed icon (two stacked rectangles/lines)
        moveTo(4f, 6f)
        horizontalLineTo(20f)
        moveTo(4f, 12f)
        horizontalLineTo(20f)
        moveTo(4f, 18f)
        horizontalLineTo(14f)
    }.build()
}
