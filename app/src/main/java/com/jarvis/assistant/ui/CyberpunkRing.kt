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
import com.jarvis.assistant.ui.theme.*

@Composable
fun CyberpunkRing(isListening: Boolean, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "ring")
    val rotation by transition.animateFloat(0f, 360f, infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart), label = "rot")
    val pulse by transition.animateFloat(0.8f, 1.2f, infiniteRepeatable(tween(1000, FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse")
    val alpha by transition.animateFloat(0.3f, if (isListening) 1f else 0.5f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "alpha")

    Canvas(modifier = modifier.size(200.dp)) {
        val c = Offset(size.width / 2, size.height / 2)
        val r = size.width / 2
        drawCircle(brush = Brush.radialGradient(listOf(CyberPurple.copy(alpha = 0.2f * pulse), CyberPurple.copy(alpha = 0f)), c, r * 1.3f), radius = r * 1.3f)
        drawCircle(color = CyberCyan.copy(alpha = alpha), radius = r * 0.9f, style = Stroke(3.dp.toPx()))
        drawCircle(color = CyberPurple.copy(alpha = alpha * 0.7f), radius = r * 0.7f, style = Stroke(2.dp.toPx()))
        drawArc(color = CyberPink.copy(alpha = alpha), startAngle = rotation, sweepAngle = 60f, useCenter = false, style = Stroke(4.dp.toPx()), topLeft = Offset(c.x - r * 0.85f, c.y - r * 0.85f), size = androidx.compose.ui.geometry.Size(r * 1.7f, r * 1.7f))
        drawCircle(brush = Brush.radialGradient(listOf(CyberPurple.copy(alpha = 0.6f), CyberCyan.copy(alpha = 0.3f)), c, r * 0.3f), radius = r * 0.3f)
    }
}
