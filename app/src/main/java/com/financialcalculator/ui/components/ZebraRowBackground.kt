package com.financialcalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.financialcalculator.ui.theme.LegacyColors

/**
 * Row striping for the amortisation schedule.
 *
 * The legacy schedule (`item_year_loan_details` / `item_loan_details`) alternates
 * its row fills, and the disclosure badge colour comes from
 * `Util.getFixedBackground(position)`, which cycles green/blue/red by
 * `position % 3`. [LegacyDescentBadge.atPosition] reproduces that cycle so a row
 * and its badge cannot drift apart.
 */
@Composable
fun ZebraRowBackground(
    position: Int,
    modifier: Modifier = Modifier,
    oddColor: Color = Color.Transparent,
    evenColor: Color = LegacyColors.GreyEA,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.background(if (position % 2 == 0) oddColor else evenColor),
    ) {
        content()
    }
}

/** The badge that goes with a schedule row, matching `Util.getFixedBackground`. */
fun badgeForScheduleRow(position: Int): LegacyDescentBadge =
    LegacyDescentBadge.atPosition(position)