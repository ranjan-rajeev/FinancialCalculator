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
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.mXparser
import kotlin.math.ceil

class GenericFormulaEngine : CalculatorEngine {
    init {
        mXparser.setRadiansMode()
    }
    
    override fun getSupportedCalculatorTypes() = listOf(
        Constants.CalculatorType.NPS,
        Constants.CalculatorType.ATAL,
        Constants.CalculatorType.CAGR
    )
    
    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        val calculatorType = getString(values, "calculatorType")
            ?: getString(values, "calculatorId")
            ?: Constants.CalculatorType.NPS
        val formulaMap = getFormulaMap(calculatorType)
        
        val variables = mutableMapOf<String, Double>()
        values.forEach { (key, value) ->
            val doubleVal = when (value) {
                is Double -> value
                is Float -> value.toDouble()
                is Int -> value.toDouble()
                is Long -> value.toDouble()
                is String -> value.toDoubleOrNull()
                else -> null
            }
            doubleVal?.let { variables[key] = it }
        }
        
        // Compute derived variables
        computeDerivedVariables(calculatorType, variables)
        
        val results = evaluateInDependencyOrder(formulaMap, variables)
        
        val summary = buildSummary(calculatorType, results)
        val breakdown = buildBreakdown(calculatorType, variables, results)
        
