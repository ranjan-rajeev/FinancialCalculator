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

class CompareLoanEngine : CalculatorEngine {
    override fun getSupportedCalculatorTypes() = listOf(Constants.CalculatorType.COMPARE_LOAN)

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        
        val loan1Principal = getDouble(values, "loan1_principal") ?: return failure(IllegalArgumentException("Loan 1 principal required"))
        val loan1Rate = getDouble(values, "loan1_rate") ?: return failure(IllegalArgumentException("Loan 1 rate required"))
        val loan1Tenure = getDouble(values, "loan1_tenure") ?: return failure(IllegalArgumentException("Loan 1 tenure required"))
        val loan1TenureType = getString(values, "loan1_tenureType") ?: "YEARS"
        
        val loan2Principal = getDouble(values, "loan2_principal") ?: return failure(IllegalArgumentException("Loan 2 principal required"))
        val loan2Rate = getDouble(values, "loan2_rate") ?: return failure(IllegalArgumentException("Loan 2 rate required"))
        val loan2Tenure = getDouble(values, "loan2_tenure") ?: return failure(IllegalArgumentException("Loan 2 tenure required"))
        val loan2TenureType = getString(values, "loan2_tenureType") ?: "YEARS"
        
        val loan1Months = if (loan1TenureType == "YEARS") loan1Tenure * 12 else loan1Tenure
        val loan2Months = if (loan2TenureType == "YEARS") loan2Tenure * 12 else loan2Tenure
        
        val loan1 = calculateEmi(loan1Principal, loan1Rate, loan1Months)
        val loan2 = calculateEmi(loan2Principal, loan2Rate, loan2Months)
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("loan1_emi", "Loan 1 EMI", Formatters.formatCurrencyINR(loan1.emi), SummaryType.KEY_VALUE),
                SummaryItem("loan1_totalInterest", "Loan 1 Total Interest", Formatters.formatCurrencyINR(loan1.totalInterest), SummaryType.KEY_VALUE),
                SummaryItem("loan1_totalPayable", "Loan 1 Total Payable", Formatters.formatCurrencyINR(loan1.totalPayable), SummaryType.KEY_VALUE),
                SummaryItem("loan2_emi", "Loan 2 EMI", Formatters.formatCurrencyINR(loan2.emi), SummaryType.KEY_VALUE),
                SummaryItem("loan2_totalInterest", "Loan 2 Total Interest", Formatters.formatCurrencyINR(loan2.totalInterest), SummaryType.KEY_VALUE),
                SummaryItem("loan2_totalPayable", "Loan 2 Total Payable", Formatters.formatCurrencyINR(loan2.totalPayable), SummaryType.KEY_VALUE),
                SummaryItem("savings", "Interest Savings (Loan 2 vs Loan 1)", Formatters.formatCurrencyINR(loan1.totalInterest - loan2.totalInterest), SummaryType.KEY_VALUE)
            )
        )
        
        val breakdown = listOf(
            BreakdownItem("Loan 1", mapOf(
                "principal" to Formatters.formatCurrencyINR(loan1Principal),
                "rate" to Formatters.formatPercentage(loan1Rate),
                "tenure" to "${loan1Tenure} ${loan1TenureType}",
                "emi" to Formatters.formatCurrencyINR(loan1.emi),
                "totalInterest" to Formatters.formatCurrencyINR(loan1.totalInterest),
                "totalPayable" to Formatters.formatCurrencyINR(loan1.totalPayable)
            )),
            BreakdownItem("Loan 2", mapOf(
                "principal" to Formatters.formatCurrencyINR(loan2Principal),
                "rate" to Formatters.formatPercentage(loan2Rate),
                "tenure" to "${loan2Tenure} ${loan2TenureType}",
                "emi" to Formatters.formatCurrencyINR(loan2.emi),
                "totalInterest" to Formatters.formatCurrencyINR(loan2.totalInterest),
                "totalPayable" to Formatters.formatCurrencyINR(loan2.totalPayable)
            ))
        )
        
        return success(CalculationResult(summary = summary, breakdown = breakdown, errors = emptyMap()))
    }
    
    private fun calculateEmi(principal: Double, rate: Double, tenureMonths: Double): EmiResult {
        val monthlyRate = rate / 1200
        val emi = if (monthlyRate > 0) {
            (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) / (Math.pow(1 + monthlyRate, tenureMonths) - 1)
        } else {
            principal / tenureMonths
        }
        val totalPayable = emi * tenureMonths
        val totalInterest = totalPayable - principal
        return EmiResult(emi, totalInterest, totalPayable)
    }
    
    private data class EmiResult(val emi: Double, val totalInterest: Double, val totalPayable: Double)
    
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