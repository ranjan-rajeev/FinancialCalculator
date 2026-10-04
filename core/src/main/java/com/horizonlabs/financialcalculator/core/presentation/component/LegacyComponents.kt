package com.horizonlabs.financialcalculator.core.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.horizonlabs.financialcalculator.core.R
import com.horizonlabs.financialcalculator.core.domain.model.BreakdownItem
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors

/**
 * Reusable building blocks that reproduce the pre-migration app's look.
 *
 * The metrics come from the legacy layouts and drawables:
 * `item_dashboard.xml` (50dp icon over a 10sp centred label),
 * `row_dashboard.xml` (8dp radius card at 8dp elevation),
 * `item_edittext.xml` (50dp field, 5dp radius, 1dp border),
 * `button_selector.xml` (15dp pill in `colorPrimary`),
 * `item_output_key_value.xml` (centred caption over a 25sp bold value).
 */
object LegacyDimens {
    val CardRadius = 8.dp
    val CardElevation = 8.dp
    val CardMargin = 10.dp
    val TileIconSize = 50.dp
    val InputRadius = 5.dp
    val InputMinHeight = 50.dp
    val ButtonRadius = 15.dp
}

/** Section caption, e.g. "EMI Calculators": 15sp bold in `colorPrimaryDark`. */
@Composable
fun LegacySectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = LegacyColors.PrimaryDark,
        modifier = modifier.padding(horizontal = LegacyDimens.CardMargin)
    )
}

/** White rounded container used to group a section's calculators. */
@Composable
fun LegacySectionCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LegacyDimens.CardMargin),
        shape = RoundedCornerShape(LegacyDimens.CardRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = LegacyDimens.CardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = LegacyColors.CardBackground
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.card_background),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )

            content()
        }
    }
}

/**
 * A single dashboard entry: local fallback artwork (or the hosted `iconUrl` when
 * supplied) above a centred caption.
 */
@Composable
fun CalculatorTile(
    name: String,
    @androidx.annotation.DrawableRes fallbackIcon: Int,
    modifier: Modifier = Modifier,
    iconUrl: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(LegacyDimens.CardRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = 5.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalculatorIcon(
            name = name,
            fallbackIcon = fallbackIcon,
            iconUrl = iconUrl,
            modifier = Modifier.size(LegacyDimens.TileIconSize)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = LegacyColors.TextLight,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

/** Remote icon when available, otherwise the bundled legacy artwork. */
@Composable
fun CalculatorIcon(
    name: String,
    @androidx.annotation.DrawableRes fallbackIcon: Int,
    modifier: Modifier = Modifier,
    iconUrl: String? = null
) {
    if (iconUrl.isNullOrBlank()) {
        Image(
            painter = painterResource(fallbackIcon),
            contentDescription = name,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        AsyncImage(
            model = iconUrl,
            contentDescription = name,
            placeholder = painterResource(fallbackIcon),
            error = painterResource(fallbackIcon),
            fallback = painterResource(fallbackIcon),
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Labelled text field matching `item_edittext.xml`: 50dp tall, 5dp corners, a
 * grey border that turns `colorPrimary` on focus, and the caption in indigo.
 * Required fields keep the legacy `Label*` convention.
 */
@Composable
fun LegacyTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    required: Boolean = false,
    error: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    Column(modifier = modifier.padding(horizontal = LegacyDimens.CardMargin)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = LegacyDimens.InputMinHeight),
            label = { Text(if (required) "$label*" else label) },
            placeholder = placeholder?.let { { Text(it, color = LegacyColors.TextSecondary) } },
            isError = error != null,
            singleLine = singleLine,
            shape = RoundedCornerShape(LegacyDimens.InputRadius),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LegacyColors.Primary,
                unfocusedBorderColor = LegacyColors.InputStroke,
                focusedLabelColor = LegacyColors.Primary,
                unfocusedLabelColor = LegacyColors.TextDescription,
                errorBorderColor = LegacyColors.Error,
                focusedTextColor = LegacyColors.TextPrimary,
                unfocusedTextColor = LegacyColors.TextPrimary,
                cursorColor = LegacyColors.Primary
            )
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = LegacyColors.Error,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

/** Full-width pill button from `button_selector.xml`. */
@Composable
fun LegacyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: Painter? = null
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .height(48.dp),
        shape = RoundedCornerShape(LegacyDimens.ButtonRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) LegacyColors.Primary else LegacyColors.InputStroke,
            disabledContainerColor = LegacyColors.InputStroke
        ),
        border = BorderStroke(1.dp, LegacyColors.PrimaryDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = LegacyColors.TextOnPrimary
            )
        }
    }
}

/**
 * Result row from `item_output_key_value.xml`: centred caption over a large bold
 * value in `colorPrimaryDark`.
 */
@Composable
fun LegacySummaryItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = LegacyColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            color = LegacyColors.PrimaryDark,
            textAlign = TextAlign.Center
        )
    }
}

