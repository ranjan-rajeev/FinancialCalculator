package com.horizonlabs.financialcalculator.core.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: CalculationHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(histories: List<CalculationHistoryEntity>)

    @Query("SELECT * FROM calculation_history WHERE calculatorType = :calculatorType ORDER BY timestamp DESC")
    fun getByCalculatorType(calculatorType: String): Flow<List<CalculationHistoryEntity>>

    @Query("SELECT * FROM calculation_history WHERE calculatorType = :calculatorType ORDER BY timestamp DESC LIMIT :limit")
    fun getByCalculatorTypeLimited(calculatorType: String, limit: Int): Flow<List<CalculationHistoryEntity>>

    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<CalculationHistoryEntity>>

    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC LIMIT :limit")
    fun getAllLimited(limit: Int): Flow<List<CalculationHistoryEntity>>

    @Query("DELETE FROM calculation_history WHERE calculatorType = :calculatorType")
    suspend fun deleteByCalculatorType(calculatorType: String): Int

    @Query("DELETE FROM calculation_history")
    suspend fun deleteAll(): Int

    @Query("SELECT COUNT(*) FROM calculation_history WHERE calculatorType = :calculatorType")
    suspend fun countByCalculatorType(calculatorType: String): Int
}