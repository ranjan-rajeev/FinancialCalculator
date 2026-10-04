package com.horizonlabs.financialcalculator.core.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
        // `BigDecimal.valueOf` throws NumberFormatException on NaN and Infinity.
        // An engine that divides by a zero rate produces them, and a formatting
        // call must never be the thing that takes the screen down, so they are
        // rendered as an em dash instead.
        if (!value.isFinite()) return if (value.isNaN()) "—" else if (value > 0) "∞" else "-∞"
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

    /**
     * Calendar dates are carried through the app as `dd-MM-yyyy` text, matching the
     * retired Java app's `Util.DATE_FORMAT`. A plain string has no timezone, which
     * keeps a picked date stable across DST shifts and device locale changes.
     *
     * Compose Material's date picker works in UTC milliseconds, so the two
     * conversions below pin the text to midnight *UTC* rather than the device's
     * zone. Going through the local zone is what makes a picker that opens on the
     * right day land one day off.
     */
    const val DATE_PATTERN = "dd-MM-yyyy"

    private fun dateFormat() = SimpleDateFormat(DATE_PATTERN, Locale.US).apply {
        isLenient = false
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /** `05-10-2024` -> millis for 2024-10-05T00:00:00Z, or null if unparseable. */
    fun dateStringToUtcMillis(date: String): Long? =
        runCatching { dateFormat().parse(date.trim())?.time }.getOrNull()

    /** Millis for 2024-10-05T00:00:00Z -> `05-10-2024`. */
    fun utcMillisToDateString(millis: Long): String = dateFormat().format(Date(millis))

    /** Today's date as `dd-MM-yyyy`, for resolving a `"TODAY"` default. */
    fun todayDateString(): String = utcMillisToDateString(todayUtcMillis())

    /**
     * Resolves a published date bound, which may be a literal `dd-MM-yyyy` or the
     * `TODAY` sentinel. Static hosted JSON cannot bake in the current day, so
     * "no later than today" has to be expressed symbolically.
     */
    fun resolveDateBound(bound: String?): Long? = when {
        bound == null -> null
        bound.trim().equals(TODAY_SENTINEL, ignoreCase = true) -> todayUtcMillis()
        else -> dateStringToUtcMillis(bound)
    }

    const val TODAY_SENTINEL = "TODAY"

    fun todayUtcMillis(): Long {
        val now = Calendar.getInstance()
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }

    /**
     * `12 Sep 2026, 14:05` for a stored history timestamp. Deliberately *not* [dateFormat]:
     * those values are wall-clock instants, so they must render in the device's zone rather
     * than be pinned to UTC midnight.
     */
    fun formatTimestamp(millis: Long): String =
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(millis))

    /** `Today` / `Yesterday` / a date, for grouping history rows under day headers. */
    fun formatRelativeDay(millis: Long): String {
        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayMillis = 86_400_000L
        val daysApart = ((midnight.timeInMillis - millis) / dayMillis).toInt()
        return when (daysApart) {
            0 -> "Today"
            1 -> "Yesterday"
            in 2..6 -> SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(millis))
            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
        }
    }
}
