package com.horizonlabs.financialcalculator.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryInputRoundTripTest {

    @Test
    fun `integral numbers round-trip without a trailing decimal point`() {
        // Gson decodes every JSON number into a Double; the calculator inputs render raw text,
        // so a stored 500000 must not come back as "500000.0".
        val restored = decodeInputValues(encodeInputValues(mapOf("principal" to "500000")))

        assertEquals("500000", restored["principal"])
    }

    @Test
    fun `genuinely fractional numbers keep their fraction`() {
        val restored = decodeInputValues(encodeInputValues(mapOf("rate" to 8.75)))

        assertEquals("8.75", restored["rate"])
    }

    @Test
    fun `text values pass through untouched`() {
        val restored = decodeInputValues(
            encodeInputValues(mapOf("tenureType" to "YEARS", "startDate" to "05-10-2024"))
        )

        assertEquals("YEARS", restored["tenureType"])
        assertEquals("05-10-2024", restored["startDate"])
    }

    @Test
    fun `blank or malformed payloads decode to an empty map instead of throwing`() {
        assertEquals(emptyMap<String, Any>(), decodeInputValues(""))
        assertEquals(emptyMap<String, Any>(), decodeInputValues("not json"))
    }
}