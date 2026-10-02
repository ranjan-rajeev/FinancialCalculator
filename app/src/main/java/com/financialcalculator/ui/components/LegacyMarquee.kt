package com.financialcalculator.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.financialcalculator.ui.theme.LegacyColors
import com.financialcalculator.ui.theme.LegacyTextStyles

/**
 * The dashboard section header from `item_header.xml`.
 *
 * The legacy `TextView` is:
 *
 * ```xml
 * style="@style/TextViewStyle.Large"     <!-- 15sp, but re-coloured below -->
 * android:layout_margin="10dp"
 * android:gravity="center"
 * android:singleLine="true"
 * android:ellipsize="marquee"
 * android:marqueeRepeatLimit="marquee_forever"
 * android:textColor="@color/colorPrimaryDark"
 * android:textStyle="bold"
 * ```
 *
 * ## Why this is not a plain `Text`
 *
 * A `TextView` only starts its marquee once it is selected, which in practice
 * meant the dashboard headers never scrolled at all — `textIsSelectable="true"`
 * was present but the adapters never focused the view, and `View` marquee also
 * requires `isFocused() || isSelected()`.
 *
 * [BasicMarquee] always animates. That is a deliberate behaviour change: it
 * produces the header animation the layout clearly intended, but it is *not*
 * pixel-for-pixel parity with what shipped. It is called out here and in
 * MIGRATION_PLAN.md §5 rather than left to be discovered as a regression.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LegacyMarquee(
    text: String,
    modifier: Modifier = Modifier,
    iterations: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier
            .padding(10.dp)
            .basicMarquee(iterations = iterations),
        color = LegacyColors.ColorPrimaryDark,
        style = LegacyTextStyles.SectionHeader,
        maxLines = 1,
    )
}