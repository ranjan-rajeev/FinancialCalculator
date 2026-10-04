package com.horizonlabs.financialcalculator.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: CalculatorConfigEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(configs: List<CalculatorConfigEntity>)

    @Query("SELECT * FROM calculator_config WHERE id = :calculatorId")
    suspend fun getConfig(calculatorId: String): CalculatorConfigEntity?

    @Query("SELECT * FROM calculator_config WHERE id = :calculatorId")
    fun observeConfig(calculatorId: String): Flow<CalculatorConfigEntity?>

    @Query("SELECT * FROM calculator_config")
    fun getAllConfigs(): Flow<List<CalculatorConfigEntity>>

    @Query("DELETE FROM calculator_config WHERE id = :calculatorId")
    suspend fun deleteConfig(calculatorId: String): Int

    @Query("DELETE FROM calculator_config")
    suspend fun deleteAllConfigs(): Int
}