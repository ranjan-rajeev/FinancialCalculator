package com.horizonlabs.financialcalculator.core.domain.usecase

import com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorConfig
import com.horizonlabs.financialcalculator.core.domain.model.CalculationResult
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
import com.horizonlabs.financialcalculator.core.domain.engine.CalculatorEngine
import com.horizonlabs.financialcalculator.core.domain.repository.CalculatorRepository
import com.horizonlabs.financialcalculator.core.domain.repository.ConfigRepository
import com.horizonlabs.financialcalculator.core.domain.repository.HistoryRepository
import com.horizonlabs.financialcalculator.core.util.Result
import com.horizonlabs.financialcalculator.core.util.Result.Companion.failure
import com.horizonlabs.financialcalculator.core.util.Result.Companion.success
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDashboardUseCaseImpl @Inject constructor(
    private val repository: CalculatorRepository
) : GetDashboardUseCase {
    override operator suspend fun invoke(): Result<DashboardResponse> {
        return repository.getDashboard()
    }
}

@Singleton
class GetCalculatorConfigUseCaseImpl @Inject constructor(
    private val repository: CalculatorRepository
) : GetCalculatorConfigUseCase {
    override operator suspend fun invoke(calculatorId: String): Result<CalculatorConfig> {
        return repository.getCalculatorConfig(calculatorId)
    }
}

@Singleton
class CalculateUseCaseImpl @Inject constructor(
    private val engines: Map<String, CalculatorEngine>
) : CalculateUseCase {
    override operator suspend fun invoke(calculatorId: String, inputValues: CalculatorInputValues): Result<CalculationResult> {
        val engine = engines.values.find { calculatorId in it.getSupportedCalculatorTypes() }
            ?: return failure(IllegalArgumentException("No engine found for calculator: $calculatorId"))
        
        return engine.calculate(inputValues)
    }
}

@Singleton
class SaveHistoryUseCaseImpl @Inject constructor(
    private val repository: HistoryRepository
) : SaveHistoryUseCase {
    override operator suspend fun invoke(history: CalculationHistory): Result<Long> {
        return repository.saveHistory(history)
    }
}

@Singleton
class GetHistoryUseCaseImpl @Inject constructor(
    private val repository: HistoryRepository
) : GetHistoryUseCase {
    override operator suspend fun invoke(calculatorType: String): Result<List<CalculationHistory>> {
        return repository.getHistoryByType(calculatorType)
    }
}

@Singleton
class GetAppConfigUseCaseImpl @Inject constructor(
    private val repository: ConfigRepository
) : GetAppConfigUseCase {
    override operator suspend fun invoke(): Result<com.horizonlabs.financialcalculator.core.data.db.ConfigEntity> {
        return repository.getAppConfig()
    }
}