package com.horizonlabs.financialcalculator.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: ConfigEntity): Long

    @Query("SELECT * FROM app_config WHERE key = 'app_config'")
    fun getConfig(): Flow<ConfigEntity?>

    @Query("DELETE FROM app_config")
    suspend fun deleteConfig(): Int
}