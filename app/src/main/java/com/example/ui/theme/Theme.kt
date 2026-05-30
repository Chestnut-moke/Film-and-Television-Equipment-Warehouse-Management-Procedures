package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = ThemeSoftForestGreen,
  onPrimary = ThemePureCleanWhite,
  secondary = ThemeActiveMintAccent,
  onSecondary = ThemePureCleanWhite,
  tertiary = ThemeDeepForestGreen,
  onTertiary = ThemePureCleanWhite,
  background = ThemeWarmSandBackground,
  onBackground = ThemeTextPrimaryDark,
  surface = ThemePureCleanWhite,
  onSurface = ThemeTextPrimaryDark,
  surfaceVariant = ThemePaleForestBg,
  onSurfaceVariant = ThemeTextPrimaryDark,
  outline = ThemeBorderSlateGrey
)

private val DarkColorScheme = darkColorScheme(
  primary = ThemeLightMintAccent,
  onPrimary = ThemeDeepForestGreen,
  secondary = ThemeActiveMintAccent,
  onSecondary = ThemePureCleanWhite,
  tertiary = ThemeSoftForestGreen,
  onTertiary = ThemePureCleanWhite,
  background = Color(0xFF121614),
  onBackground = Color(0xFFE2E7E4),
  surface = Color(0xFF1B221F),
  onSurface = Color(0xFFE2E7E4),
  surfaceVariant = Color(0xFF232B27),
  onSurfaceVariant = Color(0xFFE2E7E4),
  outline = Color(0xFF35443D)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Override dynamic color to keep our styled green/white Forest brand look Consistent
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