/** Table row from `item_year_loan_details.xml`: equal columns on a `lightGrey` band. */
@Composable
fun LegacyBreakdownRow(
    cells: List<String>,
    modifier: Modifier = Modifier,
    background: Color = LegacyColors.BreakdownRowBackground
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        cells.forEachIndexed { index, cell ->
            Text(
                text = cell,
                style = MaterialTheme.typography.bodySmall,
                color = LegacyColors.TextDark,
                textAlign = if (index == 0) TextAlign.Start else TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Amortisation table matching `item_year_loan_details.xml` plus the nested
 * `item_loan_details.xml`: one row per year carrying Year/Principal/Interest/
 * Balance, alternating white and `lightGrey`, with the leading add/minus box
 * revealing that year's monthly rows on tap.
 */
@Composable
fun LegacyBreakdownTable(
    items: List<BreakdownItem>,
    modifier: Modifier = Modifier
) {
    val columns = remember(items) { amortizationColumns(items) }
    val expandedPeriods = remember { mutableStateMapOf<String, Boolean>() }

    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            val isExpanded = expandedPeriods[item.period] == true
            LegacyAmortizationRow(
                cells = listOf(item.period) + columns.map { item.values[it].orEmpty() },
                background = if (index % 2 == 0) {
                    LegacyColors.CardBackground
                } else {
                    LegacyColors.BreakdownRowBackground
                },
                expandable = item.children.isNotEmpty(),
                expanded = isExpanded,
                onClick = if (item.children.isNotEmpty()) {
                    { expandedPeriods[item.period] = !isExpanded }
                } else {
                    null
                }
            )

            if (isExpanded) {
                item.children.forEach { child ->
                    LegacyBreakdownRow(
                        cells = listOf(child.period) + columns.map { child.values[it].orEmpty() },
                        background = LegacyColors.BreakdownMonthBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun LegacyAmortizationRow(
    cells: List<String>,
    background: Color,
    expandable: Boolean,
    expanded: Boolean,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (expandable) {
                Image(
                    painter = painterResource(
                        if (expanded) R.drawable.vector_minus else R.drawable.ic_add_box_black_24dp
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 0.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = cells.first(),
                style = MaterialTheme.typography.bodySmall,
                color = LegacyColors.TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        cells.drop(1).forEach { cell ->
            Text(
                text = cell,
                style = MaterialTheme.typography.bodySmall,
                color = LegacyColors.TextDark,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Legacy years carry Year/Principal/Interest/Balance, with "total" only ever
 * used for the headline. Fall back to whatever keys a calculator does provide
 * so FD/RD/NPS breakdowns still render instead of empty columns.
 */
internal fun isAmortizationBreakdown(items: List<BreakdownItem>): Boolean =
    items.firstOrNull()?.values?.keys.orEmpty().any { key ->
        key.equals("principal", ignoreCase = true) ||
            key.equals("deposit", ignoreCase = true) ||
            key.equals("balance", ignoreCase = true)
    }

internal fun amortizationColumns(items: List<BreakdownItem>): List<String> {
    val keys = items.flatMap { item ->
        listOf(item.period) + item.values.keys + item.children.flatMap { it.values.keys }
    }.filter { it.isNotBlank() }

    val preferred = listOf("principal", "deposit", "interest", "balance")
        .filter { key -> keys.any { it.equals(key, ignoreCase = true) } }
    if (preferred.isNotEmpty()) return preferred

    return keys.filter { !it.equals("total", ignoreCase = true) }.distinct().take(3)
}

/** Vertical rhythm between stacked input fields. */
val LegacyFieldSpacing = 12.dp

/** Gap between calculator tiles inside a section card. */
val LegacyTileSpacing = Arrangement.spacedBy(4.dp)
