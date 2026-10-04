package com.horizonlabs.financialcalculator.core.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

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
}
