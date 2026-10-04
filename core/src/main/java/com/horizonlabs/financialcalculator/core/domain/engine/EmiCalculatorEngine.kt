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

class EmiCalculatorEngine : CalculatorEngine {
    override fun getSupportedCalculatorTypes() = listOf(Constants.CalculatorType.EMI)

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        
        val principal = getDouble(values, "principal") ?: return failure(IllegalArgumentException("Principal is required"))
        val rate = getDouble(values, "rate") ?: return failure(IllegalArgumentException("Rate is required"))
        val tenure = getDouble(values, "tenure") ?: return failure(IllegalArgumentException("Tenure is required"))
        val tenureType = getString(values, "tenureType") ?: "YEARS"
        
        val tenureInMonths = if (tenureType == "YEARS") tenure * 12 else tenure
        if (tenureInMonths <= 0) return failure(IllegalArgumentException("Invalid tenure"))
        
        val monthlyRate = rate / 1200 // rate is annual percentage
        val emi = if (monthlyRate > 0) {
            (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureInMonths)) / (Math.pow(1 + monthlyRate, tenureInMonths) - 1)
        } else {
            principal / tenureInMonths
        }
        
        val totalPayable = emi * tenureInMonths
        val totalInterest = totalPayable - principal
        val interestPercentage = if (totalPayable > 0) (totalInterest / totalPayable) * 100 else 0.0
        val principalPercentage = 100 - interestPercentage
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("emi", "Monthly EMI", Formatters.formatCurrencyINR(emi), SummaryType.KEY_VALUE),
                SummaryItem("totalInterest", "Total Interest", Formatters.formatCurrencyINR(totalInterest), SummaryType.KEY_VALUE),
                SummaryItem("totalPayable", "Total Payable", Formatters.formatCurrencyINR(totalPayable), SummaryType.KEY_VALUE),
                SummaryItem("interestPercentage", "Interest %", Formatters.formatPercentage(interestPercentage), SummaryType.KEY_VALUE),
                SummaryItem("principalPercentage", "Principal %", Formatters.formatPercentage(principalPercentage), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = generateYearlyBreakdown(principal, rate, tenureInMonths, emi)
        
        return success(CalculationResult(
            summary = summary,
            breakdown = breakdown,
            errors = emptyMap()
        ))
    }
    
    private fun generateYearlyBreakdown(principal: Double, rate: Double, tenureMonths: Double, emi: Double): List<BreakdownItem> {
        val monthlyRate = rate / 1200
        var remainingPrincipal = principal
        val breakdown = mutableListOf<BreakdownItem>()
        
        val totalYears = ceil(tenureMonths / 12).toInt()
        
        for (year in 1..totalYears) {
            var yearlyPrincipal = 0.0
            var yearlyInterest = 0.0
            var yearlyTotal = 0.0
            val monthlyItems = mutableListOf<BreakdownItem>()
            
            val startMonth = (year - 1) * 12 + 1
            val endMonth = minOf(year * 12, tenureMonths.toInt())
            
            for (month in startMonth..endMonth) {
                val interest = remainingPrincipal * monthlyRate
                val principalPaid = emi - interest
                remainingPrincipal -= principalPaid
                
                yearlyPrincipal += principalPaid
                yearlyInterest += interest
                yearlyTotal += emi
                
                monthlyItems.add(BreakdownItem(
                    period = getMonthName(month),
                    values = mapOf(
                        "principal" to Formatters.formatCurrencyINR(principalPaid),
                        "interest" to Formatters.formatCurrencyINR(interest),
                        "total" to Formatters.formatCurrencyINR(emi),
                        "balance" to Formatters.formatCurrencyINR(maxOf(remainingPrincipal, 0.0))
                    )
                ))
            }
            
            breakdown.add(BreakdownItem(
                period = "Year $year",
                values = mapOf(
                    "principal" to Formatters.formatCurrencyINR(yearlyPrincipal),
                    "interest" to Formatters.formatCurrencyINR(yearlyInterest),
                    "total" to Formatters.formatCurrencyINR(yearlyTotal),
                    "balance" to Formatters.formatCurrencyINR(maxOf(remainingPrincipal, 0.0))
                ),
                children = monthlyItems
            ))
            
            if (remainingPrincipal <= 0) break
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