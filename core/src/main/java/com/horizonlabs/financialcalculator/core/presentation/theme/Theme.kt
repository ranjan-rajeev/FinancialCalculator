package com.horizonlabs.financialcalculator.core.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColorScheme = lightColorScheme(
    primary = LegacyColors.Primary,
    onPrimary = LegacyColors.TextOnPrimary,
    primaryContainer = Color(0xFFE8EAF6),
    onPrimaryContainer = LegacyColors.PrimaryDark,
    secondary = LegacyColors.Accent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE1EC),
    onSecondaryContainer = Color(0xFF880E4F),
    tertiary = LegacyColors.TextLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDEDED),
    onTertiaryContainer = LegacyColors.TextDark,
    background = LegacyColors.Background,
    onBackground = LegacyColors.TextPrimary,
    surface = LegacyColors.CardBackground,
    onSurface = LegacyColors.TextPrimary,
    surfaceVariant = LegacyColors.BreakdownRowBackground,
    onSurfaceVariant = LegacyColors.TextDark,
    outline = LegacyColors.InputStroke,
    outlineVariant = LegacyColors.Divider,
    error = LegacyColors.Error,
    onError = Color.White,
    errorContainer = Color(0xFFFDECEA),
    onErrorContainer = Color(0xFF8E1B15)
)

/**
 * The legacy app is light-only (`Theme.AppCompat.Light.NoActionBar` with a white
 * window background), so dark mode is intentionally not offered.
 */
@Composable
fun FinancialCalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

/**
 * Type scale taken from the legacy `TextViewStyle.*` styles and `dimens.xml`
 * rather than the Material defaults, so text renders at the previous sizes.
 */
private fun legacy(
    size: Int,
    color: Color,
    weight: FontWeight = FontWeight.Normal
) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = weight,
    fontSize = size.sp,
    color = color
)

val AppTypography = Typography(
    // `text_size_25` bold, the large result values.
    displaySmall = legacy(25, LegacyColors.PrimaryDark, FontWeight.Bold),
    headlineMedium = legacy(20, LegacyColors.PrimaryDark, FontWeight.Bold),
    headlineSmall = legacy(20, LegacyColors.PrimaryDark, FontWeight.Bold),
    titleLarge = legacy(15, LegacyColors.PrimaryDark, FontWeight.Bold),
    titleMedium = legacy(15, LegacyColors.TextDark),
    titleSmall = legacy(15, LegacyColors.TextDark),
    bodyLarge = legacy(15, LegacyColors.TextDark),
    bodyMedium = legacy(13, LegacyColors.TextLight),
    bodySmall = legacy(12, LegacyColors.TextDescription),
    labelLarge = legacy(15, LegacyColors.TextOnPrimary, FontWeight.Medium),
    labelMedium = legacy(13, LegacyColors.TextLight),
    labelSmall = legacy(10, LegacyColors.TextDescription)
)

/** Radii ported from the legacy drawables: 8dp cards, 5dp inputs, 15dp pill buttons. */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(5.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(15.dp)
)

