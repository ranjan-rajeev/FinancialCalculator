package com.horizonlabs.financialcalculator.core.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorTopBar
import com.horizonlabs.financialcalculator.core.presentation.component.LegacyDimens
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors

/** Feature groups, mirroring the retired `AboutUsEntity.getListAboutUsEntity()` list. */
private val ABOUT_GROUPS = listOf(
    "EMI Calculators" to listOf("EMI Calculator (Loan Calculator)", "Compare Loan", "Flat vs Reducing rate"),
    "Loan Profile" to listOf("Create Loan Profile", "View Loan Profile", "Home Loan Eligibility"),
    "Mutual Funds & SIP Calculators" to listOf(
        "Systematic Investment Plan Calculator (SIP)",
        "Goal SIP Calculator",
        "Lumpsum SIP Calculator"
    ),
    "Bank Calculators" to listOf(
        "Fixed Deposit Calculator (TDR - Interest Payout)",
        "Recurring Deposit Calculator (RD)",
        "PPF Calculator (Public Provident Fund)"
    ),
    "GST Calculator" to listOf("GST Calculator (Add)", "GST Calculator (Remove)", "VAT Calculator")
)

@Composable
fun AboutScreen(
    appVersion: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CalculatorTopBar(
            title = "About Us",
            onBackClick = onBackClick,
            showBackArrow = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(LegacyDimens.CardMargin),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Financial Calculator",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LegacyColors.TextPrimary
            )
            Text(
                text = "Version $appVersion",
                style = MaterialTheme.typography.bodyMedium,
                color = LegacyColors.TextDescription
            )

            ABOUT_GROUPS.forEach { (group, features) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(LegacyDimens.CardRadius),
                    colors = CardDefaults.cardColors(containerColor = LegacyColors.CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = group.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = LegacyColors.Primary
                        )
                        features.forEach { feature ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LegacyColors.TextLight
                                )
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LegacyColors.TextDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}