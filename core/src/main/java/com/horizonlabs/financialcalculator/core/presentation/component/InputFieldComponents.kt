package com.horizonlabs.financialcalculator.core.presentation.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.horizonlabs.financialcalculator.core.domain.model.ButtonAction
import com.horizonlabs.financialcalculator.core.domain.model.InputDataType
import com.horizonlabs.financialcalculator.core.domain.model.InputFieldConfig
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors
import com.horizonlabs.financialcalculator.core.util.Formatters

@Composable
fun CalculatorInputSection(
    fields: List<InputFieldConfig>,
    values: Map<String, Any>,
    errors: Map<String, String>,
    onInputChange: (String, Any) -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(LegacyFieldSpacing)
    ) {
            fields.sortedBy { it.order }.forEach { field ->
                when (field) {
                    is InputFieldConfig.EditText -> EditTextField(
                        field = field,
                        value = values[field.key]?.toString() ?: field.defaultValue.orEmpty(),
                        error = errors[field.key],
                        // Keep the raw text: engines parse strings themselves, and
                        // round-tripping through Double showed "500000.0" back to the user.
                        onValueChange = { newValue -> onInputChange(field.key, newValue) }
                    )

                    is InputFieldConfig.Spinner -> SpinnerField(
                        field = field,
                        value = values[field.key]?.toString()
                            ?: field.defaultValue
                            ?: field.options.firstOrNull()?.value.orEmpty(),
                        onValueChange = { onInputChange(field.key, it) }
                    )

                    is InputFieldConfig.DatePicker -> DatePickerField(
                        field = field,
                        value = values[field.key]?.toString() ?: field.defaultValue,
                        onValueChange = { onInputChange(field.key, it) }
                    )

                    is InputFieldConfig.Button -> if (field.action == ButtonAction.CALCULATE) {
                        LegacyButton(
                            text = field.label,
                            onClick = onCalculate,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    is InputFieldConfig.Spacer -> Spacer(modifier = Modifier.height(field.heightDp.dp))

                    is InputFieldConfig.InfoText -> Text(
                        text = field.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
    }
}

@Composable
fun EditTextField(
    field: InputFieldConfig.EditText,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LegacyTextField(
        label = field.label,
        value = value,
        onValueChange = onValueChange,
        placeholder = field.hint,
        required = field.validation?.required == true,
        error = error,
        keyboardOptions = KeyboardOptions(keyboardType = field.inputType.keyboardType()),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinnerField(
    field: InputFieldConfig.Spinner,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = field.options.find { it.value == value } ?: field.options.firstOrNull()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption?.label ?: field.label,
            onValueChange = {},
            label = { Text(field.label) },
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = LegacyColors.TextPrimary),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(LegacyDimens.InputRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LegacyColors.Primary,
                unfocusedBorderColor = LegacyColors.InputStroke,
                focusedLabelColor = LegacyColors.Primary,
                unfocusedLabelColor = LegacyColors.TextDescription
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = LegacyDimens.InputMinHeight)
                .padding(horizontal = 10.dp)
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            field.options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Read-only field that opens the Material date picker.
 *
 * The retired app used `android.app.DatePickerDialog`; this keeps that behaviour
 * while matching the rest of the form. Typing is disabled so the value can only
 * ever be a real date — the previous free-text field silently accepted anything
 * and handed the engine a string it could not parse.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    field: InputFieldConfig.DatePicker,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    // `publishedDefault` is null for date fields, so an empty value still means
    // "today" for display purposes while the engine keeps its own default.
    val selected = remember(value) {
        Formatters.dateStringToUtcMillis(value)
            ?.let { Formatters.utcMillisToDateString(it) }
            ?: Formatters.todayDateString()
    }
    val minMillis = remember(field.minDate) { Formatters.resolveDateBound(field.minDate) }
    val maxMillis = remember(field.maxDate) { Formatters.resolveDateBound(field.maxDate) }

    // A read-only OutlinedTextField still handles presses for cursor placement, so
    // the tap has to be observed on the interaction source rather than by wrapping
    // the field in a clickable.
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) showDialog = true
        }
    }

    LegacyTextField(
        label = field.label,
        value = selected,
        onValueChange = {},
        modifier = modifier,
        readOnly = true,
        interactionSource = interactionSource,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Pick ${field.label}",
                tint = LegacyColors.TextDescription
            )
        }
    )

    if (showDialog) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = Formatters.dateStringToUtcMillis(selected)
                ?: Formatters.todayUtcMillis(),
            // The picker works in UTC midnight; widen each bound by a day so a
            // bound that is itself selectable is not shifted out of range.
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    (minMillis == null || utcTimeMillis >= minMillis - DAY_MILLIS) &&
                        (maxMillis == null || utcTimeMillis <= maxMillis + DAY_MILLIS)

                override fun isSelectableYear(year: Int): Boolean = true
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let {
                            onValueChange(Formatters.utcMillisToDateString(it))
                        }
                        showDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

private const val DAY_MILLIS = 24L * 60 * 60 * 1000

internal fun InputDataType.keyboardType(): KeyboardType = when (this) {
    InputDataType.NUMBER -> KeyboardType.Number
    InputDataType.DECIMAL -> KeyboardType.Decimal
    InputDataType.PHONE -> KeyboardType.Phone
    InputDataType.EMAIL -> KeyboardType.Email
    InputDataType.TEXT -> KeyboardType.Text
}
