package com.horizonlabs.financialcalculator.core.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.horizonlabs.financialcalculator.core.domain.model.ChartConfig
import com.horizonlabs.financialcalculator.core.domain.model.ChartType
import com.horizonlabs.financialcalculator.core.presentation.theme.ChartPalette
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors
import com.horizonlabs.financialcalculator.core.util.Formatters

/**
 * Charts declared in a calculator's `outputConfig.charts`.
 *
 * Values come from `CalculationResult.rawValues`, not from the summary rows:
 * those hold formatted text such as `₹5,41,387.88`, which cannot be plotted.
 * A chart naming a key the engine did not publish is skipped rather than drawn
 * as a zero slice, so a stale config degrades to "no chart" instead of a
 * misleading picture.
 */
@Composable
fun CalculatorChartSection(
    charts: List<ChartConfig>,
    rawValues: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    if (charts.isEmpty()) return

    // Resolved before the title is emitted: a chart whose data keys are absent, or whose total
    // is non-positive, has no drawable geometry. Rendering the heading anyway would leave a
    // bare "Charts" label with nothing under it.
    val renderable = charts.mapNotNull { chart ->
        val slices = chart.dataKeys.mapNotNull { key ->
            val value = rawValues[key] ?: return@mapNotNull null
            key to value
        }
        // A single slice fills the whole circle and reads as "100%", which tells
        // the user nothing, and a non-positive total has no meaningful geometry.
        if (slices.size < 2 || slices.sumOf { it.second } <= 0.0) return@mapNotNull null
        chart to slices
    }
    if (renderable.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Titled "Charts", not "Breakdown": the breakdown table below renders its own heading.
        LegacySectionTitle(title = "Charts", modifier = Modifier.padding(vertical = 8.dp))

        renderable.forEach { (chart, slices) ->
            ChartCard(chart = chart, slices = slices)
        }
    }
}

@Composable
private fun ChartCard(chart: ChartConfig, slices: List<Pair<String, Double>>) {
    val total = slices.sumOf { it.second }

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LegacyDimens.CardMargin),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(LegacyDimens.CardRadius),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = LegacyDimens.CardElevation
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = chart.title,
                style = MaterialTheme.typography.titleMedium,
                color = LegacyColors.PrimaryDark
            )

            when (chart.type) {
                ChartType.PIE -> PieOrDonut(slices = slices, total = total, donut = false)
                ChartType.DONUT -> PieOrDonut(slices = slices, total = total, donut = true)
                ChartType.BAR -> BarSeries(slices = slices, max = slices.maxOf { it.second })
                ChartType.LINE -> LineSeries(slices = slices, max = slices.maxOf { it.second })
            }

            ChartLegend(slices = slices, total = total)
        }
    }
}

@Composable
private fun PieOrDonut(slices: List<Pair<String, Double>>, total: Double, donut: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val stroke = if (donut) size.minDimension * 0.18f else size.minDimension * 0.55f
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(inset, inset)

            var startAngle = -90f
            slices.forEachIndexed { index, (_, value) ->
                val sweep = (value / total * 360.0).toFloat()
                drawArc(
                    color = ChartPalette[index % ChartPalette.size],
                    startAngle = startAngle,
                    // A hairline of overlap hides the seam antialiasing leaves
                    // between adjacent sectors.
                    sweepAngle = sweep + 0.5f,
                    useCenter = !donut,
                    topLeft = topLeft,
                    size = arcSize,
                    style = if (donut) Stroke(width = stroke) else androidx.compose.ui.graphics.drawscope.Fill
                )
                startAngle += sweep
            }
        }
    }
}

@Composable
private fun BarSeries(slices: List<Pair<String, Double>>, max: Double) {
    if (max <= 0.0) return
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slices.forEachIndexed { index, (key, value) ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = humanizeKey(key),
                    style = MaterialTheme.typography.bodySmall,
                    color = LegacyColors.TextDescription
                )
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                ) {
                    drawRoundRect(
                        color = LegacyColors.BreakdownMonthBackground,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2)
                    )
                    drawRoundRect(
                        color = ChartPalette[index % ChartPalette.size],
                        size = Size(size.width * (value / max).toFloat(), size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2)
                    )
                }
            }
        }
    }
}

@Composable
private fun LineSeries(slices: List<Pair<String, Double>>, max: Double) {
    if (max <= 0.0 || slices.size < 2) return
    val points = slices.mapIndexed { index, (_, value) ->
        val x = sizeFraction(index, slices.size)
        Offset(x, 1f - (value / max).toFloat())
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(vertical = 8.dp)
        ) {
            val step = size.width / (slices.size - 1)
            val scaled = points.map { Offset(it.x * step, it.y * size.height) }

            scaled.zipWithNext().forEachIndexed { index, (start, end) ->
                drawLine(
                    color = ChartPalette[index % ChartPalette.size],
                    start = start,
                    end = end,
                    strokeWidth = 6f
                )
            }
            scaled.forEachIndexed { index, point ->
                drawCircle(
                    color = ChartPalette[index % ChartPalette.size],
                    radius = 10f,
                    center = point
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            slices.forEach { (key, _) ->
                Text(
                    text = humanizeKey(key),
                    style = MaterialTheme.typography.bodySmall,
                    color = LegacyColors.TextDescription
                )
            }
        }
    }
}

private fun sizeFraction(index: Int, count: Int): Float =
    if (count <= 1) 0f else index / (count - 1f)

@Composable
private fun ChartLegend(slices: List<Pair<String, Double>>, total: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        slices.forEachIndexed { index, (key, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(ChartPalette[index % ChartPalette.size])
                    )
                    Text(
                        text = humanizeKey(key),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LegacyColors.TextPrimary
                    )
                }
                Text(
                    text = "${Formatters.formatCurrencyINR(value)}  (${Formatters.formatPercentage(value / total * 100)})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LegacyColors.TextDark
                )
            }
        }
    }
}

/** `totalInterest` -> `Total Interest`, so a chart legend needs no published labels. */
internal fun humanizeKey(key: String): String =
    key.replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
        .replace('_', ' ')
        .split(' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
