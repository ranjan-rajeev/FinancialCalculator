package com.financialcalculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.financialcalculator.ui.theme.LegacyColors
import com.financialcalculator.ui.theme.LegacyShapes
import com.financialcalculator.ui.theme.LegacyTextStyles

/**
 * The "Calculate" button.
 *
 * The legacy button is a `TextView` with `@drawable/button_selector`, not a
 * `Button`, so it has no Material touch feedback — only the selector's pressed
 * and enabled states. Reproduced exactly:
 *
 * | State   | Fill      | Stroke        |
 * |---------|-----------|---------------|
 * | enabled | `#3F51B5` | 1dp `#303F9F` |
 * | pressed | `#303F9F` | 3dp `#3F51B5` |
 *
 * `item_button.xml` adds `padding="12dp"`, and the inline button row in
 * `content_emi_calculator.xml` adds `layout_marginTop="20dp"` with 5dp on each
 * side; both are the defaults here.
 */
@Composable
fun LegacyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    horizontalMargin: Dp = 5.dp,
    topMargin: Dp = 20.dp,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    textStyle: TextStyle = LegacyTextStyles.Default,
    onClickLabel: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val shape = LegacyShapes.Button

    // `button_selector` declares no `state_enabled="false"` branch, so a
    // disabled button keeps the enabled fill rather than greying out.
    val fill = if (pressed) LegacyColors.ColorPrimaryDark else LegacyColors.ColorPrimary
    val border = if (pressed) {
        BorderStroke(3.dp, LegacyColors.ColorPrimary)
    } else {
        BorderStroke(1.dp, LegacyColors.ColorPrimaryDark)
    }

    Text(
        text = text,
        modifier = modifier
            .padding(top = topMargin, start = horizontalMargin, end = horizontalMargin)
            .background(fill, shape)
            .border(border, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClickLabel = onClickLabel,
                onClick = onClick,
            )
            .padding(contentPadding),
        color = LegacyColors.White,
        textAlign = TextAlign.Center,
        style = textStyle,
        maxLines = 1,
    )
}