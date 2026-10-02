package com.financialcalculator.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Design tokens transcribed from `res/values/colors.xml`.
 *
 * `colors.xml` defines 211 colours; only the ones actually referenced by a
 * layout or a Java source file are reproduced here. Each constant names the
 * legacy resource it came from so the mapping stays auditable during the
 * migration — adding a colour that no legacy screen used would let the Compose
 * UI drift away from the XML it replaces.
 */
object LegacyColors {
    /** `@color/colorPrimary` — app bar tint, buttons, ring arc, focused field border. */
    val ColorPrimary = Color(0xFF3F51B5)

    /** `@color/colorPrimaryDark` — status bar, output values, pressed button fill. */
    val ColorPrimaryDark = Color(0xFF303F9F)

    /** `@color/colorAccent` → `@color/colorCTA`. Only reachable via the app theme. */
    val ColorAccent = Color(0xFFFF1744)

    /** `@color/bg_light` — calculator screen background and app-bar background. */
    val BgLight = Color(0xFFEAEFF7)

    /** `@color/dashboard_bg` — dashboard surface behind the calculator grid. */
    val DashboardBg = Color(0xFFE3E3E3)

    /** `@color/header_dark_text` — `TextViewStyle.Large`. */
    val HeaderDarkText = Color(0xFF5D5C5D)

    /** `@color/header_light_text` — `TextViewStyle.Medium` and the dashboard cell label. */
    val HeaderLightText = Color(0xFF6A6F71)

    /** `@color/description_text` — `TextViewStyle.Small`/`.VerySmall` and field hints. */
    val DescriptionText = Color(0xFF929292)

    /** `@color/secondary_text_color` — `TextViewStyle` default text colour. */
    val SecondaryText = Color(0xFF9BA0A6)

    /** `@color/application_primary_text_color` — history entry term. */
    val ApplicationPrimaryText = Color(0xFF212121)

    /** `@color/application_secondary_text_color` — history entry date. */
    val ApplicationSecondaryText = Color(0xFF757575)

    /** `@color/uvv_black` — body copy and output row labels. */
    val Black = Color(0xFF000000)

    /** `@color/white` — button glyphs, text on coloured badges. */
    val White = Color(0xFFFFFFFF)

    /** `@color/divider` — 1dp rules between output rows and history cells. */
    val Divider = Color(0xFFBDBDBD)

    /** `@color/colorGreyD8`. */
    val GreyD8 = Color(0xFFD8D8D8)

    /** `@color/colorGreyEA`. */
    val GreyEA = Color(0xFFEAEAEA)

    /** `@color/lightGrey` — the full-progress ring track (`@drawable/circular_full`). */
    val LightGrey = Color(0xFFE0E0E0)

    /** `@color/offer_info_white` — the 87%-opaque white field fill. */
    val FieldFill = Color(0xDDFFFFFF)

    /** `@color/bg` — unfocused field border. */
    val FieldBorder = Color(0xFFC9C5C5)

    /** `@color/colorPumpkin` — "Did you know?" heading and 20sp bold amounts. */
    val Pumpkin = Color(0xFFD88600)

    /** `@color/green_descent` — `@drawable/circle_green`, position 0 mod 3. */
    val GreenDescent = Color(0xFF42CEB2)

    /** `@color/red_descent` — `@drawable/circle_red`, position 2 mod 3. */
    val RedDescent = Color(0xFFDE6D75)

    /** `@color/blue_descent` — `@drawable/circle_blue`, position 1 mod 3. */
    val BlueDescent = Color(0xFF4B76C2)

    /** `@color/progress_blue`. */
    val ProgressBlue = Color(0xFF009FE3)
}

/**
 * Colours the legacy dashboard ViewHolders hard-coded rather than declaring in
 * `colors.xml`. Reproduced here so Phase 5 does not have to re-derive them.
 */
internal object LegacyHardCodedColors {
    /**
     * Unselected carousel dot, `@drawable/rounded_corner_light_grey`. The dot is
     * a 5dp-radius bar inset 3dp on each side, not a circle.
     */
    val CarouselDotUnselected = Color(0xFFC5C5C5)

    /** Selected carousel dot, `@drawable/rounded_corner_dark_grey`. */
    val CarouselDotSelected = Color(0xFF949494)
}
