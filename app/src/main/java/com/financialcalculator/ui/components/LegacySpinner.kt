package com.financialcalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.financialcalculator.ui.theme.LegacyColors
import com.financialcalculator.ui.theme.LegacyShapes
import com.financialcalculator.ui.theme.LegacyTextStyles

/**
 * A spinner that matches `item_spinner.xml` plus `@drawable/spinner_backgrond`.
 *
 * The drawable is `corner_shape` with a second `layer-list` item appended in the
 * unfocused state: `android:gravity="right|center_vertical"` with
 * `@android:drawable/arrow_down_float`. That means the dropdown arrow is present
 * **only while unfocused** — focusing the field drops the arrow — which is a
 * quirk of the drawable rather than an intention, and is reproduced here so the
 * pixels match.
 *
 * `item_spinner.xml` also puts a `colorPrimary` caption above the box with an
 * 8dp top margin; that is the `title` parameter.
 */
@Composable
fun LegacySpinner(
    selectedIndex: Int,
    options: List<String>,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }

    val selected = options.getOrNull(selectedIndex)
    val shape = LegacyShapes.Field

    Box(modifier = modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier.padding(top = 8.dp),
                color = LegacyColors.ColorPrimary,
                style = LegacyTextStyles.Default,
            )
        }

        Box(
            modifier = Modifier
                .offset(y = LegacyFieldMetrics.NotchHeight)
                .height(LegacyFieldMetrics.BoxHeight)
                .fillMaxWidth()
                .background(LegacyColors.FieldFill, shape)
                .border(
                    width = LegacyFieldMetrics.BorderWidth,
                    color = if (focused) LegacyColors.ColorPrimary else LegacyColors.FieldBorder,
                    shape = shape,
                )
                .clickable(enabled = enabled) { expanded = true }
                .onFocusChanged { focused = it.isFocused },
        ) {
            Row(
                modifier = Modifier.padding(
                    start = LegacyFieldMetrics.InnerPadding,
                    end = LegacyFieldMetrics.InnerPadding,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selected.orEmpty(),
                    modifier = Modifier.weight(1f),
                    color = LegacyColors.Black,
                    style = LegacyTextStyles.Default,
                    maxLines = 1,
                )
                if (!focused) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = LegacyColors.Black,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option, style = LegacyTextStyles.Default) },
                    onClick = {
                        onSelected(index)
                        expanded = false
                    },
                )
            }
        }
    }
}

/**
 * `spinner_backgrond` uses `arrow_down_float`, whose intrinsic size differs from
 * the Material icon substituted above. Exposed so the parity test can compare
 * the two without depending on the platform drawable.
 */
internal val LegacySpinnerArrowSizeDp = 20