package com.horizonlabs.financialcalculator.core.presentation.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Loading(val message: String? = null) : UiState<Nothing>
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>
}

abstract class MviViewModel<Intent, State>(initialState: State) : ViewModel() {
    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    abstract fun onIntent(intent: Intent)

    protected fun reduce(reducer: (State) -> State) {
        _state.value = reducer(_state.value)
    }

    protected fun setState(newState: State) {
        _state.value = newState
    }
}