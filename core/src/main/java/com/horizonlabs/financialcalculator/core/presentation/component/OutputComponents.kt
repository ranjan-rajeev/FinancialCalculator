package com.horizonlabs.financialcalculator.core.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Summary",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            EnhancedSummaryView(summary = summary)
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
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (isAmortizationBreakdown(items)) {
                LegacyBreakdownTable(items = items)
            } else {
                items.forEach { item ->
                    BreakdownItemView(item = item)
                }
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
            Text("More Info", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            
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

@Composable
private fun EnhancedSummaryView(summary: CalculatorSummary) {
    val items = summary.items.sortedBy { it.order }
    val principalItem = items.find { it.key == "principalPercentage" || it.key == "principal_percent" || it.key.contains("principal", ignoreCase = true) && it.key.contains("percent") }
    val interestItem = items.find { it.key == "interestPercentage" || it.key == "interest_percent" || it.key.contains("interest", ignoreCase = true) && it.key.contains("percent") }
    val principalValue = items.find { it.key == "totalPrincipal" || it.key == "principal" && !it.key.contains("percent") }?.value ?: ""
    val interestValue = items.find { it.key == "totalInterest" || it.key == "interest" && !it.key.contains("percent") }?.value ?: ""

    val principalPercent = principalItem?.value?.replace("%", "")?.toFloatOrNull() ?: 0f
    val interestPercent = interestItem?.value?.replace("%", "")?.toFloatOrNull() ?: (100f - principalPercent)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressWithPercentage(
                    percentage = principalPercent,
                    valueText = principalValue.ifEmpty { principalItem?.value ?: "" },
                    title = principalItem?.label ?: "Total Principal",
                    color = LegacyColors.Primary
                )
                CircularProgressWithPercentage(
                    percentage = interestPercent,
                    valueText = interestValue.ifEmpty { interestItem?.value ?: "" },
                    title = interestItem?.label ?: "Total Interest",
                    color = LegacyColors.Accent
                )
            }
            items.forEach { item ->
                if (!(item.key.contains("percent", ignoreCase = true) && (item.key.contains("principal") || item.key.contains("interest")))) {
                    if (item.type != SummaryType.DIVIDER && item.value.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.label, style = MaterialTheme.typography.bodyMedium, color = LegacyColors.TextPrimary)
                            Text(item.value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = LegacyColors.PrimaryDark)
                        }
                    } else {
                        SummaryItemView(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun CircularProgressWithPercentage(
    percentage: Float,
    valueText: String,
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 12f,
    size: Dp = 100.dp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val stroke = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                val diameter = size.toPx()
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter - strokeWidth, diameter - strokeWidth)

                drawArc(
                    color = color.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = (percentage / 100f) * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
            }
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = LegacyColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = valueText,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}
