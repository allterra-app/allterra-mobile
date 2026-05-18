package com.allterra.presentation.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.model.GeoPoint
import com.allterra.presentation.theme.AllterraTheme
import kotlin.math.min

@Composable
fun RoutePreviewCanvas(
    points: List<GeoPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Unspecified,
) {
    val terra = AllterraTheme.categorical.route
    val finalLineColor = if (lineColor == Color.Unspecified) terra.color else lineColor
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AllterraTheme.radius.sm))
            .background(terra.soft)
            .drawBehind {
                if (points.size >= 2) {
                    val minLat = points.minOfOrNull { it.lat } ?: 0.0
                    val maxLat = points.maxOfOrNull { it.lat } ?: 1.0
                    val minLon = points.minOfOrNull { it.lon } ?: 0.0
                    val maxLon = points.maxOfOrNull { it.lon } ?: 1.0

                    val latRange = (maxLat - minLat).takeIf { it > 0.0 } ?: 1.0
                    val lonRange = (maxLon - minLon).takeIf { it > 0.0 } ?: 1.0
                    val pad = 8f
                    val strokeWidth = 3.5f
                    
                    val contentWidth = (size.width - pad * 2).coerceAtLeast(1f)
                    val contentHeight = (size.height - pad * 2).coerceAtLeast(1f)
                    val scale = min(
                        contentWidth / lonRange.toFloat(),
                        contentHeight / latRange.toFloat(),
                    ).coerceAtLeast(1e-3f)
                    
                    val trackWidth = lonRange.toFloat() * scale
                    val trackHeight = latRange.toFloat() * scale
                    val offsetX = ((size.width - trackWidth) / 2f).coerceAtLeast(pad)
                    val offsetY = ((size.height - trackHeight) / 2f).coerceAtLeast(pad)

                    val path = Path()
                    points.forEachIndexed { index, point ->
                        val x = ((point.lon - minLon).toFloat() * scale) + offsetX
                        val y = trackHeight - ((point.lat - minLat).toFloat() * scale) + offsetY
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }

                    drawPath(
                        path = path,
                        color = finalLineColor,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (points.size < 2) {
            Icon(
                imageVector = AllterraIcons.Route,
                contentDescription = null,
                tint = terra.color.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
