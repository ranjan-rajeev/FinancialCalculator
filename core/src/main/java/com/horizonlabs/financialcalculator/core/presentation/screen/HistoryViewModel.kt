package com.horizonlabs.financialcalculator.core.presentation.screen

import androidx.lifecycle.viewModelScope
import com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory
import com.horizonlabs.financialcalculator.core.domain.usecase.DeleteAllHistoryUseCase
import com.horizonlabs.financialcalculator.core.domain.usecase.ObserveAllHistoryUseCase
import com.horizonlabs.financialcalculator.core.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HistoryIntent {
    data object OnClearAll : HistoryIntent
}

data class HistoryState(
    val entries: List<CalculationHistory> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    observeAllHistoryUseCase: ObserveAllHistoryUseCase,
    private val deleteAllHistoryUseCase: DeleteAllHistoryUseCase
) : MviViewModel<HistoryIntent, HistoryState, Nothing>(HistoryState()) {

    init {
        // Room emits on every insert/delete, so the list stays live without a manual refresh.
        observeAllHistoryUseCase()
            .onEach { entries -> setState(HistoryState(entries = entries, loading = false)) }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.OnClearAll -> viewModelScope.launch {
                deleteAllHistoryUseCase()
            }
        }
    }
}