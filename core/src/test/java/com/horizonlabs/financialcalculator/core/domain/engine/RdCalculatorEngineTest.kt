package com.horizonlabs.financialcalculator.core.domain.engine

import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.util.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RdCalculatorEngineTest {

    private val engine = RdCalculatorEngine()

    private fun summary(key: String, vararg pairs: Pair<String, Any>): String {
        val result = engine.calculate(CalculatorInputValues(mapOf(*pairs)))
        assertTrue("expected success but got $result", result is Result.Success)
        return result.getOrThrow().summary!!.items.first { it.key == key }.value
    }

    /**
     * The RD annuity formula divides by the monthly rate, and the published config
     * sets `rate` minimum to 0, so a 0% deposit reached `0 / 0` and produced NaN.
     * That reached `BigDecimal.valueOf` and threw out of the formatter, taking the
     * screen down. With no interest the maturity is just the deposits.
     */
    @Test
    fun `zero interest yields the deposits and does not produce NaN`() {
        fun value(key: String) = summary(key, "monthlyDeposit" to 1000.0, "rate" to 0.0, "tenure" to 5.0, "tenureType" to "YEARS")

        assertEquals("₹60,000.00", value("totalInvested"))
        assertEquals("₹60,000.00", value("maturity"))
        assertEquals("₹0.00", value("totalInterest"))
    }

    @Test
    fun `published raw values back the deposits chart`() {
        val result = engine.calculate(
            CalculatorInputValues(
                mapOf("monthlyDeposit" to 1000.0, "rate" to 8.0, "tenure" to 5.0, "tenureType" to "YEARS")
            )
        ).getOrThrow()

        val raw = result.rawValues
        assertEquals(1000.0 * 60, raw["totalInvested"]!!, 0.01)
        assertEquals(raw["totalInterest"]!!, raw["maturity"]!! - raw["totalInvested"]!!, 0.01)
        assertTrue("maturity should exceed deposits at 8%", raw["maturity"]!! > raw["totalInvested"]!!)
    }

    @Test
    fun `months and years tenure agree`() {
        fun emiTenure(tenure: Double, type: String) =
            summary("maturity", "monthlyDeposit" to 1000.0, "rate" to 8.0, "tenure" to tenure, "tenureType" to type)

        assertEquals(emiTenure(5.0, "YEARS"), emiTenure(60.0, "MONTHS"))
    }

    @Test
    fun `missing inputs and empty tenure fail`() {
        assertTrue(engine.calculate(CalculatorInputValues(mapOf("rate" to 8.0, "tenure" to 5.0))) is Result.Failure)
        assertTrue(engine.calculate(CalculatorInputValues(mapOf("monthlyDeposit" to 1000.0, "tenure" to 5.0))) is Result.Failure)
        assertTrue(engine.calculate(CalculatorInputValues(mapOf("monthlyDeposit" to 1000.0, "rate" to 8.0))) is Result.Failure)
        assertTrue(
            engine.calculate(
                CalculatorInputValues(mapOf("monthlyDeposit" to 1000.0, "rate" to 8.0, "tenure" to 0.0))
            ) is Result.Failure
        )
    }
}
