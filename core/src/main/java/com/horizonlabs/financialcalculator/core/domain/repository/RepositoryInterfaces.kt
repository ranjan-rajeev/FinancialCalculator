package com.horizonlabs.financialcalculator.core.domain.repository

import com.horizonlabs.financialcalculator.core.data.db.CalculationHistoryEntity
import com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorConfig
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
import com.horizonlabs.financialcalculator.core.util.Result
import kotlinx.coroutines.flow.Flow

interface CalculatorRepository {
    suspend fun getDashboard(): Result<DashboardResponse>
    suspend fun getCalculatorConfig(calculatorId: String): Result<CalculatorConfig>
    suspend fun getCalculatorConfigLocal(calculatorId: String): Result<CalculatorConfig>
    suspend fun saveCalculatorConfig(config: CalculatorConfig): Result<Unit>
    fun observeDashboard(): Flow<Result<DashboardResponse>>
    fun observeCalculatorConfig(calculatorId: String): Flow<Result<CalculatorConfig>>
}

interface HistoryRepository {
    suspend fun saveHistory(history: CalculationHistory): Result<Long>
    suspend fun getHistoryByType(calculatorType: String): Result<List<CalculationHistory>>
    fun observeHistoryByType(calculatorType: String): Flow<List<CalculationHistory>>
    suspend fun getAllHistory(): Result<List<CalculationHistory>>
    fun observeAllHistory(): Flow<List<CalculationHistory>>
    suspend fun deleteHistoryByType(calculatorType: String): Result<Int>
    suspend fun deleteAllHistory(): Result<Int>
}

interface ConfigRepository {
    suspend fun getAppConfig(): Result<com.horizonlabs.financialcalculator.core.data.db.ConfigEntity>
    suspend fun saveAppConfig(config: com.horizonlabs.financialcalculator.core.data.db.ConfigEntity): Result<Unit>
    fun observeAppConfig(): Flow<com.horizonlabs.financialcalculator.core.data.db.ConfigEntity?>
}