package com.horizonlabs.financialcalculator.core.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun Context.hideKeyboard(view: View? = null) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken ?: return, 0)
}

fun Activity.hideKeyboard(view: View? = null) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken ?: currentFocus?.windowToken ?: return, 0)
}

@Composable
inline fun <reified T : ViewModel> ViewModelStoreOwner.hiltViewModel(
    factory: androidx.lifecycle.ViewModelProvider.Factory? = null,
    extras: androidx.lifecycle.viewmodel.CreationExtras? = null
): T = androidx.lifecycle.viewmodel.compose.viewModel(
    key = "hiltViewModel",
    factory = factory,
    extras = extras ?: androidx.lifecycle.viewmodel.CreationExtras.Empty
)

@Composable
fun <T> rememberMutableState(initialValue: T): androidx.compose.runtime.MutableState<T> =
    remember { mutableStateOf(initialValue) }

fun CoroutineScope.launchOnMain(block: suspend () -> Unit) =
    launch(Dispatchers.Main) { block }

fun CoroutineScope.launchOnIO(block: suspend () -> Unit) =
    launch(Dispatchers.IO) { block }

fun CoroutineScope.launchOnDefault(block: suspend () -> Unit) =
    launch(Dispatchers.Default) { block }

fun LifecycleOwner.coroutineScope(): kotlinx.coroutines.CoroutineScope = lifecycleScope

fun ViewModel.coroutineScope(): kotlinx.coroutines.CoroutineScope = viewModelScope

fun String.removeCommas(): String = replace(",", "")

fun String.removeCurrencySymbol(): String = replace(Constants.CURRENCY_SYMBOL, "").trim()

fun String.removePercentageSymbol(): String = replace("%", "").trim()

fun String.toDoubleSafe(default: Double = 0.0): Double = toDoubleOrNull() ?: default

fun Double.toIntSafe(): Int = this.toInt()

fun <T> List<T>.ifNotEmpty(block: (List<T>) -> Unit): List<T>? {
    if (isNotEmpty()) block(this)
    return if (isNotEmpty()) this else null
}

@Composable
fun <T> rememberWithLifecycle(
    key: Any?,
    init: () -> T
): T {
    val scope = remember { kotlinx.coroutines.SupervisorJob() }
    val value = remember(key) { init() }
    androidx.compose.runtime.DisposableEffect(scope) {
        onDispose { scope.cancel() }
    }
    return value
}