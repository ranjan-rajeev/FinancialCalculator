package com.financialcalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.financialcalculator.ui.theme.LegacyColors
import com.financialcalculator.ui.theme.LegacyShapes
import com.financialcalculator.ui.theme.LegacyTextStyles

/**
 * Geometry of `@drawable/corner_shape`, which is the background of every
 * calculator `EditText`.
 *
 * The drawable is a `layer-list` whose single `<item android:top="10dp">`
 * insets the shape 10dp from the top of the 50dp field. The visible box is
 * therefore the bottom 40dp, and the 10dp strip above it is where
 * `TextInputLayout` floats its hint — the classic Material "notch" trick. The
 * `<padding>` inside the shape supplies the text inset.
 *
 * These are named rather than inlined so the field, the spinner and the parity
 * tests all measure against the same numbers.
 */
object LegacyFieldMetrics {
    /** `android:layout_height="50dp"` on the `EditText`. */
    val FieldHeight: Dp = 50.dp

    /** The `<item android:top="10dp">` inset on `corner_shape`. */
    val NotchHeight: Dp = 10.dp

    /** Height of the visible outlined box: 50dp minus the notch. */
    val BoxHeight: Dp = FieldHeight - NotchHeight

    /** `<stroke android:width="1dp">`. */
    val BorderWidth: Dp = 1.dp

    /** `<padding android:left="8dp" android:right="8dp" android:top="8dp">`. */
    val InnerPadding: Dp = 8.dp

    /** `<corners android:radius="5dp">`. */
    val CornerRadius: Dp = 5.dp
}

/**
 * A calculator text field that matches `item_edittext.xml`.
 *
 * The legacy widget is a `TextInputLayout` wrapping a bare `EditText`:
 *
 * - 50dp tall, `#DDFFFFFF` fill, 5dp radius, 1dp stroke
 * - `#3F51B5` stroke when focused, `#c9c5c5` otherwise
 * - hint `#929292`, floated above the box while focused or filled
 * - input text `#000000`
 *
 * On focus loss the legacy `EditTextViewHolder` rewrites the text through
 * `Util.getCommaSeparated(Util.removeComma(...))`, so the value gains Indian
 * lakh/crore grouping once the user leaves the field. That behaviour is kept
 * here by calling the same Java formatter; Phase 2 replaces both calls with the
 * pure-Kotlin `IndianNumberFormat` once it is proven byte-identical.
 *
 * The floating hint is drawn by hand rather than via Material3's
 * `OutlinedTextField`, because `TextInputLayout`'s hint animation, notch cut-out
 * and error placement do not have a Material3 equivalent that can be pinned to
 * these exact pixel dimensions.
 */
@Composable
fun LegacyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Number,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textAlign: TextAlign = TextAlign.Start,
    maxLength: Int = Int.MAX_VALUE,
    textStyle: TextStyle = LegacyTextStyles.Default,
    focusRequester: FocusRequester? = null,
    onFocusLost: (String) -> String = { it },
) {
    var focused by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        // The floating label occupies the 10dp notch. It stays in place when the
        // field is focused or non-empty and collapses into the box otherwise.
        val floated = focused || value.isNotEmpty()
        if (floated) {
            Text(
                text = label,
                modifier = Modifier
                    .offset(y = -LegacyFieldMetrics.InnerPadding)
                    .padding(start = LegacyFieldMetrics.InnerPadding),
                color = if (isError) LegacyColors.RedDescent else LegacyColors.DescriptionText,
                style = LegacyTextStyles.VerySmall,
                maxLines = 1,
            )
        }

        Box(
            modifier = Modifier
                .offset(y = LegacyFieldMetrics.NotchHeight)
                .height(LegacyFieldMetrics.BoxHeight)
                .fillMaxWidth()
                .background(LegacyColors.FieldFill, LegacyShapes.Field)
                .border(
                    width = LegacyFieldMetrics.BorderWidth,
                    color = when {
                        isError -> LegacyColors.RedDescent
                        focused -> LegacyColors.ColorPrimary
                        else -> LegacyColors.FieldBorder
                    },
                    shape = LegacyShapes.Field,
                ),
        ) {
            if (value.isEmpty() && !focused) {
                // Collapsed hint, sitting inside the box where TextInputLayout
                // places it.
                Text(
                    text = label,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = LegacyFieldMetrics.InnerPadding),
                    color = LegacyColors.DescriptionText,
                    style = textStyle,
                    maxLines = 1,
                )
            }

            BasicTextField(
                value = value,
                onValueChange = { incoming ->
                    val limited = if (incoming.length > maxLength) {
                        incoming.takeLast(maxLength)
                    } else {
                        incoming
                    }
                    onValueChange(limited)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = LegacyFieldMetrics.BoxHeight)
                    .padding(
                        start = LegacyFieldMetrics.InnerPadding,
                        end = LegacyFieldMetrics.InnerPadding,
                        top = LegacyFieldMetrics.InnerPadding,
                    )
                    .then(
                        focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier,
                    )
                    .onFocusChanged { state ->
                        val wasFocused = focused
                        focused = state.isFocused
                        if (wasFocused && !state.isFocused) {
                            onValueChange(onFocusLost(value))
                        }
                    },
                enabled = enabled,
                textStyle = textStyle.copy(
                    color = LegacyColors.Black,
                    textAlign = textAlign,
                ),
                cursorBrush = SolidColor(LegacyColors.ColorPrimary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = visualTransformation,
                singleLine = true,
            )
        }
    }

    if (isError && errorText != null) {
        Text(
            text = errorText,
            modifier = Modifier.padding(start = LegacyFieldMetrics.InnerPadding, top = 2.dp),
            color = LegacyColors.RedDescent,
            style = LegacyTextStyles.VerySmall,
        )
    }
}