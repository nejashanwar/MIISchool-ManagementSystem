package com.example.marasaaapp.ui.theme

import androidx.compose.ui.graphics.Color

// Madrasaa Ifaa Islama - Brand Palettes & Theme Accents

// Light Scheme Colors (Default Emerald)
val EmeraldPrimary = Color(0xFF006C4C)
val OnEmeraldPrimary = Color(0xFFFFFFFF)
val EmeraldPrimaryContainer = Color(0xFF89F8C7)
val OnEmeraldPrimaryContainer = Color(0xFF002114)

val GoldSecondary = Color(0xFF8C6E00)
val OnGoldSecondary = Color(0xFFFFFFFF)
val GoldSecondaryContainer = Color(0xFFFFE082)
val OnGoldSecondaryContainer = Color(0xFF2B2000)

val DeepTealTertiary = Color(0xFF00696B)
val OnDeepTealTertiary = Color(0xFFFFFFFF)
val DeepTealTertiaryContainer = Color(0xFF9CF1F3)
val OnDeepTealTertiaryContainer = Color(0xFF002021)

val IslamicLightBackground = Color(0xFFF6FBF6)
val IslamicLightSurface = Color(0xFFF6FBF6)
val IslamicLightSurfaceVariant = Color(0xFFDCE5DC)
val IslamicLightOnSurface = Color(0xFF181D1A)
val IslamicLightOnSurfaceVariant = Color(0xFF404943)

// Dark Scheme Colors
val EmeraldDarkPrimary = Color(0xFF6CDBAC)
val OnEmeraldDarkPrimary = Color(0xFF003826)
val EmeraldDarkPrimaryContainer = Color(0xFF005238)
val OnEmeraldDarkPrimaryContainer = Color(0xFF89F8C7)

val GoldDarkSecondary = Color(0xFFE5C352)
val OnGoldDarkSecondary = Color(0xFF3A2D00)
val GoldDarkSecondaryContainer = Color(0xFF544300)
val OnGoldDarkSecondaryContainer = Color(0xFFFFE082)

val DeepTealDarkTertiary = Color(0xFF80D5D7)
val OnDeepTealDarkTertiary = Color(0xFF003637)
val DeepTealDarkTertiaryContainer = Color(0xFF004F50)
val OnDeepTealDarkTertiaryContainer = Color(0xFF9CF1F3)

val IslamicDarkBackground = Color(0xFF101412)
val IslamicDarkSurface = Color(0xFF101412)
val IslamicDarkSurfaceVariant = Color(0xFF404943)
val IslamicDarkOnSurface = Color(0xFFE0E3DE)
val IslamicDarkOnSurfaceVariant = Color(0xFFC0C9C1)

// Theme Accent Customizer Options
enum class ThemeAccent(
    val label: String,
    val primaryColor: Color,
    val containerColor: Color,
    val darkPrimaryColor: Color
) {
    EMERALD(
        label = "Madrasaa Emerald Green",
        primaryColor = Color(0xFF006C4C),
        containerColor = Color(0xFF89F8C7),
        darkPrimaryColor = Color(0xFF6CDBAC)
    ),
    GOLD(
        label = "Royal Gold",
        primaryColor = Color(0xFF8C6E00),
        containerColor = Color(0xFFFFE082),
        darkPrimaryColor = Color(0xFFE5C352)
    ),
    SAPPHIRE(
        label = "Deep Sapphire Blue",
        primaryColor = Color(0xFF004B87),
        containerColor = Color(0xFFB5C4FF),
        darkPrimaryColor = Color(0xFF9ECAFF)
    ),
    BURGUNDY(
        label = "Crimson Burgundy",
        primaryColor = Color(0xFF7A0019),
        containerColor = Color(0xFFFFDADA),
        darkPrimaryColor = Color(0xFFFFB3B8)
    )
}
