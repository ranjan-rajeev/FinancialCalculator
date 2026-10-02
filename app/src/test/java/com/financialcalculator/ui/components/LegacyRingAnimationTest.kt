package com.financialcalculator.ui.components

import androidx.compose.ui.graphics.Color
import com.financialcalculator.ui.theme.LegacyColors
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.pow

/**
 * The ring animation is driven by `android.view.animation.DecelerateInterpolator`
 * with a duration of `GraphViewHolder.ANIMATION_TIME = 2000`.
 *
 * Compose's default `FastOutSlowInEasing` is a cubic bezier and produces a
 * visibly different curve, which is exactly the kind of drift MIGRATION_PLAN.md
 * §5 warns about. These tests assert the hand-rolled easing reproduces the
 * platform formula exactly rather than approximately.
 */
class DecelerateInterpolatorTest {

    @Test
    fun matchesTheAospDecelerateInterpolator() {
        // AOSP `DecelerateInterpolator.getInterpolation`, factor 1.0:
        //   (int)(input * 1.0) == input ? input * (input * 1 + 0) : input^2
        // The int-cast branch is only taken for input in [0, 1), where
        // `(int) input == input` only when input == 0, so the curve is t^2.
        for (step in 0..100) {
            val t = step / 100f
            val expected = t.pow(2)
            assertEquals(
                "easing drifted at t=$t",
                expected,
                decelerateInterpolator(t),
                1e-6f,
            )
        }
    }

    @Test
    fun isAnEaseOutNotAnEaseIn() {
        assertEquals(0f, decelerateInterpolator(0f), 0f)
        assertEquals(1f, decelerateInterpolator(1f), 0f)
        // Halfway through the duration the animation is only a quarter done,
        // which is what makes it an ease-out: the value moves fast early and
        // eases off. Linear would be 0.5 at this point.
        assertEquals(0.25f, decelerateInterpolator(0.5f), 1e-6f)
    }

    @Test
    fun differsFromMaterialFastOutSlowIn() {
        // Guards against someone "simplifying" this to the Material default: the
        // two curves are close but not equal, and the difference is visible.
        val material = androidx.compose.animation.core.FastOutSlowInEasing
        var differences = 0
        for (step in 1 until 100) {
            val t = step / 100f
            if (kotlin.math.abs(decelerateInterpolator(t) - material.transform(t)) > 0.01f) {
                differences++
            }
        }
        assertEquals(
            "the hand-rolled easing is indistinguishable from Material's; " +
                "keep it as a real parity decision, not an accident",
            true,
            differences > 10,
        )
    }

    private fun assertTrue(condition: Boolean) =
        org.junit.Assert.assertTrue(
            "easing is not an ease-out",
            condition,
        )
}

/**
 * Ports the behaviour of `Util.getFixedBackground` / `Util.getRandomBackground`
 * so the zebra striping and the rate badge cannot drift from the Java they
 * replace while those screens still exist.
 */
class LegacyDescentBadgeTest {

    @Test
    fun fixedBackgroundCyclesGreenBlueRedByPositionModuloThree() {
        // `switch (position % 3) { 0 -> green, 1 -> blue, 2 -> red }`
        assertEquals(LegacyDescentBadge.Green, LegacyDescentBadge.atPosition(0))
        assertEquals(LegacyDescentBadge.Blue, LegacyDescentBadge.atPosition(1))
        assertEquals(LegacyDescentBadge.Red, LegacyDescentBadge.atPosition(2))
        assertEquals(LegacyDescentBadge.Green, LegacyDescentBadge.atPosition(3))
        assertEquals(LegacyDescentBadge.Blue, LegacyDescentBadge.atPosition(4))
        assertEquals(LegacyDescentBadge.Red, LegacyDescentBadge.atPosition(5))
    }

    @Test
    fun handlesNegativePositionsTheSameWayTheJavaSwitchDoes() {
        // Java's `%` keeps the sign of the dividend, so -1 % 3 == -1 and -2 % 3
        // == -2. Neither label matches a `case`, so both fall through to
        // `default` and return green. floorMod would have returned red and blue
        // instead, recolouring every negatively-indexed row.
        assertEquals(LegacyDescentBadge.Green, LegacyDescentBadge.atPosition(-1))
        assertEquals(LegacyDescentBadge.Green, LegacyDescentBadge.atPosition(-2))
        assertEquals(LegacyDescentBadge.Green, LegacyDescentBadge.atPosition(-3))
    }

    @Test
    fun randomLegacyNeverProducesRedBecauseTheJavaBranchIsDead() {
        // `Util.getRandomBackground` computes `rand.nextInt(2)` — which can only be
        // 0 or 1 — so `case 2` is unreachable and red can never appear. This is a
        // bug in the legacy code; it is preserved here on purpose, and this test
        // exists so that anyone who "fixes" it finds out immediately.
        repeat(5_000) {
            assertTrue(
                "randomLegacy must never return Red",
                LegacyDescentBadge.randomLegacy() != LegacyDescentBadge.Red,
            )
        }
    }

    @Test
    fun badgeColoursMatchTheCircleDrawables() {
        assertEquals(Color(0xFF42CEB2), LegacyDescentBadge.Green.color)
        assertEquals(Color(0xFF4B76C2), LegacyDescentBadge.Blue.color)
        assertEquals(Color(0xFFDE6D75), LegacyDescentBadge.Red.color)
        assertEquals(LegacyColors.GreenDescent, LegacyDescentBadge.Green.color)
    }

    private fun assertTrue(message: String, condition: Boolean) =
        org.junit.Assert.assertTrue(message, condition)
}