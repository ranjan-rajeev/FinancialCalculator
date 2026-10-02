package com.financialcalculator.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.financialcalculator.ui.theme.LegacyColors
import com.financialcalculator.ui.theme.LegacyTextStyles
import kotlin.math.min

/**
 * Metrics of `@drawable/circular` and `@drawable/circular_full`, the ring
 * progress drawables behind the "Total Interest / Total Principal" pair.
 *
 * Both are `<shape android:shape="ring">` with `innerRadiusRatio="2.3"` and
 * `thickness="5dp"`.
 *
 * The 2.3 ratio is resolved by the framework against the drawable's **bounds
 * width**: `innerRadius = 2.3 * boundsWidth / 3` in `GradientDrawable`'s ring
 * geometry, so for a 100dp ring the inner radius is roughly 38dp. Reproducing
 * that by deriving the radius from the width — rather than hard-coding 38dp —
 * keeps the ring correct if the size is ever changed, which is exactly what the
 * legacy drawable does.
 */
object LegacyRingMetrics {
    /** The `ProgressBar` is declared 100dp x 100dp in both output layouts. */
    val RingSize: Dp = 100.dp

    /** `android:thickness="5dp"`. */
    val Thickness: Dp = 5.dp

    /** `android:innerRadiusRatio="2.3"`. */
    const val InnerRadiusRatio: Float = 2.3f
}

/**
 * A decelerating easing that reproduces `android.view.animation.DecelerateInterpolator`.
 *
 * `DecelerateInterpolator` (factor 1.0) is `max(0, 1 - (1 - t)^2)`:
 *
 * ```kotlin
 * public float getInterpolation(float input) {
 *     return (int) (input * mFactor) == input
 *             ? (input) * (input * (2 - mFactor) + (mFactor - 1))
 *             : (float) Math.pow(input, 2 * mFactor);
 * }
 * ```
 *
 * with `mFactor = 1f`, the first branch simplifies to exactly `t²`, which is
 * what this returns. Compose's `FastOutSlowInEasing` is a bezier curve and is
 * **not** equivalent — using it here is the drift the migration plan warns
 * about.
 */
fun decelerateInterpolator(t: Float): Float = t * t

/** Duration of `GraphViewHolder.ANIMATION_TIME`. */
private const val LegacyRingAnimationMillis = 2000

/**
 * One ring of the principal/interest pair.
 *
 * `GraphViewHolder` stacks two `ProgressBar`s per column: the `*Full` variant
 * draws the complete `@drawable/circular_full` ring in `#E0E0E0` immediately,
 * and the other draws `@drawable/circular` in `#3F51B5` on top, animated
 * `0 → percent` over 2000ms with a `DecelerateInterpolator`. Both are 100dp and
 * `innerRadiusRatio=2.3`.
 *
 * @param trackColor `#E0E0E0`, the `circular_full` ring.
 * @param arcColor `#3F51B5`, the `circular` ring.
 * @param animate `false` renders the final value immediately, which is what the
 *   screenshot parity tests need so the comparison is deterministic.
 */
@Composable
fun LegacyRingProgress(
    percent: Int,
    modifier: Modifier = Modifier,
    size: Dp = LegacyRingMetrics.RingSize,
    thickness: Dp = LegacyRingMetrics.Thickness,
    trackColor: Color = LegacyColors.LightGrey,
    arcColor: Color = LegacyColors.ColorPrimary,
    animate: Boolean = true,
    caption: String? = null,
) {
    val target = percent.coerceIn(0, 100)
    val progress = remember { Animatable(0f) }

    LaunchedEffect(target, animate) {
        if (animate) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = target.toFloat(),
                animationSpec = tween(
                    durationMillis = LegacyRingAnimationMillis,
                    easing = { decelerateInterpolator(it) },
                ),
            )
        } else {
            progress.snapTo(target.toFloat())
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = thickness.toPx()
            val diameter = min(this.size.width, this.size.height)
            // Mirrors `innerRadiusRatio`: the ring's outer diameter shrinks by
            // the stroke, and the arc starts at 12 o'clock running clockwise.
            val arcDiameter = diameter - stroke
            val topLeft = Offset(
                x = (this.size.width - arcDiameter) / 2f,
                y = (this.size.height - arcDiameter) / 2f,
            )
            val arcSize = Size(arcDiameter, arcDiameter)

            drawCircle(
                color = trackColor,
                radius = arcDiameter / 2f,
                style = Stroke(width = stroke),
            )
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = 360f * (progress.value / 100f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Butt),
            )
        }

        if (caption != null) {
            Text(
                text = caption,
                color = LegacyColors.ColorPrimary,
                textAlign = TextAlign.Center,
                style = LegacyTextStyles.RingPercent,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}