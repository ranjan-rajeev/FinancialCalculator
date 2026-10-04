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

class GstCalculatorEngine : CalculatorEngine {

    private companion object {
        /** Value published for the inter-state option in the hosted `isInterstate` spinner. */
        const val INTERSTATE = "INTER"
    }
    override fun getSupportedCalculatorTypes() = listOf(Constants.CalculatorType.GST)

    override fun calculate(input: CalculatorInputValues): Result<CalculationResult> {
        val values = input.values
        
        val amount = getDouble(values, "amount") ?: return failure(IllegalArgumentException("Amount required"))
        val gstRate = getDouble(values, "gstRate") ?: return failure(IllegalArgumentException("GST Rate required"))
        val calculationType = getString(values, "calculationType") ?: "EXCLUSIVE"
        
        val (gstAmount, totalAmount, baseAmount) = when (calculationType) {
            "EXCLUSIVE" -> {
                val gst = amount * gstRate / 100
                Triple(gst, amount + gst, amount)
            }
            "INCLUSIVE" -> {
                val base = amount / (1 + gstRate / 100)
                val gst = amount - base
                Triple(gst, amount, base)
            }
            else -> return failure(IllegalArgumentException("Invalid calculation type"))
        }
        
        // Inter-state supply is levied as a single IGST line; intra-state splits into CGST + SGST.
        val isInterstate = getString(values, "isInterstate") == INTERSTATE
        val cgst = if (isInterstate) 0.0 else gstAmount / 2
        val sgst = if (isInterstate) 0.0 else gstAmount / 2
        val igst = if (isInterstate) gstAmount else 0.0
        
        val summary = CalculatorSummary(
            items = listOf(
                SummaryItem("baseAmount", "Base Amount", Formatters.formatCurrencyINR(baseAmount), SummaryType.KEY_VALUE),
                SummaryItem("gstRate", "GST Rate", Formatters.formatPercentage(gstRate), SummaryType.KEY_VALUE),
                SummaryItem("gstAmount", "GST Amount", Formatters.formatCurrencyINR(gstAmount), SummaryType.KEY_VALUE),
                SummaryItem("totalAmount", "Total Amount", Formatters.formatCurrencyINR(totalAmount), SummaryType.KEY_VALUE),
                SummaryItem("cgst", "CGST", Formatters.formatCurrencyINR(cgst), SummaryType.KEY_VALUE),
                SummaryItem("sgst", "SGST", Formatters.formatCurrencyINR(sgst), SummaryType.KEY_VALUE),
                SummaryItem("igst", "IGST", Formatters.formatCurrencyINR(igst), SummaryType.KEY_VALUE)
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