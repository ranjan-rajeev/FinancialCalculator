package com.financialcalculator.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Corner radii transcribed from the drawables and layouts the legacy UI uses.
 *
 * Named after the resource they replace rather than after a Material role,
 * because there is no consistent size story here: the same 5dp appears as both
 * a field outline and a history card.
 */
object LegacyShapes {
    /** `@drawable/corner_shape` — edit-text and spinner outline. */
    val Field = RoundedCornerShape(5.dp)

    /** `app:cardCornerRadius="5dp"` — `item_emi_history`. */
    val HistoryCard = RoundedCornerShape(5.dp)

    /** `@drawable/rounded_corner_*_grey` — carousel indicator. */
    val CarouselDot = RoundedCornerShape(5.dp)

    /** `@drawable/button_selector` — the Calculate button. */
    val Button = RoundedCornerShape(15.dp)

    /** `app:cardCornerRadius="8dp"` — every calculator input/result card. */
    val Card = RoundedCornerShape(8.dp)

    /** 1dp radius used by the schedule header cells, matching `border_gray`. */
    val ScheduleHeaderCell = RoundedCornerShape(1.dp)

    /** `@drawable/circle_{green,blue,red}` — the 70dp rate badge. */
    val CircleBadge: Shape = RoundedCornerShape(percent = 50)

    /** The splash ring and the carousel image corners. */
    val SplashRing: Shape = RoundedCornerShape(percent = 50)
}

/** Corner radii exposed through `MaterialTheme.shapes` for the few screens that read it. */
val LegacyShapeScheme = Shapes(
    extraSmall = LegacyShapes.Field,
    small = LegacyShapes.HistoryCard,
    medium = LegacyShapes.Card,
    large = LegacyShapes.Button,
    extraLarge = LegacyShapes.Button,
)

/**
 * Elevation values used by the legacy `CardView`s.
 *
 * `CardView`'s elevation and Compose's `shadowElevation` are both 1dp = 1px at
 * mdpi but diverge at higher densities, so these are kept as explicit `Dp`
 * values rather than relying on either default.
 */
object LegacyElevation {
    /** `android:elevation="8dp"` / `app:cardElevation="8dp"` — calculator cards. */
    val Card: Dp = 8.dp

    /** `android:elevation="5dp"` — `item_emi_history`. */
    val HistoryCard: Dp = 5.dp
}
