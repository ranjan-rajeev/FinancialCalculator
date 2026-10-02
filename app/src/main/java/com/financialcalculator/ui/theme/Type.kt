package com.financialcalculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Type styles transcribed from the `TextViewStyle.*` family in
 * `res/values/styles.xml` plus the hard-coded sizes used by the output layouts.
 *
 * ## Why every [TextStyle] leaves `lineHeight` unspecified
 *
 * A `TextView` with no `android:lineHeight` measures its own line box from the
 * font's metrics. Material3's stock `Typography`, by contrast, hard-codes
 * generous `lineHeight` values (24sp for a 16sp body style, and so on), which
 * would add vertical padding to every single migrated label and quietly change
 * the layout of every card.
 *
 * Leaving `lineHeight = TextUnit.Unspecified` makes Compose fall back to the
 * same font metrics the platform uses, so line boxes match without having to
 * restate a multiplier that would be wrong the moment the font changes. The
 * `LegacyLineHeight` constant below records the Roboto win-metric ratio
 * (ascent 1946 + descent 512 = 2458 / 2048 em) purely as documentation, and is
 * asserted by `LegacyTypographyTest`.
 *
 * `lineHeightStyle` is pinned to the platform defaults for the same reason:
 * Compose otherwise trims ascent/descent on the first and last line of a
 * paragraph, which shrinks `wrap_content` heights relative to a `TextView`.
 */
private val PlatformLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

/** Roboto's Win-metric line height as a multiple of the em size. */
const val LegacyLineHeight: Float = 2458f / 2048f

private fun legacyStyle(
    size: TextUnit,
    color: androidx.compose.ui.graphics.Color,
    weight: FontWeight = FontWeight.Normal,
) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = weight,
    fontSize = size,
    lineHeight = TextUnit.Unspecified,
    lineHeightStyle = PlatformLineHeightStyle,
    color = color,
)

/**
 * The 20dp-wide slice of type the legacy UI actually uses.
 *
 * Material3's `Typography` is still populated (so third-party components that
 * read `MaterialTheme.typography` do not crash) but its styles are mapped onto
 * the legacy equivalents rather than the Material defaults.
 */
val LegacyTypography = Typography(
    // 25sp bold #303F9F — every headline figure in an output card
    // (`tvEmi`, `item_output_key_value`).
    displayLarge = legacyStyle(25.sp, LegacyColors.ColorPrimaryDark, FontWeight.Bold),

    // 20sp bold #d88600 / #3F51B5 — ring-graph percentage and amount labels.
    displayMedium = legacyStyle(20.sp, LegacyColors.Pumpkin, FontWeight.Bold),

    // `TextViewStyle.Large` — 15sp #5D5C5D.
    titleLarge = legacyStyle(15.sp, LegacyColors.HeaderDarkText),

    // `TextViewStyle.Medium` — 13sp #6A6F71.
    titleMedium = legacyStyle(13.sp, LegacyColors.HeaderLightText),

    // `TextViewStyle.Small` — 12sp #929292, overridden to #000000 by the output
    // and schedule layouts, which is why `OutputLabel` exists separately.
    bodyMedium = legacyStyle(12.sp, LegacyColors.DescriptionText),

    // `TextViewStyle.VerySmall` — 10sp, used for the dashboard calculator label.
    labelSmall = legacyStyle(10.sp, LegacyColors.DescriptionText),
)

/**
 * Semantic aliases for the text roles the legacy layouts express through
 * `style="@style/..."` plus a `textColor` override on the same `TextView`.
 * Compose has no style-inheritance-with-override mechanism, so each combination
 * the XML actually produces is named explicitly.
 */
object LegacyTextStyles {
    /** `TextViewStyle.Large` — 15sp #5D5C5D. */
    val Large = LegacyTypography.titleLarge

    /** `TextViewStyle.Medium` — 13sp #6A6F71. */
    val Medium = LegacyTypography.titleMedium

    /** `TextViewStyle.Small` — 12sp #929292. */
    val Small = LegacyTypography.bodyMedium

    /** `TextViewStyle.VerySmall` — 10sp #929292. */
    val VerySmall = LegacyTypography.labelSmall

    /** Base `TextViewStyle` — inherits `secondary_text_color` #9BA0A6. */
    val Default = legacyStyle(TextUnit.Unspecified, LegacyColors.SecondaryText)

    /** 25sp bold #303F9F — the hero figure in a result card. */
    val OutputValue = LegacyTypography.displayLarge

    /** 20sp bold #3F51B5 — ring-graph percentage. */
    val RingPercent = legacyStyle(20.sp, LegacyColors.ColorPrimary, FontWeight.Bold)

    /** 20sp bold #3F51B5 — ring-graph amount. */
    val RingAmount = legacyStyle(20.sp, LegacyColors.ColorPrimary, FontWeight.Bold)

    /**
     * `TextViewStyle.Small` re-coloured to `uvv_black` — the label above a hero
     * figure, and the ring caption.
     */
    val OutputLabel = legacyStyle(12.sp, LegacyColors.Black)

    /** Dashboard section header — 15sp bold #303F9F, `item_header`. */
    val SectionHeader = legacyStyle(15.sp, LegacyColors.ColorPrimaryDark, FontWeight.Bold)

    /** Dashboard calculator label — 10sp #6A6F71, `item_dashboard`. */
    val DashboardCellLabel = legacyStyle(10.sp, LegacyColors.HeaderLightText)

    /** History entry principal — 15sp bold #303F9F, `item_emi_history`. */
    val HistoryPrincipal = legacyStyle(15.sp, LegacyColors.ColorPrimaryDark, FontWeight.Bold)

    /** History entry term — 15sp #212121, `item_emi_history`. */
    val HistoryTerm = legacyStyle(15.sp, LegacyColors.ApplicationPrimaryText)

    /** History entry date — 15sp #757575 right-aligned, `item_emi_history`. */
    val HistoryDate = legacyStyle(15.sp, LegacyColors.ApplicationSecondaryText)
}
