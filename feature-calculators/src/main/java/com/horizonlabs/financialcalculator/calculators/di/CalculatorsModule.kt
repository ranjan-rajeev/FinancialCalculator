package com.horizonlabs.financialcalculator.calculators.di

import com.horizonlabs.financialcalculator.calculators.generic.CommonCalculatorViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalculatorsModule {
    // ViewModels are provided by Hilt automatically via @HiltViewModel
}