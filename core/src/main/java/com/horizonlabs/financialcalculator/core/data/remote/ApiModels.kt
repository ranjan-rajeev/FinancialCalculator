package com.horizonlabs.financialcalculator.core.data.remote

import com.horizonlabs.financialcalculator.core.domain.model.CalculatorConfig
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse

/**
 * The app speaks v2 only. `v1` documents stay on GitHub Pages for already-published
 * APKs, which parse them with their own retired Java deserializers.
 */
object ApiModels {

    fun toDashboardResponse(response: DashboardResponse): DashboardResponse = response

    fun toCalculatorConfig(response: CalculatorConfig): CalculatorConfig = response
}
