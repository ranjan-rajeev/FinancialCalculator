package com.horizonlabs.financialcalculator.dashboard.di

import com.horizonlabs.financialcalculator.dashboard.DashboardViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {
    // ViewModel is provided by Hilt automatically via @HiltViewModel
}