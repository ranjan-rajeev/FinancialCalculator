package com.horizonlabs.financialcalculator.core.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalculatorConfig(
    val id: String,
    val name: String,
    val type: CalculatorType,
    val version: Int,
    val category: String,
    val engine: String,
    val inputFields: List<InputFieldConfig>,
    val outputConfig: OutputConfig,
    val moreInfo: List<MoreInfoItem>,
    val formulaMap: Map<String, String>? = null
)

@Serializable
enum class CalculatorType {
    CUSTOM, GENERIC
}

@Serializable
sealed interface InputFieldConfig {
    val key: String
    val order: Int
    val visibleWhen: VisibilityCondition?

    @Serializable
    @SerialName("EDIT_TEXT")
    data class EditText(
        override val key: String,
        val label: String,
        val hint: String? = null,
        val inputType: InputDataType,
        val validation: ValidationRule? = null,
        val defaultValue: String? = null,
        val formatter: FormatterType = FormatterType.NONE,
        override val order: Int = 0,
        override val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig

    @Serializable
    @SerialName("SPINNER")
    data class Spinner(
        override val key: String,
        val label: String,
        val options: List<SpinnerOption>,
        val defaultValue: String? = null,
        override val order: Int = 0,
        override val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig

    @Serializable
    @SerialName("DATE_PICKER")
    data class DatePicker(
        override val key: String,
        val label: String,
        val defaultValue: String = "TODAY",
        /** Inclusive lower bound as `dd-MM-yyyy`. Null means unbounded. */
        val minDate: String? = null,
        /** Inclusive upper bound as `dd-MM-yyyy`. Null means unbounded. */
        val maxDate: String? = null,
        override val order: Int = 0,
        override val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig

    @Serializable
    @SerialName("BUTTON")
    data class Button(
        override val key: String,
        val label: String,
        val action: ButtonAction,
        val style: ButtonStyle = ButtonStyle.PRIMARY,
        override val order: Int = 0,
        override val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig

    @Serializable
    @SerialName("SPACER")
    data class Spacer(
        override val key: String,
        val heightDp: Int = 16,
        override val order: Int = 0,
        override val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig

    @Serializable
    @SerialName("INFO_TEXT")
    data class InfoText(
        override val key: String,
        val text: String,
        override val order: Int = 0,
        override val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig
}

@Serializable
enum class InputDataType {
    NUMBER, DECIMAL, TEXT, PHONE, EMAIL
}

@Serializable
data class ValidationRule(
    val required: Boolean = false,
    val min: Double? = null,
    val max: Double? = null,
    val regex: String? = null
)

@Serializable
data class SpinnerOption(
    val label: String,
    val value: String
)

@Serializable
enum class FormatterType {
    NONE, CURRENCY_INR, PERCENTAGE, COMPACT_NUMBER
}

@Serializable
enum class ButtonAction {
    CALCULATE, CLEAR, NAVIGATE
}

@Serializable
enum class ButtonStyle {
    PRIMARY, SECONDARY, OUTLINE
}

@Serializable
data class VisibilityCondition(
    val field: String,
    val operator: VisibilityOperator,
    val value: String
)

@Serializable
enum class VisibilityOperator {
    EQ, NE, GT, LT, GTE, LTE, CONTAINS
}

@Serializable
data class OutputConfig(
    val summary: List<SummaryItemConfig>,
    val breakdown: BreakdownConfig?,
    val charts: List<ChartConfig>,
    val formulas: List<FormulaConfig>
)

@Serializable
data class SummaryItemConfig(
    val key: String,
    val label: String,
    val type: SummaryType,
    val formatter: FormatterType = FormatterType.NONE,
    val order: Int = 0
)

@Serializable
enum class SummaryType {
    KEY_VALUE, FORMULA, EXPRESSION_WITH_FORMULA, GRAPH, DIVIDER
}

@Serializable
data class BreakdownConfig(
    val enabled: Boolean = false,
    val groupBy: GroupByType = GroupByType.YEAR,
    val columns: List<String> = emptyList(),
    val expandable: Boolean = true
)

@Serializable
enum class GroupByType {
    YEAR, MONTH, QUARTER, NONE
}

@Serializable
data class ChartConfig(
    val type: ChartType,
    val dataKeys: List<String>,
    val title: String
)

@Serializable
enum class ChartType {
    PIE, BAR, LINE, DONUT
}

@Serializable
data class FormulaConfig(
    val key: String,
    val label: String,
    val expression: String,
    val variables: List<String>
)

@Serializable
data class MoreInfoItem(
    val question: String,
    val answer: String
)