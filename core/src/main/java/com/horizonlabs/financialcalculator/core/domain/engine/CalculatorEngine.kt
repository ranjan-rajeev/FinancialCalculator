package com.horizonlabs.financialcalculator.core.domain.engine

import com.horizonlabs.financialcalculator.core.domain.model.CalculationResult
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.util.Result

interface CalculatorEngine {
    fun calculate(input: CalculatorInputValues): Result<CalculationResult>
    fun getSupportedCalculatorTypes(): List<String>
}
