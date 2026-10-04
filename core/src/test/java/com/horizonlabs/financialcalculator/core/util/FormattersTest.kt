package com.horizonlabs.financialcalculator.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Indian digit grouping is easy to regress because it depends on the pattern
 * `#,##,##0` combined with `en-IN` symbols; a plain `en-US` locale silently
 * produces western grouping instead.
 */
class FormattersTest {

    private val original = Locale.getDefault()

    @Test
    fun `currency uses indian grouping regardless of device locale`() {
        Locale.setDefault(Locale.US)
        try {
            assertEquals("₹10,41,387.88", Formatters.formatCurrencyINR(1041387.88))
            assertEquals("₹4,339.12", Formatters.formatCurrencyINR(4339.12))
            assertEquals("₹99,99,999.00", Formatters.formatCurrencyINR(9999999.0))
        } finally {
            Locale.setDefault(original)
        }
    }

    @Test
    fun `currency rounds to two decimals`() {
        assertEquals("₹1,00,000.01", Formatters.formatCurrencyINR(100000.005))
        assertEquals("₹1,00,000.00", Formatters.formatCurrencyINR(99999.999))
    }

    @Test
    fun `no decimal currency drops paise`() {
        assertEquals("₹10,41,388", Formatters.formatCurrencyINRNoDecimal(1041387.88))
    }

    @Test
    fun `percentage keeps two decimals`() {
        assertEquals("51.99%", Formatters.formatPercentage(51.9851))
    }

    /**
     * Regression: `formatCompact` used to fall through to `Double.toString()`, which
     * rendered a rupee total as "5.413878800386408 L".
     */
    @Test
    fun `compact never leaks full double precision`() {
        assertEquals("5.41 L", Formatters.formatCompact(541387.8800386408))
        assertEquals("10.41 L", Formatters.formatCompact(1041387.8800386408))
        assertEquals("1.04 Cr", Formatters.formatCompact(10413878.8))
        assertEquals("4.34 K", Formatters.formatCompact(4339.12))
        assertEquals("999.4", Formatters.formatCompact(999.4))
        assertEquals("-5.41 L", Formatters.formatCompact(-541387.88))
    }

    @Test
    fun `parse accepts grouped and prefixed input`() {
        assertEquals(1041387.88, Formatters.parseCurrency("₹10,41,387.88"), 0.001)
        assertEquals(500000.0, Formatters.parseCurrency("500000"), 0.001)
        assertEquals(0.0, Formatters.parseCurrency(""), 0.001)
        assertEquals(8.5, Formatters.parsePercentage("8.5%"), 0.001)
    }

    /**
     * A date is carried as `dd-MM-yyyy` text and converted to UTC midnight for the
     * Material date picker. If either side used the device zone instead, a picker
     * opened on the 1st would highlight the 28th or 2nd depending on DST.
     */
    @Test
    fun `date round trips through utc millis unchanged`() {
        listOf("01-01-2024", "05-10-2024", "28-02-2024", "31-12-1999", "29-02-2024")
            .forEach { date ->
                val millis = Formatters.dateStringToUtcMillis(date)
                assertNotNull("expected $date to parse", millis)
                assertEquals(date, Formatters.utcMillisToDateString(millis!!))
            }
    }

    @Test
    fun `date parsing is pinned to utc midnight`() {
        val millis = Formatters.dateStringToUtcMillis("05-10-2024")!!
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
        assertEquals(2024, utc.get(Calendar.YEAR))
        assertEquals(Calendar.OCTOBER, utc.get(Calendar.MONTH))
        assertEquals(5, utc.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, utc.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `invalid and sentinel dates are rejected`() {
        assertNull(Formatters.dateStringToUtcMillis(""))
        assertNull(Formatters.dateStringToUtcMillis("TODAY"))
        assertNull(Formatters.dateStringToUtcMillis("31-02-2024"))
        assertNull(Formatters.dateStringToUtcMillis("2024-10-05"))
    }

    /**
     * `BigDecimal.valueOf` throws `NumberFormatException` on NaN and Infinity, so an
     * engine returning a non-finite figure used to crash the screen instead of
     * rendering a result.
     */
    @Test
    fun `non finite values render instead of throwing`() {
        assertEquals("₹—", Formatters.formatCurrencyINR(Double.NaN))
        assertEquals("₹∞", Formatters.formatCurrencyINR(Double.POSITIVE_INFINITY))
        assertEquals("₹-∞", Formatters.formatCurrencyINR(Double.NEGATIVE_INFINITY))
        assertEquals("—%", Formatters.formatPercentage(Double.NaN))
        // Every magnitude comparison against NaN is false, so compact falls through
        // to the plain decimal path rather than picking a Cr/L/K suffix.
        assertEquals("—", Formatters.formatCompact(Double.NaN))
        assertTrue(Formatters.formatCurrencyINRNoDecimal(Double.NaN).endsWith("—"))
        assertTrue(Formatters.formatNumber(Double.POSITIVE_INFINITY).endsWith("∞"))
    }

    @Test
    fun `today matches the device calendar day`() {
        val expected = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val formatter = SimpleDateFormat(Formatters.DATE_PATTERN, Locale.US)
        assertEquals(formatter.format(expected.time), Formatters.todayDateString())
    }
}
