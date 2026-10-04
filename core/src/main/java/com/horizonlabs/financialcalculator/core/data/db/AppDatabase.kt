package com.horizonlabs.financialcalculator.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.horizonlabs.financialcalculator.core.util.Constants

@Database(
    entities = [
        CalculationHistoryEntity::class,
        MoreInfoEntity::class,
        ConfigEntity::class,
        CalculatorConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calculationHistoryDao(): CalculationHistoryDao
    abstract fun moreInfoDao(): MoreInfoDao
    abstract fun configDao(): ConfigDao
    abstract fun calculatorConfigDao(): CalculatorConfigDao

    companion object {
        @Suppress("UNUSED_PARAMETER")
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.Database.NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}