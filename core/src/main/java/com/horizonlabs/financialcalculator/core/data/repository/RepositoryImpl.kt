package com.horizonlabs.financialcalculator.core.data.repository

import com.horizonlabs.financialcalculator.core.data.db.AppDatabase
import com.horizonlabs.financialcalculator.core.data.db.ConfigEntity
import com.horizonlabs.financialcalculator.core.data.remote.ApiModels
import com.horizonlabs.financialcalculator.core.data.remote.FinanceCalculatorService
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorConfig
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
import com.horizonlabs.financialcalculator.core.domain.model.toDomain
import com.horizonlabs.financialcalculator.core.domain.model.toEntity
import com.horizonlabs.financialcalculator.core.domain.repository.CalculatorRepository
import com.horizonlabs.financialcalculator.core.domain.repository.ConfigRepository
import com.horizonlabs.financialcalculator.core.domain.repository.HistoryRepository
import com.horizonlabs.financialcalculator.core.util.Result
import com.horizonlabs.financialcalculator.core.util.Result.Companion.failure
import com.horizonlabs.financialcalculator.core.util.Result.Companion.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Runs [block] on IO and folds any throw into a failed [Result]. */
private suspend fun <T> apiCall(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
    try {
        success(block())
    } catch (e: Exception) {
        failure(e)
    }
}

@Singleton
class CalculatorRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val apiService: FinanceCalculatorService
) : CalculatorRepository {

    override suspend fun getDashboard(): Result<DashboardResponse> {
        return withContext(Dispatchers.IO) {
            apiCall { ApiModels.toDashboardResponse(apiService.getDashboard()) }
        }
    }

    override suspend fun getCalculatorConfig(calculatorId: String): Result<CalculatorConfig> {
        return withContext(Dispatchers.IO) {
            try {
                val localConfig = runCatching {
                    database.calculatorConfigDao().getConfig(calculatorId)?.toDomain()
                }.getOrNull()

                val remoteConfig = fetchRemoteConfig(calculatorId).getOrNull()

                when {
                    remoteConfig != null && (localConfig == null || remoteConfig.version > localConfig.version) -> {
                        database.calculatorConfigDao().insert(remoteConfig.toEntity())
                        success(remoteConfig)
                    }
                    localConfig != null -> success(localConfig)
                    else -> failure(IllegalStateException("Calculator config not found: $calculatorId"))
                }
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    private suspend fun fetchRemoteConfig(calculatorId: String): Result<CalculatorConfig> {
        return withContext(Dispatchers.IO) {
            apiCall { ApiModels.toCalculatorConfig(apiService.getCalculatorConfig(calculatorId)) }
        }
    }

    override suspend fun getCalculatorConfigLocal(calculatorId: String): Result<CalculatorConfig> {
        return withContext(Dispatchers.IO) {
            try {
                val config = database.calculatorConfigDao().getConfig(calculatorId)
                if (config != null) {
                    success(config.toDomain())
                } else {
                    failure(IllegalStateException("Config not found locally"))
                }
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    override suspend fun saveCalculatorConfig(config: CalculatorConfig): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                database.calculatorConfigDao().insert(config.toEntity())
                success(Unit)
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    override fun observeDashboard(): Flow<Result<DashboardResponse>> {
        // For now, just return empty flow - would need Room-backed caching
        return kotlinx.coroutines.flow.emptyFlow()
    }

    override fun observeCalculatorConfig(calculatorId: String): Flow<Result<CalculatorConfig>> {
        return database.calculatorConfigDao().observeConfig(calculatorId)
            .map { entity ->
                entity?.toDomain()?.let { success(it) } ?: failure(IllegalStateException("Config not found"))
            }
            .distinctUntilChanged()
    }
}

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : HistoryRepository {

    override suspend fun saveHistory(history: com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = history.toEntity()
                val id = database.calculationHistoryDao().insert(entity)
                success(id)
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    override suspend fun getHistoryByType(calculatorType: String): Result<List<com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory>> {
        return withContext(Dispatchers.IO) {
            try {
                val entities = database.calculationHistoryDao().getByCalculatorType(calculatorType).first()
                success(entities.map { it.toDomain() })
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    override fun observeHistoryByType(calculatorType: String): Flow<List<com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory>> {
        return database.calculationHistoryDao().getByCalculatorType(calculatorType)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getAllHistory(): Result<List<com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory>> {
        return withContext(Dispatchers.IO) {
            try {
                val entities = database.calculationHistoryDao().getAll().first()
                success(entities.map { it.toDomain() })
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    override fun observeAllHistory(): Flow<List<com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory>> {
        return database.calculationHistoryDao().getAll()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun deleteHistoryByType(calculatorType: String): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val count = database.calculationHistoryDao().deleteByCalculatorType(calculatorType)
                success(count)
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    override suspend fun deleteAllHistory(): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val count = database.calculationHistoryDao().deleteAll()
                success(count)
            } catch (e: Exception) {
                failure(e)
            }
        }
    }
}

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val apiService: FinanceCalculatorService
) : ConfigRepository {

    override suspend fun getAppConfig(): Result<ConfigEntity> {
        val localConfig = runCatching { database.configDao().getConfig().first() }.getOrNull()
        if (localConfig != null) return success(localConfig)

        return apiCall {
            val entity = apiService.getConfig().toEntity()
            database.configDao().insert(entity)
            entity
        }
    }

    override suspend fun saveAppConfig(config: ConfigEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                database.configDao().insert(config)
                success(Unit)
            } catch (e: Exception) {
                failure(e)
            }
        }
    }

    override fun observeAppConfig(): Flow<ConfigEntity?> {
        return database.configDao().getConfig()
    }
}