package com.horizonlabs.financialcalculator.core.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonlabs.financialcalculator.core.domain.model.BreakdownItem
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorSummary
import com.horizonlabs.financialcalculator.core.domain.model.MoreInfoItem
import com.horizonlabs.financialcalculator.core.domain.model.SummaryType
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors
import com.horizonlabs.financialcalculator.core.domain.model.FormatterType

@Composable
fun CalculatorSummarySection(
    summary: CalculatorSummary,
    modifier: Modifier = Modifier
) {
    if (summary.items.isEmpty()) return
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LegacySectionTitle(title = "Summary")
        summary.items.sortedBy { it.order }.forEach { item ->
            SummaryItemView(item = item)
        }
    }
}

@Composable
fun SummaryItemView(item: com.horizonlabs.financialcalculator.core.domain.model.SummaryItem) {
    when {
        item.type == SummaryType.DIVIDER ->
            HorizontalDivider(color = LegacyColors.Divider, thickness = 1.dp)

        // Formula rows carry the expression in the label, so there is nothing
        // large to show underneath.
        item.value.isBlank() -> Text(
            text = item.label,
            style = MaterialTheme.typography.bodySmall,
            color = LegacyColors.TextDescription,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        else -> LegacySummaryItem(title = item.label, value = item.value)
    }
}

@Composable
fun CalculatorBreakdownSection(
    title: String,
    items: List<BreakdownItem>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    
    // EMI/FD/RD expose year rows with monthly children, which the legacy app
    // showed as the expandable four-column table rather than a label/value list.
    if (isAmortizationBreakdown(items)) {
        Column(modifier = modifier.fillMaxWidth()) {
            LegacySectionTitle(
                title = title,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LegacyBreakdownTable(items = items)
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            LegacySectionTitle(
                title = title,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            items.forEach { item ->
                BreakdownItemView(item = item)
            }
        }
    }
}

@Composable
fun BreakdownItemView(item: BreakdownItem, indent: Int = 0) {
    val indentDp = (indent * 16).dp
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indentDp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(
                text = item.period,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            item.values["total"]?.let { total ->
                Text(
                    text = total,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        item.values.filter { it.key != "total" }.forEach { (key, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatKey(key),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        item.children.forEach { child ->
            BreakdownItemView(child, indent + 1)
        }
    }
}

@Composable
fun CalculatorMoreInfoSection(
    items: List<MoreInfoItem>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("More Information", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            
            items.forEach { item ->
                MoreInfoItemView(item = item)
            }
        }
    }
}

@Composable
fun MoreInfoItemView(item: MoreInfoItem) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Q: ${item.question}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "A: ${item.answer}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatKey(key: String): String {
    return key.split("_").joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
}