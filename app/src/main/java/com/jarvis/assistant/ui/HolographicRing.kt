package com.jarvis.assistant.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.jarvis.assistant.ui.theme.JarvisBlue
import com.jarvis.assistant.ui.theme.JarvisBlueDark

@Composable
fun HolographicRing(
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isListening) 1f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        // Outer glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    JarvisBlue.copy(alpha = 0.1f * pulse),
                    JarvisBlue.copy(alpha = 0f)
                ),
                center = center,
                radius = radius * 1.2f
            ),
            radius = radius * 1.2f
        )

        // Main ring
        drawCircle(
            color = JarvisBlue.copy(alpha = alpha),
            radius = radius * 0.9f,
            style = Stroke(width = 3.dp.toPx())
        )

        // Inner ring
        drawCircle(
            color = JarvisBlueDark.copy(alpha = alpha * 0.7f),
            radius = radius * 0.7f,
            style = Stroke(width = 2.dp.toPx())
        )

        // Rotating arc
        drawArc(
            color = JarvisBlue.copy(alpha = alpha),
            startAngle = rotation,
            sweepAngle = 60f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx()),
            topLeft = Offset(
                center.x - radius * 0.85f,
                center.y - radius * 0.85f
            ),
            size = androidx.compose.ui.geometry.Size(
                radius * 1.7f,
                radius * 1.7f
            )
        )

        // Center dot
        drawCircle(
            color = JarvisBlue.copy(alpha = alpha * 0.8f),
            radius = 8.dp.toPx()
        )
    }
}
