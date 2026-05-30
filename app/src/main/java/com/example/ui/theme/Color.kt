package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme

// Raw theme colors for static initialization in Theme.kt
val ThemeDeepForestGreen = Color(0xFF1B4332)
val ThemeSoftForestGreen = Color(0xFF2D6A4F)
val ThemeActiveMintAccent = Color(0xFF40916C)
val ThemeLightMintAccent = Color(0xFF74C69D)
val ThemePaleForestBg = Color(0xFFEAF5EE)
val ThemeWarmSandBackground = Color(0xFFFAFBF9)
val ThemeTextPrimaryDark = Color(0xFF1A1F1C)
val ThemeTextSecondaryMuted = Color(0xFF5A6660)
val ThemeBorderSlateGrey = Color(0xFFE2E7E4)
val ThemePureCleanWhite = Color(0xFFFFFFFF)

// Minimal organic Forest-green palette
val DeepForestGreen: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF74C69D) else ThemeDeepForestGreen

val SoftForestGreen: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF52B788) else ThemeSoftForestGreen

val ActiveMintAccent: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF40916C) else ThemeActiveMintAccent

val LightMintAccent: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF74C69D) else ThemeLightMintAccent

val PaleForestBg: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF232B27) else ThemePaleForestBg

val WarmSandBackground: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF121614) else ThemeWarmSandBackground // Dynamic page background

// Notion Slate/Dark colors
val TextPrimaryDark: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFFE2E7E4) else ThemeTextPrimaryDark

val TextSecondaryMuted: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF8A9A93) else ThemeTextSecondaryMuted

val BorderSlateGrey: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF2F3B35) else ThemeBorderSlateGrey

val PureCleanWhite: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF1B221F) else ThemePureCleanWhite // Dynamic card surface

// Secondary alert/notice colors
val CoralOrangeAlert: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFFFBBF24) else Color(0xFFD97706) // Rental warning status

val CoralRedAlert: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFFF87171) else Color(0xFFDC2626) // Expired/Late status

val PaleRedBg: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF3B1E1E) else Color(0xFFFEF2F2)

val PaleOrangeBg: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF3D2E16) else Color(0xFFFFFBEB)

val LightCoolBlue: Color
  @Composable
  get() = if (isSystemInDarkTheme()) Color(0xFF10283E) else Color(0xFFE1F5FE)

