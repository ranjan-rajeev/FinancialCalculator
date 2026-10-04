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

class SipCalculatorEngine : CalculatorEngine {
    override fun getSupportedCalculatorTypes() = listOf(
        Constants.CalculatorType.SIP,
        Constants.CalculatorType.GOAL_SIP,
        Constants.CalculatorType.LUMPSUM_SIP
    )

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        val calculatorType = getString(values, "calculatorType")
            ?: getString(values, "calculatorId")
            ?: Constants.CalculatorType.SIP
        
        return when (calculatorType) {
            Constants.CalculatorType.SIP -> calculateSip(values)
            Constants.CalculatorType.GOAL_SIP -> calculateGoalSip(values)
            Constants.CalculatorType.LUMPSUM_SIP -> calculateLumpsumSip(values)
            else -> failure(IllegalArgumentException("Unknown calculator type"))
        }
    }
    
    private fun calculateSip(values: Map<String, Any>): Result<CalculationResult> {
        val monthlyInvestment = getDouble(values, "monthlyInvestment") ?: return failure(IllegalArgumentException("Monthly investment is required"))
        val rate = getDouble(values, "rate") ?: return failure(IllegalArgumentException("Rate is required"))
        val tenure = getDouble(values, "tenure") ?: return failure(IllegalArgumentException("Tenure is required"))
        val tenureType = getString(values, "tenureType") ?: "YEARS"
        val frequency = getString(values, "frequency") ?: "MONTHLY"
        
        val tenureInMonths = if (tenureType == "YEARS") tenure * 12 else tenure
        if (tenureInMonths <= 0) return failure(IllegalArgumentException("Invalid tenure"))
        
        val monthlyRate = rate / 1200
        val frequencyMultiplier = when (frequency) {
            "MONTHLY" -> 1
            "QUARTERLY" -> 3
            "HALF_YEARLY" -> 6
            "YEARLY" -> 12
            else -> 1
        }
        
        var maturity = 0.0
        var totalInvested = 0.0
        
        if (monthlyRate > 0) {
            val periods = tenureInMonths / frequencyMultiplier
            maturity = monthlyInvestment * frequencyMultiplier * 
                (Math.pow(1 + monthlyRate * frequencyMultiplier, periods) - 1) / 
                (monthlyRate * frequencyMultiplier) * 
                (1 + monthlyRate * frequencyMultiplier)
            totalInvested = monthlyInvestment * tenureInMonths
        } else {
            maturity = monthlyInvestment * tenureInMonths
            totalInvested = maturity
        }
        
        val totalReturns = maturity - totalInvested
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("totalInvested", "Total Invested", Formatters.formatCurrencyINR(totalInvested), SummaryType.KEY_VALUE),
                SummaryItem("totalReturns", "Total Returns", Formatters.formatCurrencyINR(totalReturns), SummaryType.KEY_VALUE),
                SummaryItem("maturity", "Maturity Value", Formatters.formatCurrencyINR(maturity), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = generateSipBreakdown(monthlyInvestment, rate, tenureInMonths, frequencyMultiplier)
        
        return success(CalculationResult(summary = summary, breakdown = breakdown, errors = emptyMap()))
    }
    
    private fun calculateGoalSip(values: Map<String, Any>): Result<CalculationResult> {
        val goalAmount = getDouble(values, "goalAmount") ?: return failure(IllegalArgumentException("Goal amount is required"))
        val rate = getDouble(values, "rate") ?: return failure(IllegalArgumentException("Rate is required"))
        val tenure = getDouble(values, "tenure") ?: return failure(IllegalArgumentException("Tenure is required"))
        val tenureType = getString(values, "tenureType") ?: "YEARS"
        val frequency = getString(values, "frequency") ?: "MONTHLY"
        
        val tenureInMonths = if (tenureType == "YEARS") tenure * 12 else tenure
        if (tenureInMonths <= 0) return failure(IllegalArgumentException("Invalid tenure"))
        
        val monthlyRate = rate / 1200
        val frequencyMultiplier = when (frequency) {
            "MONTHLY" -> 1
            "QUARTERLY" -> 3
            "HALF_YEARLY" -> 6
            "YEARLY" -> 12
            else -> 1
        }
        
        var requiredMonthly = 0.0
        if (monthlyRate > 0) {
            val periods = tenureInMonths / frequencyMultiplier
            requiredMonthly = goalAmount / 
                (frequencyMultiplier * (Math.pow(1 + monthlyRate * frequencyMultiplier, periods) - 1) / 
                (monthlyRate * frequencyMultiplier) * (1 + monthlyRate * frequencyMultiplier))
        } else {
            requiredMonthly = goalAmount / tenureInMonths
        }
        
        val totalInvested = requiredMonthly * tenureInMonths
        val totalReturns = goalAmount - totalInvested
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("requiredMonthly", "Required Monthly SIP", Formatters.formatCurrencyINR(requiredMonthly), SummaryType.KEY_VALUE),
                SummaryItem("totalInvested", "Total Invested", Formatters.formatCurrencyINR(totalInvested), SummaryType.KEY_VALUE),
                SummaryItem("totalReturns", "Total Returns", Formatters.formatCurrencyINR(totalReturns), SummaryType.KEY_VALUE),
                SummaryItem("goalAmount", "Goal Amount", Formatters.formatCurrencyINR(goalAmount), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = generateSipBreakdown(requiredMonthly, rate, tenureInMonths, frequencyMultiplier)
        
        return success(CalculationResult(summary = summary, breakdown = breakdown, errors = emptyMap()))
    }
    
    private fun calculateLumpsumSip(values: Map<String, Any>): Result<CalculationResult> {
        val lumpsumAmount = getDouble(values, "lumpsumAmount") ?: return failure(IllegalArgumentException("Lumpsum amount is required"))
        val rate = getDouble(values, "rate") ?: return failure(IllegalArgumentException("Rate is required"))
        val tenure = getDouble(values, "tenure") ?: return failure(IllegalArgumentException("Tenure is required"))
        val tenureType = getString(values, "tenureType") ?: "YEARS"
        
        val tenureInMonths = if (tenureType == "YEARS") tenure * 12 else tenure
        if (tenureInMonths <= 0) return failure(IllegalArgumentException("Invalid tenure"))
        
        val monthlyRate = rate / 1200
        val maturity = lumpsumAmount * Math.pow(1 + monthlyRate, tenureInMonths)
        val totalReturns = maturity - lumpsumAmount
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("lumpsumAmount", "Lumpsum Invested", Formatters.formatCurrencyINR(lumpsumAmount), SummaryType.KEY_VALUE),
                SummaryItem("totalReturns", "Total Returns", Formatters.formatCurrencyINR(totalReturns), SummaryType.KEY_VALUE),
                SummaryItem("maturity", "Maturity Value", Formatters.formatCurrencyINR(maturity), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = generateLumpsumBreakdown(lumpsumAmount, rate, tenureInMonths)
        
        return success(CalculationResult(summary = summary, breakdown = breakdown, errors = emptyMap()))
    }
    
    private fun generateSipBreakdown(
        monthlyInvestment: Double,
        rate: Double,
        tenureMonths: Double,
        frequencyMultiplier: Int
    ): List<BreakdownItem> {
        val monthlyRate = rate / 1200
        val breakdown = mutableListOf<BreakdownItem>()
        
        val totalYears = ceil(tenureMonths / 12).toInt()
        var runningBalance = 0.0
        var totalInvested = 0.0
        
        for (year in 1..totalYears) {
            var yearlyInvested = 0.0
            var yearlyReturns = 0.0
            val monthlyItems = mutableListOf<BreakdownItem>()
            
            val startMonth = (year - 1) * 12 + 1
            val endMonth = minOf(year * 12, tenureMonths.toInt())
            
            for (month in startMonth..endMonth) {
                runningBalance += monthlyInvestment
                totalInvested += monthlyInvestment
                yearlyInvested += monthlyInvestment
                
                if (month > 1) {
                    val interest = (runningBalance - monthlyInvestment) * monthlyRate
                    runningBalance += interest
                    yearlyReturns += interest
                }
                
                monthlyItems.add(BreakdownItem(
                    period = getMonthName(month),
                    values = mapOf(
                        "invested" to Formatters.formatCurrencyINR(monthlyInvestment),
                        "returns" to Formatters.formatCurrencyINR(0.0),
                        "total" to Formatters.formatCurrencyINR(runningBalance)
                    )
                ))
            }
            
            breakdown.add(BreakdownItem(
                period = "Year $year",
                values = mapOf(
                    "invested" to Formatters.formatCurrencyINR(yearlyInvested),
                    "returns" to Formatters.formatCurrencyINR(yearlyReturns),
                    "total" to Formatters.formatCurrencyINR(runningBalance)
                ),
                children = monthlyItems
            ))
        }
        
        return breakdown
    }
    
    private fun generateLumpsumBreakdown(lumpsum: Double, rate: Double, tenureMonths: Double): List<BreakdownItem> {
        val monthlyRate = rate / 1200
        val breakdown = mutableListOf<BreakdownItem>()
        
        val totalYears = ceil(tenureMonths / 12).toInt()
        var runningBalance = lumpsum
        
        for (year in 1..totalYears) {
            var yearlyReturns = 0.0
            val monthlyItems = mutableListOf<BreakdownItem>()
            
            val startMonth = (year - 1) * 12 + 1
            val endMonth = minOf(year * 12, tenureMonths.toInt())
            
            for (month in startMonth..endMonth) {
                val interest = runningBalance * monthlyRate
                runningBalance += interest
                yearlyReturns += interest
                
                monthlyItems.add(BreakdownItem(
                    period = getMonthName(month),
                    values = mapOf(
                        "invested" to Formatters.formatCurrencyINR(0.0),
                        "returns" to Formatters.formatCurrencyINR(interest),
                        "total" to Formatters.formatCurrencyINR(runningBalance)
                    )
                ))
            }
            
            breakdown.add(BreakdownItem(
                period = "Year $year",
                values = mapOf(
                    "invested" to Formatters.formatCurrencyINR(0.0),
                    "returns" to Formatters.formatCurrencyINR(yearlyReturns),
                    "total" to Formatters.formatCurrencyINR(runningBalance)
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