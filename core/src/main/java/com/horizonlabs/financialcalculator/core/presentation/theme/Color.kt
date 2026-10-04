package com.horizonlabs.financialcalculator.core.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette ported from the pre-migration Java/XML app so the rewrite keeps the
 * same look. Values mirror `app/src/main/res/values/colors.xml`.
 */
object LegacyColors {
    val Primary = Color(0xFF3F51B5) // colorPrimary
    val PrimaryDark = Color(0xFF303F9F) // colorPrimaryDark
    val Accent = Color(0xFFFF4081) // colorAccent_inspect, used by the banner artwork
    val AccentDeep = Color(0xFFFF1744) // banner highlight

    val Background = Color(0xFFFFFFFF) // android:windowBackground
    val DashboardBackground = Color(0xFFEAEFF7) // bg_light
    val DashboardSectionBackground = Color(0xFFE3E3E3) // dashboard_bg
    val CardBackground = Color(0xFFFFFFFF)

    val InputStroke = Color(0xFFC9C5C5) // bg, the resting input border
    val Divider = Color(0xFFF1F1F1)
    val BreakdownRowBackground = Color(0xFFE0E0E0) // lightGrey, odd year rows
    val BreakdownMonthBackground = Color(0xFFEAEFF7) // bg_light, expanded month rows

    val TextPrimary = Color(0xFF212121) // application_primary_text_color
    val TextDark = Color(0xFF5D5C5D) // header_dark_text
    val TextLight = Color(0xFF6A6F71) // header_light_text
    val TextDescription = Color(0xFF929292) // description_text
    val TextSecondary = Color(0xFF9BA0A6) // secondary_text_color
    val TextOnPrimary = Color(0xFFFFFFFF)

    val Error = Color(0xFFD3332B) // red_nav
}
