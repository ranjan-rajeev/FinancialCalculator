package com.horizonlabs.financialcalculator.core.domain.engine

import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.util.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

/**
 * `GENERIC_FORMULA` calculators publish formulas that reference each other
 * (`months` -> `totalCorpus` -> `annuityAmount` -> `pensionAmount`).
 *
 * Regression: results were written to a separate map while each formula was
 * evaluated against the input variables only, so every dependent formula failed
 * on an unknown identifier and NPS/ATAL/CAGR rendered no summary at all.
 */
class GenericFormulaEngineTest {

    private fun run(calculatorId: String, values: Map<String, Any>): Map<String, Double> {
        val result = GenericFormulaEngine().calculate(
            CalculatorInputValues(values + ("calculatorId" to calculatorId))
        ).getOrThrow()
        return result.summary!!.items.associate { item ->
            item.key to item.value
                .replace("₹", "")
                .replace(",", "")
                .replace("%", "")
                .trim()
                .toDouble()
        }
    }

    @Test
    fun `nps resolves the full dependency chain`() {
        val summary = run(
            Constants.CalculatorType.NPS,
            mapOf(
                "age" to "30",
                "retirementAge" to "60",
                "monthlyContribution" to "5000",
                "expectedReturn" to "10",
                "annuityPercentage" to "40",
                "annuityRate" to "6"
            )
        )

        assertEquals(
            setOf("totalCorpus", "maturityValue", "pensionAmount", "lumpSum"),
            summary.keys
        )
    }

    @Test
    fun `nps corpus matches the future value of a monthly contribution`() {
        val summary = run(
            Constants.CalculatorType.NPS,
            mapOf(
                "age" to "30",
                "retirementAge" to "60",
                "monthlyContribution" to "5000",
                "expectedReturn" to "10",
                "annuityPercentage" to "40",
                "annuityRate" to "6"
            )
        )

        val months = (60 - 30) * 12
        val monthlyRate = 10.0 / 1200
        val expected = 5000 * ((1 + monthlyRate).pow(months) - 1) / monthlyRate

        assertEquals(expected, summary.getValue("totalCorpus") ?: 0.0, expected * 0.001)
    }

    @Test
    fun `nps splits corpus between lump sum and annuity`() {
        val summary = run(
            Constants.CalculatorType.NPS,
            mapOf(
                "age" to "30",
                "retirementAge" to "60",
                "monthlyContribution" to "5000",
                "expectedReturn" to "10",
                "annuityPercentage" to "40",
                "annuityRate" to "6"
            )
        )

        val corpus = summary.getValue("totalCorpus") ?: 0.0
        assertEquals(corpus * 0.6, summary.getValue("lumpSum") ?: 0.0, corpus * 0.001)
        // Pension is the annuity share earning `annuityRate` per year, drawn monthly.
        assertEquals(corpus * 0.4 * 6 / 1200, summary.getValue("pensionAmount") ?: 0.0, corpus * 0.001)
    }

    @Test
    fun `atal resolves its own chain`() {
        val summary = run(
            Constants.CalculatorType.ATAL,
            mapOf(
                "age" to "30",
                "retirementAge" to "60",
                "monthlyContribution" to "5000",
                "expectedReturn" to "10",
                "pensionRate" to "6"
            )
        )

        assertTrue("ATAL summary was empty", summary.isNotEmpty())
        assertEquals(5000.0 * 360, summary.getValue("totalContribution") ?: 0.0, 1.0)
    }

    @Test
    fun `cagr is reported as a percentage`() {
        val summary = run(
            Constants.CalculatorType.CAGR,
            mapOf(
                "beginningValue" to "100000",
                "endingValue" to "200000",
                "tenure" to "5"
            )
        )

        val expected = ((200000.0 / 100000.0).pow(1.0 / 5.0) - 1) * 100
        assertEquals(expected, summary.getValue("cagrPercent") ?: 0.0, 0.01)
    }
}
