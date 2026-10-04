package com.horizonlabs.financialcalculator.core.domain.engine

import com.horizonlabs.financialcalculator.core.domain.model.BreakdownItem
import com.horizonlabs.financialcalculator.core.domain.model.CalculationResult
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorSummary
import com.horizonlabs.financialcalculator.core.domain.model.SummaryItem
import com.horizonlabs.financialcalculator.core.domain.model.SummaryType
import com.horizonlabs.financialcalculator.core.util.Constants
import com.horizonlabs.financialcalculator.core.util.Formatters
import com.horizonlabs.financialcalculator.core.util.Result
import com.horizonlabs.financialcalculator.core.util.Result.Companion.failure
import com.horizonlabs.financialcalculator.core.util.Result.Companion.success

class PpfCalculatorEngine : CalculatorEngine {
    override fun getSupportedCalculatorTypes() = listOf(Constants.CalculatorType.PPF)

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        
        val yearlyDeposit = getDouble(values, "yearlyDeposit") ?: return failure(IllegalArgumentException("Yearly deposit required"))
        val rate = getDouble(values, "rate") ?: return failure(IllegalArgumentException("Rate required"))
        val tenure = getDouble(values, "tenure") ?: 15.0 // PPF default 15 years
        
        if (yearlyDeposit <= 0) return failure(IllegalArgumentException("Invalid deposit"))
        if (tenure < 15) return failure(IllegalArgumentException("PPF minimum tenure is 15 years"))
        
        val yearlyRate = rate / 100
        var balance = 0.0
        var totalInvested = 0.0
        var totalInterest = 0.0
        
        for (year in 1..tenure.toInt()) {
            balance += yearlyDeposit
            totalInvested += yearlyDeposit
            val interest = balance * yearlyRate
            balance += interest
            totalInterest += interest
        }
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("totalInvested", "Total Invested", Formatters.formatCurrencyINR(totalInvested), SummaryType.KEY_VALUE),
                SummaryItem("totalInterest", "Total Interest", Formatters.formatCurrencyINR(totalInterest), SummaryType.KEY_VALUE),
                SummaryItem("maturity", "Maturity Value", Formatters.formatCurrencyINR(balance), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = generatePpfBreakdown(yearlyDeposit, rate, tenure.toInt())
        
        return success(CalculationResult(summary = summary, breakdown = breakdown, errors = emptyMap()))
    }
    
    private fun generatePpfBreakdown(yearlyDeposit: Double, rate: Double, tenure: Int): List<BreakdownItem> {
        val yearlyRate = rate / 100
        val breakdown = mutableListOf<BreakdownItem>()
        var balance = 0.0
        var totalInvested = 0.0
        var totalInterest = 0.0
        
        for (year in 1..tenure) {
            balance += yearlyDeposit
            totalInvested += yearlyDeposit
            val interest = balance * yearlyRate
            balance += interest
            totalInterest += interest
            
            breakdown.add(BreakdownItem(
                period = "Year $year",
                values = mapOf(
                    "deposit" to Formatters.formatCurrencyINR(yearlyDeposit),
                    "interest" to Formatters.formatCurrencyINR(interest),
                    "totalInvested" to Formatters.formatCurrencyINR(totalInvested),
                    "totalInterest" to Formatters.formatCurrencyINR(totalInterest),
                    "balance" to Formatters.formatCurrencyINR(balance)
                )
            ))
        }
        
        return breakdown
    }
    
    private fun getDouble(values: Map<String, Any>, key: String): Double? {
        val value = values[key]
        return when (value) {
            is Double -> value
            is Float -> value.toDouble()
            is Int -> value.toDouble()
            is Long -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }
}