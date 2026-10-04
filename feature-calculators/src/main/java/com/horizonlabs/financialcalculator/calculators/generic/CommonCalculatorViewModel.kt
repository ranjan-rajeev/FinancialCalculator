package com.horizonlabs.financialcalculator.calculators.generic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory
import com.horizonlabs.financialcalculator.core.domain.model.CalculationResult
import com.horizonlabs.financialcalculator.core.domain.model.BreakdownItem
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorState
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorIntent
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorSummary
import com.horizonlabs.financialcalculator.core.domain.model.InputFieldConfig
import com.horizonlabs.financialcalculator.core.domain.model.SummaryItem
import com.horizonlabs.financialcalculator.core.domain.model.defaultValues
import com.horizonlabs.financialcalculator.core.domain.model.visibleFields
import com.horizonlabs.financialcalculator.core.domain.model.visibleValues
import com.horizonlabs.financialcalculator.core.domain.usecase.CalculateUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.GetCalculatorConfigUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.GetHistoryUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.SaveHistoryUseCase
import com.horizonlabs.financialcalculator.core.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonCalculatorViewModel @Inject constructor(
    private val getConfigUseCase: GetCalculatorConfigUseCase,
    private val calculateUseCase: CalculateUseCase,
    private val saveHistoryUseCase: SaveHistoryUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : MviViewModel<CalculatorIntent, CalculatorState>(
    CalculatorState(
        calculatorId = savedStateHandle[ARG_CALCULATOR_ID] ?: "",
        calculatorName = savedStateHandle[ARG_CALCULATOR_NAME] ?: ""
    )
) {

    private val calculatorId: String = savedStateHandle[ARG_CALCULATOR_ID] ?: ""

    init {
        loadCalculatorConfig()
    }

    private fun loadCalculatorConfig() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = getConfigUseCase(calculatorId)
            result.onSuccess { config ->
                val fields = config.inputFields.sortedBy { it.order }
                _state.update { it.copy(
                    calculatorName = config.name,
                    inputFields = fields,
                    // Defaults must live in state, not just in the rendered text, or a
                    // required field with a published default is reported as empty.
                    inputValues = fields.defaultValues(),
                    isLoading = false
                )}
            }.onFailure { error ->
                _state.update { it.copy(
                    isLoading = false,
                    validationErrors = mapOf("general" to (error.message ?: "Failed to load calculator"))
                )}
            }
        }
    }

    override fun onIntent(intent: CalculatorIntent) {
        when (intent) {
            is CalculatorIntent.OnInputChanged -> updateInput(intent.key, intent.value)
            is CalculatorIntent.OnCalculate -> calculate()
            is CalculatorIntent.OnClear -> clear()
            is CalculatorIntent.OnHistorySelected -> restoreHistory(intent.history)
            is CalculatorIntent.OnShowHistory -> _state.update { it.copy(showHistory = true) }
        }
    }

    private fun updateInput(key: String, value: Any) {
        _state.update { current ->
            val newValues = current.inputValues + (key to value)
            val newErrors = current.validationErrors - key
            current.copy(inputValues = newValues, validationErrors = newErrors)
        }
    }

    private fun calculate() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, validationErrors = emptyMap()) }
            
            // Validate required fields
            val errors = validateInputs()
            if (errors.isNotEmpty()) {
                _state.update { it.copy(isLoading = false, validationErrors = errors) }
                return@launch
            }
            
            // `calculatorId` is injected so engines that dispatch on calculator type
            // (e.g. GenericFormulaEngine) don't need a hidden field in the JSON config.
            val inputValues = CalculatorInputValues(
                _state.value.inputFields.visibleValues(
                    values = _state.value.inputValues,
                    extra = mapOf(ARG_CALCULATOR_ID to calculatorId)
                )
            )
            val result = calculateUseCase(calculatorId, inputValues)
            
            result.onSuccess { calculationResult ->
                _state.update { it.copy(
                    summary = calculationResult.summary,
                    breakdown = calculationResult.breakdown,
                    moreInfo = calculationResult.moreInfo,
                    isLoading = false,
                    validationErrors = calculationResult.errors
                )}
                
                if (calculationResult.errors.isEmpty()) {
                    saveHistory(calculationResult)
                }
            }.onFailure { error ->
                _state.update { it.copy(
                    isLoading = false,
                    validationErrors = mapOf("general" to (error.message ?: "Calculation failed"))
                )}
            }
        }
    }

    private fun validateInputs(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val state = _state.value
        
        state.inputFields.visibleFields(state.inputValues).forEach { field ->
            if (field !is InputFieldConfig.EditText) return@forEach
            val validation = field.validation ?: return@forEach
            val value = state.inputValues[field.key]?.toString() ?: ""

            if (validation.required && value.isBlank()) {
                errors[field.key] = "${field.label} is required"
            } else if (value.isNotBlank()) {
                val numericValue = value.toDoubleOrNull()
                if (numericValue != null) {
                    validation.min?.let { min ->
                        if (numericValue < min) errors[field.key] = "${field.label} must be >= $min"
                    }
                    validation.max?.let { max ->
                        if (numericValue > max) errors[field.key] = "${field.label} must be <= $max"
                    }
                }
            }
        }
        
        return errors
    }

    private fun saveHistory(result: CalculationResult) {
        viewModelScope.launch {
            val history = CalculationHistory(
                calculatorId = calculatorId,
                calculatorName = _state.value.calculatorName,
                calculatorType = _state.value.calculatorId, // Use ID as type for now
                inputValues = _state.value.inputValues,
                outputSummary = result.summary?.let { summary ->
                    summary.items.associateBy({ it.key }, { it.value })
                } ?: emptyMap(),
                breakdownData = result.breakdown.map { it.values }
            )
            saveHistoryUseCase(history)
        }
    }

    private fun clear() {
        _state.update { it.copy(
            inputValues = it.inputFields.defaultValues(),
            validationErrors = emptyMap(),
            summary = null,
            breakdown = emptyList(),
            moreInfo = emptyList()
        )}
    }

    private fun restoreHistory(history: CalculationHistory) {
        _state.update { it.copy(
            inputValues = history.inputValues,
            summary = history.outputSummary?.let { map ->
                CalculatorSummary(
                    items = map.map { (key, value) -> SummaryItem(key, key, value.toString()) }
                )
            },
            breakdown = history.breakdownData?.map { values ->
                BreakdownItem(
                    period = values["period"]?.toString() ?: "",
                    values = values.mapValues { (_, value) -> value.toString() }
                )
            } ?: emptyList(),
            validationErrors = emptyMap()
        )}
    }

    companion object {
        const val ARG_CALCULATOR_ID = "calculatorId"
        const val ARG_CALCULATOR_NAME = "calculatorName"
    }
}
