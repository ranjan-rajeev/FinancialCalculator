package com.financialcalculator.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The legacy colour scheme, mapped onto the Material3 roles.
 *
 * The app is light-only: `AppTheme` parents `Theme.AppCompat.Light.NoActionBar`
 * with no `-night` variant, so there is no dark scheme to provide. Material3's
 * `error`/`onError`/`surfaceVariant` roles have no legacy equivalent and are
 * filled with the nearest value already in `colors.xml` rather than the
 * Material default, so that any standard component dropped in during Phases
 * 5-9 cannot introduce a colour the XML never had.
 */
internal val LegacyColorScheme = lightColorScheme(
    primary = LegacyColors.ColorPrimary,
    onPrimary = LegacyColors.White,
    primaryContainer = LegacyColors.BgLight,
    onPrimaryContainer = LegacyColors.ColorPrimaryDark,

    secondary = LegacyColors.ColorPrimaryDark,
    onSecondary = LegacyColors.White,
    secondaryContainer = LegacyColors.BgLight,
    onSecondaryContainer = LegacyColors.ColorPrimaryDark,

    tertiary = LegacyColors.Pumpkin,
    onTertiary = LegacyColors.White,
    tertiaryContainer = LegacyColors.GreyEA,
    onTertiaryContainer = LegacyColors.Pumpkin,

    background = LegacyColors.White,
    onBackground = LegacyColors.Black,

    surface = LegacyColors.White,
    onSurface = LegacyColors.Black,
    surfaceVariant = LegacyColors.GreyEA,
    onSurfaceVariant = LegacyColors.ApplicationSecondaryText,
    surfaceContainerLowest = LegacyColors.White,
    surfaceContainerLow = LegacyColors.BgLight,
    surfaceContainer = LegacyColors.BgLight,
    surfaceContainerHigh = LegacyColors.DashboardBg,
    surfaceContainerHighest = LegacyColors.DashboardBg,
    surfaceTint = Color.Transparent,

    error = LegacyColors.RedDescent,
    onError = LegacyColors.White,
    errorContainer = LegacyColors.GreyEA,
    onErrorContainer = LegacyColors.RedDescent,

    outline = LegacyColors.FieldBorder,
    outlineVariant = LegacyColors.Divider,
    scrim = LegacyColors.Black,
)

/**
 * The calculator screens do not use `colorPrimary` as their app-bar colour; the
 * Java activities set the toolbar background to `bg_light` instead. Exposing it
 * as a composition local keeps that decision in one place rather than having
 * every screen remember to reach for [LegacyColors] directly.
 */
val LocalScreenBackground = staticCompositionLocalOf { LegacyColors.BgLight }

@Composable
fun FCFTheme(content: @Composable () -> Unit) {
    // Dynamic colour is deliberately *not* wired up. On Android 12+ it would
    // repaint the whole app in the user's wallpaper palette, which contradicts
    // the "design exactly the same" requirement; see the risk register in
    // MIGRATION_PLAN.md §5.
    CompositionLocalProvider(LocalContentColor provides LegacyColors.Black) {
        MaterialTheme(
            colorScheme = LegacyColorScheme,
            typography = LegacyTypography,
            shapes = LegacyShapeScheme,
            content = content,
        )
    }
}

/** Convenience accessor mirroring `MaterialTheme.colorScheme`. */
object FCFTheme {
    val screenBackground: Color
        @Composable @ReadOnlyComposable get() = LocalScreenBackground.current
}
