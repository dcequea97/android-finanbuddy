package com.example.finanbuddy.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9EE88C),
    onPrimary = Color(0xFF042100),
    primaryContainer = Color(0xFF16440E),
    onPrimaryContainer = Color(0xFFB7F397),
    inversePrimary = Color(0xFF2E6D13),

    secondary = Color(0xFFBFD8B4),
    onSecondary = Color(0xFF072100),
    secondaryContainer = Color(0xFF234017),
    onSecondaryContainer = Color(0xFFD8E7CB),

    tertiary = Color(0xFFA7D78A),
    onTertiary = Color(0xFF062103),
    tertiaryContainer = Color(0xFF223B14),
    onTertiaryContainer = Color(0xFFDFF7D1),

    background = Color(0xFF07140A),
    onBackground = Color(0xFFE6F6E4),
    surface = Color(0xFF07140A),
    onSurface = Color(0xFFE6F6E4),

    surfaceVariant = Color(0xFF0F2612),
    onSurfaceVariant = Color(0xFFC3C8BC),
    surfaceTint = Color(0xFF9EE88C),

    inverseSurface = Color(0xFFF3FBF3),
    inverseOnSurface = Color(0xFF081802),

    error = Color(0xFFCF6679),
    onError = Color(0xFF370B12),
    errorContainer = Color(0xFF8C1D2B),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF8DA089),
    outlineVariant = Color(0xFF232922),
    scrim = Color(0x66000000),

    // Extended / additional colors (estilo negro / oscuro)
    surfaceBright = Color(0xFF0B1A0F),
    surfaceDim = Color(0xFF041006),
    surfaceContainer = Color(0xFF0C1F12),
    surfaceContainerHigh = Color(0xFF1D1E1D),
    surfaceContainerHighest = Color(0xFF1D1E1D),
    surfaceContainerLow = Color(0xFF07140A),
    surfaceContainerLowest = Color(0xFF041009),

    primaryFixed = Color(0xFF7FD06A),
    primaryFixedDim = Color(0xFF5EA84F),
    onPrimaryFixed = Color(0xFF042100),
    onPrimaryFixedVariant = Color(0xFFB7F397),

    secondaryFixed = Color(0xFF9FCB96),
    secondaryFixedDim = Color(0xFF7AAE73),
    onSecondaryFixed = Color(0xFF072100),
    onSecondaryFixedVariant = Color(0xFFD8E7CB),

    tertiaryFixed = Color(0xFF8FD06A),
    tertiaryFixedDim = Color(0xFF6EA64F),
    onTertiaryFixed = Color(0xFF062103),
    onTertiaryFixedVariant = Color(0xFFDFF7D1)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF386A20),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB7F397),
    onPrimaryContainer = Color(0xFF042100),
    inversePrimary = Color(0xFFB7F397),

    secondary = Color(0xFF5A7F38),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD8E7CB),
    onSecondaryContainer = Color(0xFF131F0E),

    tertiary = Color(0xFF8ABF6B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD7F0C8),
    onTertiaryContainer = Color(0xFF062403),

    background = Color(0xFFE5E4E4), // user requirement
    onBackground = Color(0xFF191C18),
    surface = Color(0xFFFFFFFF), // cards white
    onSurface = Color(0xFF191C18),

    surfaceVariant = Color(0xFFF2F5F0),
    onSurfaceVariant = Color(0xFF43483E),
    surfaceTint = Color(0xFF386A20),

    inverseSurface = Color(0xFF11120F),
    inverseOnSurface = Color(0xFFEFF6EA),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = Color(0xFF73796F),
    outlineVariant = Color(0xFFC3C8BC),
    scrim = Color(0x66000000),

    // Extended / additional colors (manteniendo estilo oscuro en acentos)
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFF5F6F5),
    surfaceContainer = Color(0xFFF8FAF7),
    surfaceContainerHigh = Color(0xFFEEF3EA),
    surfaceContainerHighest = Color(0xFFE6EFE1),
    surfaceContainerLow = Color(0xFFF2F5F0),
    surfaceContainerLowest = Color(0xFFF7FAF6),

    primaryFixed = Color(0xFF2F5A18),
    primaryFixedDim = Color(0xFF244712),
    onPrimaryFixed = Color(0xFFFFFFFF),
    onPrimaryFixedVariant = Color(0xFFB7F397),

    secondaryFixed = Color(0xFF4F6C2E),
    secondaryFixedDim = Color(0xFF3E5624),
    onSecondaryFixed = Color(0xFFFFFFFF),
    onSecondaryFixedVariant = Color(0xFFD8E7CB),

    tertiaryFixed = Color(0xFF6FAF4F),
    tertiaryFixedDim = Color(0xFF568B3F),
    onTertiaryFixed = Color(0xFFFFFFFF),
    onTertiaryFixedVariant = Color(0xFFDFF7D1)
)

@Composable
fun FinanBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
