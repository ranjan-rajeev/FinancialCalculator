package com.horizonlabs.financialcalculator.core.presentation.component

import androidx.annotation.DrawableRes
import com.horizonlabs.financialcalculator.core.R

/**
 * Local calculator artwork copied from the pre-migration app so every calculator
 * keeps the icon it had before the rewrite.
 *
 * The hosted JSON `iconUrl` is used when present; these are the fallbacks, so a
 * missing or unreachable icon never leaves a blank tile. Ids mirror
 * `DashBoardFragment` in the legacy app.
 */
object CalculatorIcons {

    @DrawableRes
    fun fallbackFor(calculatorId: String): Int = when (calculatorId.uppercase()) {
        "EMI" -> R.drawable.emi_cal
        "COMPARE_LOAN" -> R.drawable.compare_loan_new
        "FLAT_VS_REDUCING" -> R.drawable.compare_icon
        "SIP", "SIP_CALCULATOR" -> R.drawable.sip_icons
        "GOAL_SIP" -> R.drawable.goal
        "LUMPSUM_SIP" -> R.drawable.lumpsum
        "FD", "FD_CALCULATOR" -> R.drawable.fd
        "PPF", "PPF_CALCULATOR" -> R.drawable.ppf
        "GST", "GST_CALCULATOR" -> R.drawable.gst
        "VAT", "VAT_CALCULATOR" -> R.drawable.vat
        "HOME_LOAN" -> R.drawable.home_loan
        "CREATE_LOAN_PROFILE" -> R.drawable.create_loan_
        "VIEW_LOAN_PROFILE" -> R.drawable.view_loan_
        // The legacy app reused the EMI glyph for RD, NPS, ATAL and CAGR had no
        // dedicated artwork either, so they share the generic fallback.
        else -> R.drawable.ic_calculator_fallback
    }
}
