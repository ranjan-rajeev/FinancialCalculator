package com.horizonlabs.financialcalculator.core.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun DatePickerField(
    field: InputFieldConfig.DatePicker,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(value) { mutableStateOf(value) }

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            onValueChange(newText)
        },
        label = { Text(field.label) },
        modifier = modifier.fillMaxWidth()
    )
}

internal fun InputDataType.keyboardType(): KeyboardType = when (this) {
    InputDataType.NUMBER -> KeyboardType.Number
    InputDataType.DECIMAL -> KeyboardType.Decimal
    InputDataType.PHONE -> KeyboardType.Phone
    InputDataType.EMAIL -> KeyboardType.Email
    InputDataType.TEXT -> KeyboardType.Text
}
