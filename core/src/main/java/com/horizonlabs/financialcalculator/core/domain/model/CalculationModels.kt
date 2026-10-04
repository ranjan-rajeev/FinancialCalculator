package com.horizonlabs.financialcalculator.core.domain.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

data class CalculationHistory(
    val id: Long = 0,
    val calculatorId: String,
    val calculatorName: String,
    val calculatorType: String,
    val inputValues: Map<String, Any>,
    val outputSummary: Map<String, Any>?,
    val breakdownData: List<Map<String, Any>>?,
    val timestamp: Long = System.currentTimeMillis()
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CalculatorSummary(
    val items: List<SummaryItem>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SummaryItem(
    val key: String,
    val label: String,
    val value: String,
    val type: SummaryType = SummaryType.KEY_VALUE,
    val formula: String? = null,
    val expression: String? = null,
    val order: Int = 0
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class BreakdownItem(
    val period: String,
    val values: Map<String, String>,
    val children: List<BreakdownItem> = emptyList()
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CalculationResult(
    val summary: CalculatorSummary?,
    val breakdown: List<BreakdownItem>,
    val errors: Map<String, String>,
    val moreInfo: List<MoreInfoItem> = emptyList(),
    /**
     * Unformatted numbers keyed by the same names a chart's `dataKeys` reference.
     *
     * `summary` holds display strings such as `₹5,41,387.88`, which cannot be
     * plotted. Engines publish the figures a chart needs here and the chart config
     * picks them out by key, so adding a chart does not mean reformatting a
     * summary row.
     */
    val rawValues: Map<String, Double> = emptyMap()
)

data class CalculatorInputValues(
    val values: Map<String, Any>
)

sealed interface CalculatorIntent {
    data class OnInputChanged(val key: String, val value: Any) : CalculatorIntent
    object OnCalculate : CalculatorIntent
    object OnClear : CalculatorIntent
    data class OnHistorySelected(val history: CalculationHistory) : CalculatorIntent
    object OnShowHistory : CalculatorIntent
}

data class CalculatorState(
    val inputFields: List<InputFieldConfig> = emptyList(),
    val inputValues: Map<String, Any> = emptyMap(),
    val validationErrors: Map<String, String> = emptyMap(),
    val summary: CalculatorSummary? = null,
    val breakdown: List<BreakdownItem> = emptyList(),
    val moreInfo: List<MoreInfoItem> = emptyList(),
    /** Chart declarations from the calculator's `outputConfig.charts`. */
    val charts: List<ChartConfig> = emptyList(),
    /** Unformatted figures the charts plot, published by the engine. */
    val rawValues: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val showHistory: Boolean = false,
    val calculatorId: String = "",
    val calculatorName: String = ""
)