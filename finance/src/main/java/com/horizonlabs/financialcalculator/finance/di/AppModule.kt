package com.horizonlabs.financialcalculator.finance.di

import com.horizonlabs.financialcalculator.core.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // App-level dependencies can be added here
    // For now, core module provides everything needed
}