package com.horizonlabs.financialcalculator.core.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Loading(val message: String? = null) : UiState<Nothing>
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>
}

/**
 * One-shot side effects (navigation, share sheets, dialogs) that must not be replayed on
 * configuration change, unlike [state]. BUFFERED keeps an effect emitted while the screen is
 * stopped from being dropped before collection resumes.
 */
abstract class MviViewModel<Intent, State, Effect>(initialState: State) : ViewModel() {
    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    abstract fun onIntent(intent: Intent)

    protected fun reduce(reducer: (State) -> State) {
        _state.value = reducer(_state.value)
    }

    protected fun setState(newState: State) {
        _state.value = newState
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}