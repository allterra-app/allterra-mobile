package com.allterra.presentation.routes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.allterra.presentation.common.model.GeoPoint
import kotlin.math.min

@Composable
fun RoutePreviewCanvas(
    points: List<GeoPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFFFF6A00),
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8))
            .background(Color(0xFF1E7D84)),
    ) {
        if (points.size < 2) {
            Icon(
                imageVector = Icons.Outlined.Route,
                contentDescription = null,
                tint = Color(0xFFF2F7F9),
                modifier = Modifier.align(Alignment.Center),
            )
            return
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val minLat = points.minOf { it.lat }
            val maxLat = points.maxOf { it.lat }
            val minLon = points.minOf { it.lon }
            val maxLon = points.maxOf { it.lon }

            val latRange = (maxLat - minLat).takeIf { it > 0.0 } ?: 1.0
            val lonRange = (maxLon - minLon).takeIf { it > 0.0 } ?: 1.0
            val padding = 8f
            val strokeWidth = 3.5f
            val contentWidth = (size.width - padding * 2).coerceAtLeast(1f)
            val contentHeight = (size.height - padding * 2).coerceAtLeast(1f)
            val scale = min(
                contentWidth / lonRange.toFloat(),
                contentHeight / latRange.toFloat(),
            ).coerceAtLeast(1e-3f)
            val trackWidth = lonRange.toFloat() * scale
            val trackHeight = latRange.toFloat() * scale
            val offsetX = ((size.width - trackWidth) / 2f).coerceAtLeast(padding)
            val offsetY = ((size.height - trackHeight) / 2f).coerceAtLeast(padding)

            val path = Path()
            points.forEachIndexed { index, point ->
                val x = ((point.lon - minLon).toFloat() * scale) + offsetX
                val y = trackHeight - ((point.lat - minLat).toFloat() * scale) + offsetY
                val offset = Offset(x, y)
                if (index == 0) path.moveTo(offset.x, offset.y) else path.lineTo(offset.x, offset.y)
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
    }
}
