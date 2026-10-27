package com.example.marasaaapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.marasaaapp.repository.SchoolRepository

@Composable
fun MarasaaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    repository: SchoolRepository = SchoolRepository,
    content: @Composable () -> Unit
) {
    val accent by repository.themeAccent.collectAsState()

    val lightColorScheme = lightColorScheme(
        primary = accent.primaryColor,
        onPrimary = OnEmeraldPrimary,
        primaryContainer = accent.containerColor,
        onPrimaryContainer = OnEmeraldPrimaryContainer,
        secondary = GoldSecondary,
        onSecondary = OnGoldSecondary,
        secondaryContainer = GoldSecondaryContainer,
        onSecondaryContainer = OnGoldSecondaryContainer,
        tertiary = DeepTealTertiary,
        onTertiary = OnDeepTealTertiary,
        tertiaryContainer = DeepTealTertiaryContainer,
        onTertiaryContainer = OnDeepTealTertiaryContainer,
        background = IslamicLightBackground,
        surface = IslamicLightSurface,
        surfaceVariant = IslamicLightSurfaceVariant,
        onBackground = IslamicLightOnSurface,
        onSurface = IslamicLightOnSurface,
        onSurfaceVariant = IslamicLightOnSurfaceVariant
    )

    val darkColorScheme = darkColorScheme(
        primary = accent.darkPrimaryColor,
        onPrimary = OnEmeraldDarkPrimary,
        primaryContainer = accent.primaryColor,
        onPrimaryContainer = accent.containerColor,
        secondary = GoldDarkSecondary,
        onSecondary = OnGoldDarkSecondary,
        secondaryContainer = GoldDarkSecondaryContainer,
        onSecondaryContainer = OnGoldDarkSecondaryContainer,
        tertiary = DeepTealDarkTertiary,
        onTertiary = OnDeepTealDarkTertiary,
        tertiaryContainer = DeepTealDarkTertiaryContainer,
        onTertiaryContainer = OnDeepTealDarkTertiaryContainer,
        background = IslamicDarkBackground,
        surface = IslamicDarkSurface,
        surfaceVariant = IslamicDarkSurfaceVariant,
        onBackground = IslamicDarkOnSurface,
        onSurface = IslamicDarkOnSurface,
        onSurfaceVariant = IslamicDarkOnSurfaceVariant
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
