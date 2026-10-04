package com.horizonlabs.financialcalculator.core.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizonlabs.financialcalculator.core.domain.model.CalculationHistory
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorTopBar
import com.horizonlabs.financialcalculator.core.presentation.component.LegacyDimens
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors
import com.horizonlabs.financialcalculator.core.util.Formatters

private const val MAX_VISIBLE_ROWS = 3

@Composable
fun HistoryScreen(
    entries: List<CalculationHistory>,
    onBackClick: () -> Unit,
    onClearAllClick: () -> Unit,
    onEntryClick: (CalculationHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmClear by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        CalculatorTopBar(
            title = "History",
            onBackClick = onBackClick,
            showBackArrow = true
        )

        if (entries.isEmpty()) {
            EmptyHistoryView(modifier = Modifier.weight(1f))
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = LegacyDimens.CardMargin,
                end = LegacyDimens.CardMargin,
                top = 8.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "__header__") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (entries.size == 1) "1 calculation" else "${entries.size} calculations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LegacyColors.TextDark
                    )
                    TextButton(onClick = { confirmClear = true }) {
                        Text("Clear all", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            entries.forEachIndexed { index, entry ->
                val previous = entries.getOrNull(index - 1)
                val showDayHeader = previous == null ||
                    !Formatters.formatRelativeDay(previous.timestamp)
                        .equals(Formatters.formatRelativeDay(entry.timestamp))

                if (showDayHeader) {
                    item(key = "day_${entry.timestamp}") {
                        Text(
                            text = Formatters.formatRelativeDay(entry.timestamp),
                            style = MaterialTheme.typography.labelLarge,
                            color = LegacyColors.TextLight,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }
                }

                item(key = "row_${entry.id}") {
                    HistoryRow(entry = entry, onClick = { onEntryClick(entry) })
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear history?") },
            text = { Text("This permanently deletes all ${entries.size} saved calculations.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmClear = false
                        onClearAllClick()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun HistoryRow(entry: CalculationHistory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(LegacyDimens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = LegacyColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.calculatorName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = LegacyColors.TextPrimary
                )
                Text(
                    text = Formatters.formatTimestamp(entry.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = LegacyColors.TextLight
                )
            }

            summarize(entry.outputSummary)?.let { summary ->
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LegacyColors.TextDark
                )
            }

            entry.inputValues.entries.take(MAX_VISIBLE_ROWS).forEach { (key, value) ->
                Text(
                    text = "${key.humanise()}: ${value.asDisplayString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LegacyColors.TextDescription
                )
            }
        }
    }
}

/** Picks the most useful headline number out of the stored result map. */
private fun summarize(outputSummary: Map<String, Any>?): String? {
    if (outputSummary.isNullOrEmpty()) return null
    val preferred = listOf("totalPayable", "maturityAmount", "totalReturns", "totalAmount", "emi")
    val key = preferred.firstOrNull { outputSummary.containsKey(it) }
        ?: outputSummary.keys.firstOrNull()
        ?: return null
    return "${key.humanise()}: ${outputSummary[key].asDisplayString()}"
}

private fun String.humanise(): String =
    replace(Regex("([a-z])([A-Z])"), "$1 $2").replaceFirstChar { it.uppercase() }

private fun Any?.asDisplayString(): String = when (this) {
    null -> "—"
    is Double -> if (isFinite()) Formatters.formatNumber(this, 2) else "—"
    is Float -> toDouble().asDisplayString()
    is Number -> toString()
    else -> toString()
}

@Composable
private fun EmptyHistoryView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No calculations yet",
            style = MaterialTheme.typography.titleMedium,
            color = LegacyColors.TextDark
        )
        Text(
            text = "Run a calculator and your results will show up here.",
            style = MaterialTheme.typography.bodyMedium,
            color = LegacyColors.TextDescription
        )
    }
}