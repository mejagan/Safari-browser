package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a Safari/iOS-inspired Liquid Glass surface effect.
 * Produces crisp content inside while rendering an elegant translucent refraction
 * with top highlight and bottom rim reflection.
 */
fun Modifier.liquidGlass(
    cornerRadius: Dp = 24.dp,
    surfaceColor: Color = Color(0x66FFFFFF),
    borderHighlightColor: Color = Color(0x80FFFFFF),
    borderShadowColor: Color = Color(0x1A000000),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 0.dp
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            borderHighlightColor,
            borderHighlightColor.copy(alpha = 0.3f),
            borderShadowColor
        )
    )

    return this
        .then(
            if (elevation > 0.dp) {
                Modifier.shadow(elevation, shape, clip = false)
            } else {
                Modifier
            }
        )
        .clip(shape)
        .background(color = surfaceColor, shape = shape)
        .border(width = borderWidth, brush = borderBrush, shape = shape)
}

/**
 * Capsule variant of Liquid Glass for search bars, pills, and orbs.
 */
fun Modifier.liquidGlassCapsule(
    surfaceColor: Color = Color(0x80FFFFFF),
    borderColor: Color = Color(0x66FFFFFF),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 0.dp
): Modifier = liquidGlass(
    cornerRadius = 50.dp,
    surfaceColor = surfaceColor,
    borderHighlightColor = borderColor,
    borderShadowColor = Color(0x10000000),
    borderWidth = borderWidth,
    elevation = elevation
)

/**
 * Dark theme variant of Liquid Glass
 */
fun Modifier.liquidGlassDark(
    cornerRadius: Dp = 24.dp,
    surfaceColor: Color = Color(0xCC1E293B),
    borderHighlightColor: Color = Color(0x40FFFFFF),
    borderWidth: Dp = 1.dp
): Modifier = liquidGlass(
    cornerRadius = cornerRadius,
    surfaceColor = surfaceColor,
    borderHighlightColor = borderHighlightColor,
    borderShadowColor = Color(0x66000000),
    borderWidth = borderWidth
)
