package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun VegetableGraphic(type: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFE8F5E9), CircleShape)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val cy = h / 2

            when (type.lowercase()) {
                "tomato" -> {
                    // Draw red tomato body
                    drawCircle(
                        color = Color(0xFFE53935),
                        radius = cx * 0.8f,
                        center = Offset(cx, cy + h * 0.05f)
                    )
                    // Draw green stem
                    val leafPath = Path().apply {
                        moveTo(cx, cy - h * 0.4f)
                        lineTo(cx - w * 0.15f, cy - h * 0.2f)
                        lineTo(cx + w * 0.15f, cy - h * 0.2f)
                        close()
                    }
                    drawPath(leafPath, color = Color(0xFF43A047))
                    drawCircle(
                        color = Color(0xFF43A047),
                        radius = cx * 0.15f,
                        center = Offset(cx, cy - h * 0.2f)
                    )
                }
                "potato" -> {
                    // Draw brown potato body
                    val path = Path().apply {
                        moveTo(cx - w * 0.4f, cy)
                        quadraticTo(cx - w * 0.4f, cy - h * 0.35f, cx, cy - h * 0.35f)
                        quadraticTo(cx + w * 0.45f, cy - h * 0.25f, cx + w * 0.45f, cy)
                        quadraticTo(cx + w * 0.4f, cy + h * 0.35f, cx - w * 0.1f, cy + h * 0.35f)
                        quadraticTo(cx - w * 0.4f, cy + h * 0.25f, cx - w * 0.4f, cy)
                        close()
                    }
                    drawPath(path, color = Color(0xFF8D6E63))
                    // Tiny sprouts
                    drawCircle(Color(0xFF5D4037), cx * 0.05f, Offset(cx - w * 0.15f, cy - h * 0.1f))
                    drawCircle(Color(0xFF5D4037), cx * 0.04f, Offset(cx + w * 0.2f, cy + h * 0.1f))
                    drawCircle(Color(0xFF5D4037), cx * 0.05f, Offset(cx - w * 0.05f, cy + h * 0.15f))
                }
                "carrot" -> {
                    // Draw orange triangular carrot pointing down
                    val path = Path().apply {
                        moveTo(cx - w * 0.3f, cy - h * 0.3f)
                        lineTo(cx + w * 0.3f, cy - h * 0.3f)
                        lineTo(cx, cy + h * 0.45f)
                        close()
                    }
                    drawPath(path, color = Color(0xFFFFB74D)) // bright orange
                    
                    // Lines on carrot
                    drawLine(Color(0xFFEF6C00), Offset(cx - w * 0.15f, cy - h * 0.1f), Offset(cx + w * 0.1f, cy - h * 0.1f), strokeWidth = 2f)
                    drawLine(Color(0xFFEF6C00), Offset(cx - w * 0.1f, cy + h * 0.1f), Offset(cx + w * 0.05f, cy + h * 0.1f), strokeWidth = 2f)

                    // Green tops
                    val stem1 = Path().apply {
                        moveTo(cx, cy - h * 0.3f)
                        quadraticTo(cx - w * 0.15f, cy - h * 0.45f, cx - w * 0.2f, cy - h * 0.45f)
                    }
                    val stem2 = Path().apply {
                        moveTo(cx, cy - h * 0.3f)
                        quadraticTo(cx + w * 0.15f, cy - h * 0.45f, cx + w * 0.2f, cy - h * 0.45f)
                    }
                    drawPath(stem1, color = Color(0xFF4CAF50), style = Stroke(width = 6f))
                    drawPath(stem2, color = Color(0xFF4CAF50), style = Stroke(width = 6f))
                }
                "spinach", "organic_spinach" -> {
                    // Draw broad green leaves overlapping
                    val path1 = Path().apply {
                        moveTo(cx, cy + h * 0.3f)
                        cubicTo(cx - w * 0.4f, cy + h * 0.1f, cx - w * 0.4f, cy - h * 0.3f, cx, cy - h * 0.35f)
                        cubicTo(cx + w * 0.4f, cy - h * 0.3f, cx + w * 0.4f, cy + h * 0.1f, cx, cy + h * 0.3f)
                    }
                    drawPath(path1, color = Color(0xFF4CAF50))
                    
                    // Leaf veins
                    drawLine(Color(0xFF388E3C), Offset(cx, cy + h * 0.3f), Offset(cx, cy - h * 0.3f), strokeWidth = 4f)
                    drawLine(Color(0xFF388E3C), Offset(cx, cy + h * 0.1f), Offset(cx - w * 0.18f, cy - h * 0.05f), strokeWidth = 3f)
                    drawLine(Color(0xFF388E3C), Offset(cx, cy + h * 0.1f), Offset(cx + w * 0.18f, cy - h * 0.05f), strokeWidth = 3f)
                    drawLine(Color(0xFF388E3C), Offset(cx, cy - h * 0.1f), Offset(cx - w * 0.12f, cy - h * 0.2f), strokeWidth = 3f)
                    drawLine(Color(0xFF388E3C), Offset(cx, cy - h * 0.1f), Offset(cx + w * 0.12f, cy - h * 0.2f), strokeWidth = 3f)

                    if (type.lowercase() == "organic_spinach") {
                        // Eco leaf badge
                        drawCircle(Color.White, cx * 0.22f, Offset(cx + w * 0.22f, cy - h * 0.22f))
                        drawCircle(Color(0xFF2E7D32), cx * 0.18f, Offset(cx + w * 0.22f, cy - h * 0.22f))
                    }
                }
                "onion" -> {
                    // Draw red/purple round onion
                    drawCircle(
                        color = Color(0xFFC2185B),
                        radius = cx * 0.75f,
                        center = Offset(cx, cy + h * 0.05f)
                    )
                    // Draw stripes
                    drawArc(
                        color = Color(0xFFF8BBD0),
                        startAngle = 120f,
                        sweepAngle = 120f,
                        useCenter = false,
                        topLeft = Offset(cx - cx * 0.6f, cy - h * 0.6f),
                        size = Size(cx * 1.2f, h * 1.2f),
                        style = Stroke(width = 3f)
                    )
                    // Draw white stem at top
                    val path = Path().apply {
                        moveTo(cx - w * 0.1f, cy - h * 0.35f)
                        lineTo(cx + w * 0.1f, cy - h * 0.35f)
                        lineTo(cx, cy - h * 0.45f)
                        close()
                    }
                    drawPath(path, color = Color(0xFFDCEDC8))
                }
                "apple" -> {
                    // Double curves for sweet apple
                    val path = Path().apply {
                        moveTo(cx, cy - h * 0.25f)
                        cubicTo(cx - w * 0.25f, cy - h * 0.35f, cx - w * 0.45f, cy - h * 0.1f, cx - w * 0.4f, cy + h * 0.15f)
                        cubicTo(cx - w * 0.35f, cy + h * 0.38f, cx - w * 0.1f, cy + h * 0.38f, cx, cy + h * 0.28f)
                        cubicTo(cx + w * 0.1f, cy + h * 0.38f, cx + w * 0.35f, cy + h * 0.38f, cx + w * 0.4f, cy + h * 0.15f)
                        cubicTo(cx + w * 0.45f, cy - h * 0.1f, cx + w * 0.25f, cy - h * 0.35f, cx, cy - h * 0.25f)
                        close()
                    }
                    drawPath(path, color = Color(0xFFD32F2F)) // deep rich red
                    
                    // Stem
                    drawLine(Color(0xFF5D4037), Offset(cx, cy - h * 0.25f), Offset(cx + w * 0.08f, cy - h * 0.42f), strokeWidth = 5f)
                    // Mini leaf
                    val leaf = Path().apply {
                        moveTo(cx + w * 0.05f, cy - h * 0.35f)
                        quadraticTo(cx + w * 0.2f, cy - h * 0.45f, cx + w * 0.25f, cy - h * 0.35f)
                        quadraticTo(cx + w * 0.12f, cy - h * 0.3f, cx + w * 0.05f, cy - h * 0.35f)
                    }
                    drawPath(leaf, color = Color(0xFF43A047))
                }
                "coriander" -> {
                    // Small herb clusters
                    drawCircle(Color(0xFF388E3C), cx * 0.3f, Offset(cx - w * 0.2f, cy + h * 0.1f))
                    drawCircle(Color(0xFF388E3C), cx * 0.28f, Offset(cx + w * 0.2f, cy + h * 0.1f))
                    drawCircle(Color(0xFF4CAF50), cx * 0.34f, Offset(cx, cy - h * 0.15f))
                    
                    // Stem connections
                    drawLine(Color(0xFF2E7D32), Offset(cx, cy), Offset(cx - w * 0.2f, cy + h * 0.1f), strokeWidth = 4f)
                    drawLine(Color(0xFF2E7D32), Offset(cx, cy), Offset(cx + w * 0.2f, cy + h * 0.1f), strokeWidth = 4f)
                    drawLine(Color(0xFF2E7D32), Offset(cx, cy), Offset(cx, cy - h * 0.15f), strokeWidth = 4f)
                    drawLine(Color(0xFF2E7D32), Offset(cx, cy), Offset(cx, cy + h * 0.35f), strokeWidth = 5f)
                }
                "broccoli" -> {
                    // Broccoli visual
                    drawCircle(Color(0xFF2E7D32), cx * 0.35f, Offset(cx - w * 0.18f, cy - h * 0.12f))
                    drawCircle(Color(0xFF1B5E20), cx * 0.38f, Offset(cx + w * 0.18f, cy - h * 0.12f))
                    drawCircle(Color(0xFF4CAF50), cx * 0.42f, Offset(cx, cy - h * 0.22f))
                    // Thick stalk
                    val stalk = Path().apply {
                        moveTo(cx - w * 0.15f, cy - h * 0.1f)
                        lineTo(cx + w * 0.15f, cy - h * 0.1f)
                        lineTo(cx + w * 0.1f, cy + h * 0.35f)
                        lineTo(cx - w * 0.1f, cy + h * 0.35f)
                        close()
                    }
                    drawPath(stalk, color = Color(0xFF81C784))
                }
                "chilli" -> {
                    // Stylized Indian green chilli (Hari Mirch)
                    val body = Path().apply {
                        moveTo(cx - w * 0.15f, cy - h * 0.35f)
                        quadraticTo(cx + w * 0.25f, cy - h * 0.05f, cx, cy + h * 0.4f)
                        quadraticTo(cx - w * 0.2f, cy + h * 0.15f, cx - w * 0.15f, cy - h * 0.35f)
                        close()
                    }
                    drawPath(body, color = Color(0xFF2E7D32))
                    
                    // Stem cap
                    val cap = Path().apply {
                        moveTo(cx - w * 0.18f, cy - h * 0.32f)
                        lineTo(cx + w * 0.08f, cy - h * 0.38f)
                        lineTo(cx, cy - h * 0.45f)
                        close()
                    }
                    drawPath(cap, color = Color(0xFF1B5E20))
                }
                "bhindi" -> {
                    // Lady finger geometry
                    val path = Path().apply {
                        moveTo(cx - w * 0.12f, cy - h * 0.38f)
                        lineTo(cx + w * 0.12f, cy - h * 0.38f)
                        lineTo(cx + w * 0.08f, cy + h * 0.3f)
                        lineTo(cx, cy + h * 0.45f)
                        lineTo(cx - w * 0.08f, cy + h * 0.3f)
                        close()
                    }
                    drawPath(path, color = Color(0xFF4CAF50))
                    // Ridges
                    drawLine(Color(0xFF1B5E20), Offset(cx, cy - h * 0.38f), Offset(cx, cy + h * 0.45f), strokeWidth = 2f)
                    drawLine(Color(0xFF1B5E20), Offset(cx - w * 0.06f, cy - h * 0.38f), Offset(cx - w * 0.04f, cy + h * 0.3f), strokeWidth = 1.5f)
                    drawLine(Color(0xFF1B5E20), Offset(cx + w * 0.06f, cy - h * 0.38f), Offset(cx + w * 0.04f, cy + h * 0.3f), strokeWidth = 1.5f)
                }
                else -> {
                    // Draw a lovely fallback green leaf path which is 100% vector drawn in DrawScope
                    val leafPath = Path().apply {
                        moveTo(cx, cy + h * 0.3f)
                        quadraticTo(cx - w * 0.3f, cy, cx, cy - h * 0.3f)
                        quadraticTo(cx + w * 0.3f, cy, cx, cy + h * 0.3f)
                        close()
                    }
                    drawPath(leafPath, color = Color(0xFF2E7D32))
                    // Small vein
                    drawLine(Color(0xFF1B5E20), Offset(cx, cy + h * 0.3f), Offset(cx, cy - h * 0.25f), strokeWidth = 3f)
                }
            }
        }
    }
}
