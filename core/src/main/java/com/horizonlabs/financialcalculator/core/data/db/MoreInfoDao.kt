package com.horizonlabs.financialcalculator.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoreInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(moreInfo: MoreInfoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(moreInfoList: List<MoreInfoEntity>)

    @Query("SELECT * FROM more_info WHERE calculatorId = :calculatorId")
    fun getByCalculatorId(calculatorId: String): Flow<List<MoreInfoEntity>>

    @Query("SELECT * FROM more_info WHERE firebaseId = :firebaseId")
    suspend fun getByFirebaseId(firebaseId: String): MoreInfoEntity?

    @Query("DELETE FROM more_info WHERE calculatorId = :calculatorId")
    suspend fun deleteByCalculatorId(calculatorId: String): Int

    @Query("DELETE FROM more_info")
    suspend fun deleteAll(): Int
}