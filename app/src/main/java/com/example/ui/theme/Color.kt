package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Liquid Glass Brand & Accent Colors
val ElectricBlue = Color(0xFF007AFF)
val ElectricBlueLight = Color(0xFF0A84FF)
val ElectricBlueDark = Color(0xFF0051A8)
val StatusGreen = Color(0xFF34C759)
val StatusGreenGlow = Color(0x6634C759)
val WarningAmber = Color(0xFFFF9500)
val DangerRed = Color(0xFFFF3B30)

// Liquid Neutral & Accent Colors
val SlateDark = Color(0xFF0F172A)
val SlateMedium = Color(0xFF334155)
val SlateLight = Color(0xFF64748B)
val SlateMuted = Color(0xFF94A3B8)
val SlateBorder = Color(0xFFE2E8F0)
val OffWhite = Color(0xFFF8FAFC)

// Translucent Glass Surfaces
val GlassSurfaceLight = Color(0xB8FFFFFF)
val GlassSurfaceWhite20 = Color(0x33FFFFFF)
val GlassSurfaceWhite10 = Color(0x1AFFFFFF)
val GlassSurfaceWhite40 = Color(0x66FFFFFF)
val GlassSurfaceWhite80 = Color(0xCCFFFFFF)
val GlassBorderHighlight = Color(0x80FFFFFF)
val GlassBorderDark = Color(0x1A000000)

val DarkGlassBackground = Color(0xE614171F)
val DarkCardSurface = Color(0x801F2937)
val DarkGlassBorder = Color(0x33FFFFFF)

// Wallpaper Gradients
val IceBlueGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFEBF4FA),
        Color(0xFFD3E7F4),
        Color(0xFFB9D8ED),
        Color(0xFFA6CBE6)
    )
)

val DuskSandGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF3EBE6),
        Color(0xFFE6D5CA),
        Color(0xFFD1BDB0),
        Color(0xFFBAA394)
    )
)

val PineGreenGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1B593E),
        Color(0xFF174C35),
        Color(0xFF0F3223)
    )
)

val DeepDarkGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1E2430),
        Color(0xFF141824),
        Color(0xFF0B0E17)
    )
)

val OmnibarGlassBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xCCFFFFFF),
        Color(0x99F0FDF4),
        Color(0x73DCFCE7)
    )
)

val OmnibarDarkGlassBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xCC1E293B),
        Color(0x990F172A),
        Color(0x80020617)
    )
)
