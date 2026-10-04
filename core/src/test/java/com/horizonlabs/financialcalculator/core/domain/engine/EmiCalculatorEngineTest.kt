package com.horizonlabs.financialcalculator.core.domain.engine

import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.util.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The EMI figures were captured from the retired Java calculator and recorded in
 * `docs/baseline/GOLDEN_VALUES.md`; these lock them in so a formula or
 * formatting change cannot quietly move the numbers.
 *
 * Note the currency symbol is a prefix here (`₹13,215.07`) where the legacy app
 * appended it (`13,215.07₹`). That difference is deliberate and already accepted.
 */
class EmiCalculatorEngineTest {

    private val engine = EmiCalculatorEngine()

    private fun run(vararg pairs: Pair<String, Any>) = engine.calculate(
        CalculatorInputValues(mapOf(*pairs))
    )

    private fun summary(key: String, vararg pairs: Pair<String, Any>): String {
        val result = run(*pairs)
        assertTrue("expected success but got $result", result is Result.Success)
        return result.getOrThrow().summary!!.items.first { it.key == key }.value
    }

    /** Principal 1000000, rate 10%, tenure 10 years — the documented golden input. */
    private fun golden(key: String) = summary(
        key,
        "principal" to 1000000.0, "rate" to 10.0, "tenure" to 10.0, "tenureType" to "YEARS"
    )

    @Test
    fun `matches the golden emi figures`() {
        assertEquals("₹13,215.07", golden("emi"))
        assertEquals("₹5,85,808.84", golden("totalInterest"))
        assertEquals("₹15,85,808.84", golden("totalPayable"))
        assertEquals("36.94%", golden("interestPercentage"))
        assertEquals("63.06%", golden("principalPercentage"))
    }

    /** The values verified on-device in the rewrite, before this engine gained dates. */
    @Test
    fun `matches the on-device verified figures`() {
        fun summary20(key: String) = summary(
            key,
            "principal" to 500000.0, "rate" to 8.5, "tenure" to 20.0, "tenureType" to "YEARS"
        )
        assertEquals("₹4,339.12", summary20("emi"))
        assertEquals("₹5,41,387.88", summary20("totalInterest"))
        assertEquals("₹10,41,387.88", summary20("totalPayable"))
        assertEquals("51.99%", summary20("interestPercentage"))
    }

    @Test
    fun `months and years tenure agree`() {
        val inYears = summary("emi", "principal" to 500000.0, "rate" to 8.5, "tenure" to 30.0, "tenureType" to "YEARS")
        val inMonths = summary("emi", "principal" to 500000.0, "rate" to 8.5, "tenure" to 360.0, "tenureType" to "MONTHS")
        assertEquals(inYears, inMonths)
    }

    @Test
    fun `breakdown month labels advance from the first instalment date`() {
        val result = run(
            "principal" to 500000.0, "rate" to 8.5, "tenure" to 1.0,
            "tenureType" to "YEARS", "startDate" to "05-10-2024"
        ).getOrThrow()

        val months = result.breakdown.single().children.map { it.period }
        assertEquals(
            listOf("Oct 2024", "Nov 2024", "Dec 2024", "Jan 2025", "Feb 2025", "Mar 2025",
                "Apr 2025", "May 2025", "Jun 2025", "Jul 2025", "Aug 2025", "Sep 2025"),
            months
        )
    }

    @Test
    fun `a december start rolls the year over correctly`() {
        val result = run(
            "principal" to 120000.0, "rate" to 12.0, "tenure" to 1.0,
            "tenureType" to "YEARS", "startDate" to "15-12-2024"
        ).getOrThrow()

        val months = result.breakdown.single().children.map { it.period }
        assertEquals("Dec 2024", months.first())
        assertEquals("Nov 2025", months.last())
    }

    @Test
    fun `an absent or unparseable date still yields real month labels`() {
        listOf(
            mapOf("principal" to 500000.0, "rate" to 8.5, "tenure" to 1.0, "tenureType" to "YEARS"),
            mapOf("principal" to 500000.0, "rate" to 8.5, "tenure" to 1.0, "tenureType" to "YEARS", "startDate" to "garbage"),
            mapOf("principal" to 500000.0, "rate" to 8.5, "tenure" to 1.0, "tenureType" to "YEARS", "startDate" to "")
        ).forEach { values ->
            val months = engine.calculate(CalculatorInputValues(values)).getOrThrow()
                .breakdown.single().children
            assertEquals(12, months.size)
            months.forEach { assertTrue("label was '${it.period}'", it.period.matches(Regex("[A-Z][a-z]{2} \\d{4}"))) }
        }
    }

    @Test
    fun `zero interest divides the principal evenly`() {
        assertEquals(
            "₹1,388.89",
            summary("emi", "principal" to 500000.0, "rate" to 0.0, "tenure" to 30.0, "tenureType" to "YEARS")
        )
    }

    @Test
    fun `missing inputs fail instead of returning zeroes`() {
        assertTrue(run("rate" to 8.5, "tenure" to 30.0) is Result.Failure)
        assertTrue(run("principal" to 500000.0, "tenure" to 30.0) is Result.Failure)
        assertTrue(run("principal" to 500000.0, "rate" to 8.5) is Result.Failure)
        assertTrue(run("principal" to 500000.0, "rate" to 8.5, "tenure" to 0.0) is Result.Failure)
    }
}
