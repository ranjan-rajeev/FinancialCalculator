package com.financialcalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.financialcalculator.ui.theme.LegacyColors
import com.financialcalculator.ui.theme.LegacyShapes
import com.financialcalculator.ui.theme.LegacyTextStyles

/**
 * The three descent badge colours, in the order `Util.getFixedBackground` cycles
 * them. `getRandomBackground` picks from the same three, so both legacy
 * helpers collapse to this one enum.
 */
enum class LegacyDescentBadge(val color: Color) {
    /** `@drawable/circle_green` → `#42ceb2`. */
    Green(LegacyColors.GreenDescent),

    /** `@drawable/circle_blue` → `#4b76c2`. */
    Blue(LegacyColors.BlueDescent),

    /** `@drawable/circle_red` → `#de6d75`. */
    Red(LegacyColors.RedDescent),
    ;

    companion object {
        /**
         * The badge for a list row. Port of `Util.getFixedBackground`, which
         * switches on `position % 3`.
         *
         * Java's `%` keeps the sign of the dividend, so a negative position
         * produces a negative case label that matches none of `0`/`1`/`2` and
         * falls through to `default` — green. That is reproduced here rather
         * than "fixed" with a floorMod, which would silently recolour every
         * negative index red.
         */
        fun atPosition(position: Int): LegacyDescentBadge {
            val index = position % entries.size
            return if (index < 0) Green else entries[index]
        }

        /**
         * Faithful port of `Util.getRandomBackground`, including its dead
         * `case 2`: `Random.nextInt(2)` returns 0 or 1 only, so the red badge is
         * unreachable here. Kept so a screen that used the random helper keeps
         * producing the same pixels.
         */
        fun randomLegacy(): LegacyDescentBadge {
            val n = kotlin.random.Random.nextInt(2)
            return if (n == 0) Green else Blue
        }
    }
}

/**
 * The 70dp rate badge from `item_emi_history`.
 *
 * `@drawable/circle_green` is an `<shape android:shape="oval">` with a `150dp`
 * `size` hint, but the `TextView` that uses it is explicitly `70dp` x `70dp`, so
 * the oval is scaled down to that. The label is white per the layout's
 * `android:textColor="@color/white"`.
 *
 * The layout declares `android:textSize="15dp"` — density-scaled, not
 * font-scale-scaled. [LegacyTextStyles.Large] uses `sp` because the rest of the
 * app does; the two agree at the default font scale and diverge only for users
 * who have enlarged system text, where the Compose version is the better
 * behaviour and the drift is accepted deliberately.
 */
@Composable
fun LegacyCircleBadge(
    text: String,
    badge: LegacyDescentBadge,
    modifier: Modifier = Modifier,
    size: Dp = 70.dp,
    textColor: Color = LegacyColors.White,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(badge.color, LegacyShapes.CircleBadge),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            style = LegacyTextStyles.Large,
            maxLines = 1,
        )
    }
}