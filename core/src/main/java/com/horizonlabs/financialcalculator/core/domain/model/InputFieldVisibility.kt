package com.horizonlabs.financialcalculator.core.domain.model

/**
 * Conditional visibility for input fields, published as `visibleWhen` in the
 * hosted calculator JSON.
 *
 * A field with no `visibleWhen` is always shown. A field with a condition is
 * shown only while the condition holds, and hidden fields are excluded from
 * validation and from the value map handed to the calculator engine so a stale
 * value cannot leak into a result.
 */
fun VisibilityCondition.evaluate(values: Map<String, Any>): Boolean {
    val actual = values[field]?.toString()?.trim().orEmpty()
    val expected = value.trim()

    return when (operator) {
        VisibilityOperator.EQ -> actual.equals(expected, ignoreCase = true)
        VisibilityOperator.NE -> !actual.equals(expected, ignoreCase = true)
        VisibilityOperator.CONTAINS -> actual.contains(expected, ignoreCase = true)
        VisibilityOperator.GT, VisibilityOperator.LT,
        VisibilityOperator.GTE, VisibilityOperator.LTE -> {
            // Non-numeric operands cannot satisfy an ordering, so the field stays hidden.
            val left = actual.toDoubleOrNull()
            val right = expected.toDoubleOrNull()
            if (left == null || right == null) {
                false
            } else {
                when (operator) {
                    VisibilityOperator.GT -> left > right
                    VisibilityOperator.LT -> left < right
                    VisibilityOperator.GTE -> left >= right
                    else -> left <= right
                }
            }
        }
    }
}

fun InputFieldConfig.isVisible(values: Map<String, Any>): Boolean =
    visibleWhen?.evaluate(values) ?: true

fun List<InputFieldConfig>.visibleFields(values: Map<String, Any>): List<InputFieldConfig> =
    filter { it.isVisible(values) }

/** Values for the fields that are currently on screen, plus any keys the engine needs directly. */
fun List<InputFieldConfig>.visibleValues(
    values: Map<String, Any>,
    extra: Map<String, Any> = emptyMap()
): Map<String, Any> {
    val keys = visibleFields(values).mapTo(mutableSetOf()) { it.key }
    return values.filterKeys { it in keys } + extra
}

/**
 * Default published for this field, if any.
 *
 * `DatePicker` is excluded on purpose: its `defaultValue` is the `TODAY` sentinel
 * rather than a literal the engine can consume.
 */
val InputFieldConfig.publishedDefault: String?
    get() = when (this) {
        is InputFieldConfig.EditText -> defaultValue
        is InputFieldConfig.Spinner -> defaultValue
        else -> null
    }

/** Seed state from the published defaults so required fields are not reported empty. */
fun List<InputFieldConfig>.defaultValues(): Map<String, Any> =
    mapNotNull { field -> field.publishedDefault?.let { field.key to it } }.toMap()
