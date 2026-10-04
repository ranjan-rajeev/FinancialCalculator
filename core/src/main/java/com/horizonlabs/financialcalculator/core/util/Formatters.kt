package com.horizonlabs.financialcalculator.core.util

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Number and currency formatting for the UI.
 *
 * Indian digit grouping is computed here rather than delegated to
 * `NumberFormat`/`DecimalFormat`. Both `#,##,##0` and
 * `NumberFormat.getNumberInstance(en-IN)` fall back to western grouping
 * (1,041,387.88) depending on the JDK and ICU data shipped by the device, so
 * rupee amounts must be grouped explicitly.
 */
object Formatters {

    /** 1041387 -> "10,41,387" */
    private fun groupIndian(digits: String): String {
        if (digits.length <= 3) return digits
        val lastThree = digits.takeLast(3)
        var rest = digits.dropLast(3)
        val groups = mutableListOf<String>()
        while (rest.length > 2) {
            groups.add(rest.takeLast(2))
            rest = rest.dropLast(2)
        }
        if (rest.isNotEmpty()) groups.add(rest)
        return groups.reversed().joinToString(",") + "," + lastThree
    }

    private fun decimal(value: Double, minFrac: Int, maxFrac: Int): String {
        val rounded = BigDecimal.valueOf(value).setScale(maxFrac, RoundingMode.HALF_UP)
        val plain = rounded.abs().toPlainString()
        val integerPart = plain.substringBefore('.')
        var fractionPart = if (maxFrac == 0) "" else plain.substringAfter('.', "")
        if (fractionPart.length < minFrac) fractionPart = fractionPart.padEnd(minFrac, '0')
        if (fractionPart.length > minFrac) fractionPart = fractionPart.trimEnd('0')

        return buildString {
            if (rounded.signum() < 0) append('-')
            append(groupIndian(integerPart))
            if (fractionPart.isNotEmpty()) {
                append('.')
                append(fractionPart)
            }
        }
    }

    /** Exact rupee amount with Indian grouping, e.g. `₹10,41,387.88`. */
    fun formatCurrencyINR(value: Double): String =
        "${Constants.CURRENCY_SYMBOL}${decimal(value, 2, 2)}"

    fun formatCurrencyINRExact(value: Double): String = formatCurrencyINR(value)

    fun formatCurrencyINRNoDecimal(value: Double): String =
        "${Constants.CURRENCY_SYMBOL}${decimal(value, 0, 0)}"

    fun formatPercentage(value: Double): String = "${decimal(value, 2, 2)}%"

    /**
     * Abbreviated magnitude for tight spaces such as dashboard tiles.
     * Financial results should use [formatCurrencyINR] so the user sees the real amount.
     */
    fun formatCompact(value: Double): String {
        val magnitude = kotlin.math.abs(value)
        val sign = if (value < 0) "-" else ""
        return when {
            magnitude >= 1_00_00_000 -> "$sign${decimal(magnitude / 1_00_00_000, 0, 2)} Cr"
            magnitude >= 1_00_000 -> "$sign${decimal(magnitude / 1_00_000, 0, 2)} L"
            magnitude >= 1_000 -> "$sign${decimal(magnitude / 1_000, 0, 2)} K"
            else -> decimal(magnitude, 0, 2)
        }
    }

    fun formatNumber(value: Double, decimals: Int = 0): String = decimal(value, decimals, decimals)

    fun parseCurrency(input: String): Double {
        val cleaned = input.removeCurrencySymbol().replace(",", "").trim()
        return if (cleaned.isEmpty()) 0.0 else cleaned.toDoubleOrNull() ?: 0.0
    }

    fun parsePercentage(input: String): Double {
        val cleaned = input.replace("%", "").trim()
        return if (cleaned.isEmpty()) 0.0 else cleaned.toDoubleOrNull() ?: 0.0
    }

    fun roundToDecimals(value: Double, decimals: Int): Double =
        BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP).toDouble()
}
