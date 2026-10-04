package com.horizonlabs.financialcalculator.core.domain.usecase

import com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorConfig
import com.horizonlabs.financialcalculator.core.domain.model.CalculationResult
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorInputValues
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
import com.horizonlabs.financialcalculator.core.util.Result
import kotlinx.coroutines.flow.Flow

interface GetDashboardUseCase {
    operator suspend fun invoke(): Result<DashboardResponse>
}

interface GetCalculatorConfigUseCase {
    operator suspend fun invoke(calculatorId: String): Result<CalculatorConfig>
}

interface CalculateUseCase {
    operator suspend fun invoke(calculatorId: String, inputValues: CalculatorInputValues): Result<CalculationResult>
}

interface SaveHistoryUseCase {
    operator suspend fun invoke(history: CalculationHistory): Result<Long>
}

interface GetHistoryUseCase {
    operator suspend fun invoke(calculatorType: String): Result<List<CalculationHistory>>
}

/** Streams every saved calculation, newest first, so the History screen can react to new writes. */
interface ObserveAllHistoryUseCase {
    operator fun invoke(): Flow<List<CalculationHistory>>
}

interface DeleteAllHistoryUseCase {
    suspend operator fun invoke(): Result<Int>
}

interface GetAppConfigUseCase {
    operator suspend fun invoke(): Result<com.horizonlabs.financialcalculator.core.data.db.ConfigEntity>
}