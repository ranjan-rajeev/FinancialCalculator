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
import kotlin.math.ceil

class RdCalculatorEngine : CalculatorEngine {
    override fun getSupportedCalculatorTypes() = listOf(Constants.CalculatorType.RD)

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        
        val monthlyDeposit = getDouble(values, "monthlyDeposit") ?: return failure(IllegalArgumentException("Monthly deposit required"))
        val rate = getDouble(values, "rate") ?: return failure(IllegalArgumentException("Rate required"))
        val tenure = getDouble(values, "tenure") ?: return failure(IllegalArgumentException("Tenure required"))
        val tenureType = getString(values, "tenureType") ?: "YEARS"
        
        val tenureMonths = if (tenureType == "YEARS") tenure * 12 else tenure
        if (tenureMonths <= 0) return failure(IllegalArgumentException("Invalid tenure"))
        
        val monthlyRate = rate / 1200
        val maturity = monthlyDeposit * ((Math.pow(1 + monthlyRate, tenureMonths) - 1) / monthlyRate) * (1 + monthlyRate)
        val totalInvested = monthlyDeposit * tenureMonths
        val totalInterest = maturity - totalInvested
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("totalInvested", "Total Invested", Formatters.formatCurrencyINR(totalInvested), SummaryType.KEY_VALUE),
                SummaryItem("totalInterest", "Total Interest", Formatters.formatCurrencyINR(totalInterest), SummaryType.KEY_VALUE),
                SummaryItem("maturity", "Maturity Value", Formatters.formatCurrencyINR(maturity), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = generateRdBreakdown(monthlyDeposit, rate, tenureMonths)
        
        return success(CalculationResult(summary = summary, breakdown = breakdown, errors = emptyMap()))
    }
    
    private fun generateRdBreakdown(monthlyDeposit: Double, rate: Double, tenureMonths: Double): List<BreakdownItem> {
        val monthlyRate = rate / 1200
        val breakdown = mutableListOf<BreakdownItem>()
        var runningBalance = 0.0
        var totalInvested = 0.0
        
        val totalYears = ceil(tenureMonths / 12).toInt()
        
        for (year in 1..totalYears) {
            var yearlyInvested = 0.0
            var yearlyInterest = 0.0
            val monthlyItems = mutableListOf<BreakdownItem>()
            
            val startMonth = (year - 1) * 12 + 1
            val endMonth = minOf(year * 12, tenureMonths.toInt())
            
            for (month in startMonth..endMonth) {
                runningBalance += monthlyDeposit
                totalInvested += monthlyDeposit
                yearlyInvested += monthlyDeposit
                
                val interest = (runningBalance - monthlyDeposit) * monthlyRate
                runningBalance += interest
                yearlyInterest += interest
                
                monthlyItems.add(BreakdownItem(
                    period = getMonthName(month),
                    values = mapOf(
                        "deposit" to Formatters.formatCurrencyINR(monthlyDeposit),
                        "interest" to Formatters.formatCurrencyINR(interest),
                        "balance" to Formatters.formatCurrencyINR(runningBalance)
                    )
                ))
            }
            
            breakdown.add(BreakdownItem(
                period = "Year $year",
                values = mapOf(
                    "deposit" to Formatters.formatCurrencyINR(yearlyInvested),
                    "interest" to Formatters.formatCurrencyINR(yearlyInterest),
                    "balance" to Formatters.formatCurrencyINR(runningBalance)
                ),
                children = monthlyItems
            ))
        }
        
        return breakdown
    }
    
    private fun getMonthName(month: Int): String {
        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return months[(month - 1) % 12]
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