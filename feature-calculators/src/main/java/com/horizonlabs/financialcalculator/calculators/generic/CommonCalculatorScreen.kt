package com.horizonlabs.financialcalculator.calculators.generic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.horizonlabs.financialcalculator.core.domain.model.visibleFields
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorIntent
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorBreakdownSection
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorChartSection
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorInputSection
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorMoreInfoSection
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorSummarySection
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorTopBar

@Composable
fun CommonCalculatorScreen(
    viewModel: CommonCalculatorViewModel,
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CalculatorTopBar(
                title = state.calculatorName,
                onBackClick = onBackClick,
                onHistoryClick = onHistoryClick,
                showBackArrow = true
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.validationErrors["general"]?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (state.inputFields.isNotEmpty()) {
                        CalculatorInputSection(
                            fields = state.inputFields.visibleFields(state.inputValues),
                            values = state.inputValues,
                            errors = state.validationErrors,
                            onInputChange = { key, value ->
                                viewModel.onIntent(CalculatorIntent.OnInputChanged(key, value))
                            },
                            onCalculate = { viewModel.onIntent(CalculatorIntent.OnCalculate) }
                        )
                    }

                    state.summary?.let { summary ->
                        CalculatorSummarySection(summary = summary, modifier = Modifier.padding(0.dp))
                    }

                    CalculatorChartSection(
                        charts = state.charts,
                        rawValues = state.rawValues,
                        modifier = Modifier.padding(0.dp)
                    )

                    if (state.breakdown.isNotEmpty()) {
                        CalculatorBreakdownSection(
                            title = "Breakdown",
                            items = state.breakdown,
                            modifier = Modifier.padding(0.dp)
                        )
                    }

                    if (state.moreInfo.isNotEmpty()) {
                        CalculatorMoreInfoSection(items = state.moreInfo, modifier = Modifier.padding(0.dp))
                    }
                }
            }
        }
    }
}
