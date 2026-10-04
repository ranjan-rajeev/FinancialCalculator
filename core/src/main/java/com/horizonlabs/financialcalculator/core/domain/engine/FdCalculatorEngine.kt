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

class FdCalculatorEngine : CalculatorEngine {
    override fun getSupportedCalculatorTypes() = listOf(Constants.CalculatorType.FD)

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        
        val principal = getDouble(values, "principal") ?: return failure(IllegalArgumentException("Principal is required"))
        val rate = getDouble(values, "rate") ?: return failure(IllegalArgumentException("Rate is required"))
        val year = getDouble(values, "year") ?: 0.0
        val month = getDouble(values, "month") ?: 0.0
        val day = getDouble(values, "day") ?: 0.0
        val compoundingType = getString(values, "compoundingType") ?: "QUARTERLY"
        
        val totalMonths = ceil(year * 12 + month + day / 30).toInt()
        if (totalMonths <= 0) return failure(IllegalArgumentException("Invalid tenure"))
        
        val n = when (compoundingType) {
            "MONTHLY" -> 12
            "QUARTERLY" -> 4
            "HALF_YEARLY" -> 2
            "YEARLY" -> 1
            else -> 4
        }
        
        val r = rate / 100
        val t = year + (month / 12) + (day / 365)
        
        val maturity = principal * Math.pow(1 + r / n, n * t)
        val totalInterest = maturity - principal
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("principal", "Principal Amount", Formatters.formatCurrencyINR(principal), SummaryType.KEY_VALUE),
                SummaryItem("totalInterest", "Total Interest", Formatters.formatCurrencyINR(totalInterest), SummaryType.KEY_VALUE),
                SummaryItem("maturity", "Maturity Amount", Formatters.formatCurrencyINR(maturity), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = generateBreakdown(principal, rate, year, month, day, n, compoundingType)
        
        return success(CalculationResult(
            summary = summary,
            breakdown = breakdown,
            errors = emptyMap(),
            rawValues = mapOf(
                "principal" to principal,
                "totalInterest" to totalInterest,
                "maturity" to maturity
            )
        ))
    }
    
    private fun generateBreakdown(
        principal: Double,
        rate: Double,
        year: Double,
        month: Double,
        day: Double,
        n: Int,
        compoundingType: String
    ): List<BreakdownItem> {
        val r = rate / 100
        val totalMonths = ceil(year * 12 + month + day / 30).toInt()
        val breakdown = mutableListOf<BreakdownItem>()
        
        var remainingPrincipal = principal
        var totalInterest = 0.0
        
        val totalYears = ceil(totalMonths / 12.0).toInt()
        
        for (yr in 1..totalYears) {
            var yearlyPrincipal = 0.0
            var yearlyInterest = 0.0
            var yearlyTotal = 0.0
            val periodItems = mutableListOf<BreakdownItem>()
            
            val startMonth = (yr - 1) * 12 + 1
            val endMonth = minOf(yr * 12, totalMonths)
            
            for (m in startMonth..endMonth) {
                val interest = remainingPrincipal * r / n
                remainingPrincipal += interest
                yearlyInterest += interest
                totalInterest += interest
                
                periodItems.add(BreakdownItem(
                    period = getMonthName(m),
                    values = mapOf(
                        "principal" to Formatters.formatCurrencyINR(principal),
                        "interest" to Formatters.formatCurrencyINR(interest),
                        "total" to Formatters.formatCurrencyINR(interest),
                        "balance" to Formatters.formatCurrencyINR(remainingPrincipal)
                    )
                ))
            }
            
            breakdown.add(BreakdownItem(
                period = "Year $yr",
                values = mapOf(
                    "principal" to Formatters.formatCurrencyINR(principal),
                    "interest" to Formatters.formatCurrencyINR(yearlyInterest),
                    "total" to Formatters.formatCurrencyINR(yearlyInterest),
                    "balance" to Formatters.formatCurrencyINR(remainingPrincipal)
                ),
                children = periodItems
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