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

class FlatVsReducingEngine : CalculatorEngine {
    override fun getSupportedCalculatorTypes() = listOf(Constants.CalculatorType.FLAT_VS_REDUCING)

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        
        val principal = getDouble(values, "principal") ?: return failure(IllegalArgumentException("Principal required"))
        val rate = getDouble(values, "rate") ?: return failure(IllegalArgumentException("Rate required"))
        val tenure = getDouble(values, "tenure") ?: return failure(IllegalArgumentException("Tenure required"))
        val tenureType = getString(values, "tenureType") ?: "YEARS"
        
        val tenureMonths = if (tenureType == "YEARS") tenure * 12 else tenure
        if (tenureMonths <= 0) return failure(IllegalArgumentException("Invalid tenure"))
        
        // Flat rate calculation
        val flatInterest = principal * (rate / 100) * (tenureMonths / 12)
        val flatTotalPayable = principal + flatInterest
        val flatEmi = flatTotalPayable / tenureMonths
        
        // Reducing balance calculation
        val monthlyRate = rate / 1200
        val reducingEmi = if (monthlyRate > 0) {
            (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) / (Math.pow(1 + monthlyRate, tenureMonths) - 1)
        } else {
            principal / tenureMonths
        }
        val reducingTotalPayable = reducingEmi * tenureMonths
        val reducingInterest = reducingTotalPayable - principal
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("flatEmi", "Flat Rate EMI", Formatters.formatCurrencyINR(flatEmi), SummaryType.KEY_VALUE),
                SummaryItem("flatTotalInterest", "Flat Rate Total Interest", Formatters.formatCurrencyINR(flatInterest), SummaryType.KEY_VALUE),
                SummaryItem("flatTotalPayable", "Flat Rate Total Payable", Formatters.formatCurrencyINR(flatTotalPayable), SummaryType.KEY_VALUE),
                SummaryItem("reducingEmi", "Reducing Balance EMI", Formatters.formatCurrencyINR(reducingEmi), SummaryType.KEY_VALUE),
                SummaryItem("reducingTotalInterest", "Reducing Balance Total Interest", Formatters.formatCurrencyINR(reducingInterest), SummaryType.KEY_VALUE),
                SummaryItem("reducingTotalPayable", "Reducing Balance Total Payable", Formatters.formatCurrencyINR(reducingTotalPayable), SummaryType.KEY_VALUE),
                SummaryItem("emiDifference", "EMI Difference", Formatters.formatCurrencyINR(flatEmi - reducingEmi), SummaryType.KEY_VALUE),
                SummaryItem("interestDifference", "Interest Savings", Formatters.formatCurrencyINR(flatInterest - reducingInterest), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = listOf(
            BreakdownItem("Flat Rate", mapOf(
                "emi" to Formatters.formatCurrencyINR(flatEmi),
                "totalInterest" to Formatters.formatCurrencyINR(flatInterest),
                "totalPayable" to Formatters.formatCurrencyINR(flatTotalPayable),
                "effectiveRate" to Formatters.formatPercentage((flatInterest / principal / (tenureMonths / 12)) * 100)
            )),
            BreakdownItem("Reducing Balance", mapOf(
                "emi" to Formatters.formatCurrencyINR(reducingEmi),
                "totalInterest" to Formatters.formatCurrencyINR(reducingInterest),
                "totalPayable" to Formatters.formatCurrencyINR(reducingTotalPayable),
                "effectiveRate" to Formatters.formatPercentage(rate)
            ))
        )
        
        return success(CalculationResult(summary = summary, breakdown = breakdown, errors = emptyMap()))
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
    
    private fun getString(values: Map<String, Any>, key: String): String? {
        val value = values[key]
        return when (value) {
            is String -> value
            else -> value?.toString()
        }
    }
}