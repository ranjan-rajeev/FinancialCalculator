package com.horizonlabs.financialcalculator.core.data.remote

import com.horizonlabs.financialcalculator.core.domain.model.AppConfig
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorConfig
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
import com.horizonlabs.financialcalculator.core.util.Constants
import retrofit2.http.GET
import retrofit2.http.Path

interface FinanceCalculatorService {

    @GET("${Constants.API_VERSION}/dashboard.json")
    suspend fun getDashboard(): DashboardResponse

    @GET("${Constants.API_VERSION}/calculators/{calculatorId}.json")
    suspend fun getCalculatorConfig(@Path("calculatorId") calculatorId: String): CalculatorConfig

    @GET("${Constants.API_VERSION}/config.json")
    suspend fun getConfig(): AppConfig
}
