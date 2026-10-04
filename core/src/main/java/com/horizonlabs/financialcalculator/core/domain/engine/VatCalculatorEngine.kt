package com.horizonlabs.financialcalculator.core.domain.engine

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

class VatCalculatorEngine : CalculatorEngine {
    override fun getSupportedCalculatorTypes() = listOf(Constants.CalculatorType.VAT)

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        
        val amount = getDouble(values, "amount") ?: return failure(IllegalArgumentException("Amount required"))
        val vatRate = getDouble(values, "vatRate") ?: return failure(IllegalArgumentException("VAT Rate required"))
        val calculationType = getString(values, "calculationType") ?: "EXCLUSIVE"
        
        val (vatAmount, totalAmount, baseAmount) = when (calculationType) {
            "EXCLUSIVE" -> {
                val vat = amount * vatRate / 100
                Triple(vat, amount + vat, amount)
            }
            "INCLUSIVE" -> {
                val base = amount / (1 + vatRate / 100)
                val vat = amount - base
                Triple(vat, amount, base)
            }
            else -> return failure(IllegalArgumentException("Invalid calculation type"))
        }
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("baseAmount", "Base Amount", Formatters.formatCurrencyINR(baseAmount), SummaryType.KEY_VALUE),
                SummaryItem("vatRate", "VAT Rate", Formatters.formatPercentage(vatRate), SummaryType.KEY_VALUE),
                SummaryItem("vatAmount", "VAT Amount", Formatters.formatCurrencyINR(vatAmount), SummaryType.KEY_VALUE),
                SummaryItem("totalAmount", "Total Amount", Formatters.formatCurrencyINR(totalAmount), SummaryType.KEY_VALUE)
            )
        )
        
        return success(CalculationResult(summary = summary, breakdown = emptyList(), errors = emptyMap()))
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