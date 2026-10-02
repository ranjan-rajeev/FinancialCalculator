package com.financialcalculator.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.financialcalculator.ui.theme.LegacyColors
import com.financialcalculator.ui.theme.LegacyElevation
import com.financialcalculator.ui.theme.LegacyShapes

/**
 * The card used by every calculator screen.
 *
 * Reproduces the `androidx.cardview.widget.CardView` that `content_*.xml`
 * declares eight times over: `cardCornerRadius="8dp"`, `cardElevation="8dp"`,
 * `layout_margin="12dp"`, no explicit background (so the AppCompat light-theme
 * default, opaque white).
 *
 * ## Known deviation: shadow colour
 *
 * `CardView` derives its shadow colour from the card background, which for a
 * white card produces a soft neutral grey. Compose's `Surface` shadow is black
 * unless told otherwise, so the halo here is marginally darker than the XML.
 * The parity test in `LegacyCardParityTest` measures the delta; it is left at
 * the Compose default rather than hand-tuned until the measurement justifies a
 * specific colour.
 */
@Composable
fun LegacyCard(
    modifier: Modifier = Modifier,
    shape: Shape = LegacyShapes.Card,
    elevation: Dp = LegacyElevation.Card,
    containerColor: Color = LegacyColors.White,
    contentColor: Color = LegacyColors.Black,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        shadowElevation = elevation,
    ) {
        content()
    }
}

/**
 * [LegacyCard] with the `layout_margin="12dp"` from `content_*.xml` and the
 * `android:padding="10dp"` from its inner `LinearLayout` already applied.
 *
 * The margin is applied outside the shadow so it matches `CardView`, whose
 * layout margin also sits outside the drawn background.
 */
@Composable
fun LegacyCalculatorCard(
    modifier: Modifier = Modifier,
    outerMargin: Dp = 12.dp,
    innerPadding: PaddingValues = PaddingValues(10.dp),
    content: @Composable () -> Unit,
) {
    LegacyCard(modifier = modifier.padding(outerMargin)) {
        Surface(
            color = androidx.compose.ui.graphics.Color.Transparent,
            contentColor = LocalContentColor.current,
        ) {
            androidx.compose.foundation.layout.Box(Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}