        return success(CalculationResult(summary = summary, breakdown = breakdown, errors = emptyMap()))
    }
    
    /**
     * Formulas reference each other (`months` -> `totalCorpus` -> `pensionAmount`), so each
     * resolved value is published back into the variable scope before the next formula runs.
     * Passes repeat until nothing new resolves, which makes the order of `formulaMap`
     * irrelevant instead of silently dropping every dependent formula.
     */
    private fun evaluateInDependencyOrder(
        formulaMap: Map<String, String>,
        variables: MutableMap<String, Double>
    ): Map<String, Double> {
        val results = mutableMapOf<String, Double>()
        val pending = formulaMap.toMutableMap()

        var madeProgress = true
        while (pending.isNotEmpty() && madeProgress) {
            madeProgress = false
            val iterator = pending.entries.iterator()
            while (iterator.hasNext()) {
                val (key, formula) = iterator.next()
                val value = evaluateFormula(formula, variables + results).getOrNull()
                if (value != null && !value.isNaN() && !value.isInfinite()) {
                    results[key] = value
                    iterator.remove()
                    madeProgress = true
                }
            }
        }
        return results
    }

    fun evaluateFormula(formula: String, variables: Map<String, Double>): Result<Double> {
        return try {
            val expr = Expression(formula)
            variables.forEach { (key, value) ->
                expr.addArguments(org.mariuszgromada.math.mxparser.Argument(key, value))
            }
            val result = expr.calculate()
            // mXparser's getErrorMessage() also returns its "syntax check" trace on a
            // successful parse, so a non-null message does not mean failure. NaN is the
            // real signal that an identifier was missing or the expression was invalid.
            if (result.isNaN()) {
                failure(IllegalArgumentException("Formula '$formula' could not be evaluated"))
            } else {
                success(result)
            }
        } catch (e: Exception) {
            failure(IllegalArgumentException("Formula evaluation failed: ${e.message}"))
        }
    }
    
    private fun getFormulaMap(calculatorType: String): Map<String, String> {
        return when (calculatorType) {
            Constants.CalculatorType.NPS -> mapOf(
                "months" to "(retirementAge - age) * 12",
                "totalCorpus" to "monthlyContribution * ((1 + expectedReturn/1200)^months - 1) / (expectedReturn/1200)",
                "maturityValue" to "totalCorpus",
                "lumpSum" to "totalCorpus * (100 - annuityPercentage) / 100",
                "annuityAmount" to "totalCorpus * annuityPercentage / 100",
                "pensionAmount" to "annuityAmount * annuityRate / 1200"
            )
            Constants.CalculatorType.ATAL -> mapOf(
                "months" to "(retirementAge - age) * 12",
                "totalContribution" to "monthlyContribution * months",
                "totalCorpus" to "monthlyContribution * ((1 + expectedReturn/1200)^months - 1) / (expectedReturn/1200)",
                "maturityValue" to "totalCorpus",
                "monthlyPension" to "totalCorpus * pensionRate / 1200"
            )
            Constants.CalculatorType.CAGR -> mapOf(
                "years" to "tenure",
                "cagr" to "(endingValue / beginningValue)^(1/years) - 1",
                "cagrPercent" to "cagr * 100"
            )
            else -> emptyMap()
        }
    }
    
    private fun computeDerivedVariables(calculatorType: String, variables: MutableMap<String, Double>) {
        when (calculatorType) {
            Constants.CalculatorType.NPS -> {
                val age = variables["age"] ?: 0.0
                val retirementAge = variables["retirementAge"] ?: 0.0
                val monthlyContribution = variables["monthlyContribution"] ?: 0.0
                val expectedReturn = variables["expectedReturn"] ?: 0.0
                val annuityPercentage = variables["annuityPercentage"] ?: 40.0
                val annuityRate = variables["annuityRate"] ?: 6.0
                
                val months = (retirementAge - age) * 12
                variables["months"] = months
                
                if (expectedReturn > 0 && months > 0) {
                    val totalCorpus = monthlyContribution * (Math.pow(1 + expectedReturn / 1200, months) - 1) / (expectedReturn / 1200)
                    variables["totalCorpus"] = totalCorpus
                    variables["maturityValue"] = totalCorpus
                    variables["lumpSum"] = totalCorpus * (100 - annuityPercentage) / 100
                    variables["annuityAmount"] = totalCorpus * annuityPercentage / 100
                    variables["pensionAmount"] = variables["annuityAmount"]!! * annuityRate / 1200
                }
            }
            Constants.CalculatorType.ATAL -> {
                val age = variables["age"] ?: 0.0
                val retirementAge = variables["retirementAge"] ?: 0.0
                val monthlyContribution = variables["monthlyContribution"] ?: 0.0
                val expectedReturn = variables["expectedReturn"] ?: 0.0
                val pensionRate = variables["pensionRate"] ?: 6.0
                
                val months = (retirementAge - age) * 12
                variables["months"] = months
                variables["totalContribution"] = monthlyContribution * months
                
                if (expectedReturn > 0 && months > 0) {
                    val totalCorpus = monthlyContribution * (Math.pow(1 + expectedReturn / 1200, months) - 1) / (expectedReturn / 1200)
                    variables["totalCorpus"] = totalCorpus
                    variables["maturityValue"] = totalCorpus
                    variables["monthlyPension"] = totalCorpus * pensionRate / 1200
                }
            }
            Constants.CalculatorType.CAGR -> {
                val beginningValue = variables["beginningValue"] ?: 0.0
                val endingValue = variables["endingValue"] ?: 0.0
                val tenure = variables["tenure"] ?: 1.0
                
                if (beginningValue > 0 && tenure > 0) {
                    val cagr = Math.pow(endingValue / beginningValue, 1.0 / tenure) - 1
                    variables["cagr"] = cagr
                    variables["cagrPercent"] = cagr * 100
                }
            }
        }
    }
    
    private fun buildSummary(calculatorType: String, results: Map<String, Double>): CalculatorSummary {
        val items = mutableListOf<SummaryItem>()
        
        when (calculatorType) {
            Constants.CalculatorType.NPS -> {
                results["totalCorpus"]?.let { items.add(SummaryItem("totalCorpus", "Total Corpus", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
                results["maturityValue"]?.let { items.add(SummaryItem("maturityValue", "Maturity Value", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
                results["pensionAmount"]?.let { items.add(SummaryItem("pensionAmount", "Monthly Pension", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
                results["lumpSum"]?.let { items.add(SummaryItem("lumpSum", "Lump Sum Withdrawal", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
            }
            Constants.CalculatorType.ATAL -> {
                results["totalContribution"]?.let { items.add(SummaryItem("totalContribution", "Total Contribution", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
                results["totalCorpus"]?.let { items.add(SummaryItem("totalCorpus", "Total Corpus", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
                results["maturityValue"]?.let { items.add(SummaryItem("maturityValue", "Maturity Value", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
                results["monthlyPension"]?.let { items.add(SummaryItem("monthlyPension", "Monthly Pension", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
            }
            Constants.CalculatorType.CAGR -> {
                results["cagrPercent"]?.let { items.add(SummaryItem("cagrPercent", "CAGR", Formatters.formatPercentage(it), SummaryType.KEY_VALUE)) }
                results["endingValue"]?.let { items.add(SummaryItem("endingValue", "Ending Value", Formatters.formatCurrencyINR(it), SummaryType.KEY_VALUE)) }
            }
        }
        
        return CalculatorSummary(items)
    }
    
    private fun buildBreakdown(calculatorType: String, variables: Map<String, Double>, results: Map<String, Double>): List<BreakdownItem> {
        return when (calculatorType) {
            Constants.CalculatorType.NPS, Constants.CalculatorType.ATAL -> {
                val months = variables["months"] ?: 0.0
                val monthlyContribution = variables["monthlyContribution"] ?: 0.0
                val expectedReturn = variables["expectedReturn"] ?: 0.0
                
                if (months <= 0) emptyList() else {
                    val totalYears = ceil(months / 12).toInt()
                    val breakdown = mutableListOf<BreakdownItem>()
                    var runningBalance = 0.0
                    var totalContribution = 0.0
                    val monthlyRate = expectedReturn / 1200
                    
                    for (year in 1..totalYears) {
                        var yearlyContribution = 0.0
                        var yearlyReturns = 0.0
                        
                        val startMonth = (year - 1) * 12 + 1
                        val endMonth = minOf(year * 12, months.toInt())
                        
                        for (month in startMonth..endMonth) {
                            runningBalance += monthlyContribution
                            totalContribution += monthlyContribution
                            yearlyContribution += monthlyContribution
                            
                            if (month > 1 && expectedReturn > 0) {
                                val interest = (runningBalance - monthlyContribution) * monthlyRate
                                runningBalance += interest
                                yearlyReturns += interest
                            }
                        }
                        
                        breakdown.add(BreakdownItem(
                            period = "Year $year",
                            values = mapOf(
                                "contribution" to Formatters.formatCurrencyINR(yearlyContribution),
                                "returns" to Formatters.formatCurrencyINR(yearlyReturns),
                                "total" to Formatters.formatCurrencyINR(runningBalance)
                            )
                        ))
                    }
                    breakdown
                }
            }
            Constants.CalculatorType.CAGR -> emptyList()
            else -> emptyList()
        }
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