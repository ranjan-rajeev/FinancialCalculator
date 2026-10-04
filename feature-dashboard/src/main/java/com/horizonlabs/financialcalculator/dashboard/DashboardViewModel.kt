package com.horizonlabs.financialcalculator.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
import com.horizonlabs.financialcalculator.core.domain.usecase.GetDashboardUseCase
import com.horizonlabs.financialcalculator.core.presentation.mvi.MviViewModel
import com.horizonlabs.financialcalculator.core.presentation.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardIntent {
    data object OnRefresh : DashboardIntent
    data object OnRetry : DashboardIntent
    data class OnCalculatorClick(val calculatorId: String, val calculatorName: String) : DashboardIntent
    data class OnBannerClick(val actionUrl: String?, val actionType: String) : DashboardIntent
}

data class DashboardState(
    val dashboard: DashboardResponse? = null,
    val uiState: UiState<DashboardResponse> = UiState.Loading()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase
) : MviViewModel<DashboardIntent, DashboardState>(DashboardState()) {

    init {
        loadDashboard()
    }

    override fun onIntent(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.OnRefresh, DashboardIntent.OnRetry -> loadDashboard()
            is DashboardIntent.OnCalculatorClick -> Unit
            is DashboardIntent.OnBannerClick -> Unit
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = UiState.Loading()) }

            getDashboardUseCase()
                .onSuccess { dashboard ->
                    _state.update {
                        it.copy(dashboard = dashboard, uiState = UiState.Success(dashboard))
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            uiState = UiState.Error(
                                message = error.message ?: "Failed to load dashboard",
                                throwable = error
                            )
                        )
                    }
                }
        }
    }
}
