package com.horizonlabs.financialcalculator.core.presentation.navigation

import android.net.Uri

object Routes {
    const val DASHBOARD = "dashboard"
    const val EMI = "emi"
    const val COMPARE_LOAN = "compare_loan"
    const val FLAT_VS_REDUCING = "flat_vs_reducing"
    const val SIP = "sip"
    const val GOAL_SIP = "goal_sip"
    const val LUMPSUM_SIP = "lumpsum_sip"
    const val FD = "fd"
    const val RD = "rd"
    const val PPF = "ppf"
    const val GST = "gst"
    const val VAT = "vat"
    const val LOAN_PROFILE_CREATE = "loan_profile_create"
    const val LOAN_PROFILE_VIEW = "loan_profile_view"
    const val HOME_LOAN_ELIGIBILITY = "home_loan_eligibility"
    const val ABOUT = "about"
    const val HISTORY = "history"
    const val GENERIC_CALCULATOR =
        "calculator/{calculatorId}/{calculatorName}?restoreInputs={restoreInputs}"
    const val WEB_VIEW = "web_view/{url}/{title}"

    fun genericCalculator(
        calculatorId: String,
        calculatorName: String,
        restoreInputs: String? = null
    ): String {
        val base = "calculator/${calculatorId.encodeUrlSegment()}/${calculatorName.encodeUrlSegment()}"
        return if (restoreInputs.isNullOrBlank()) {
            base
        } else {
            "$base?restoreInputs=${restoreInputs.encodeUrlSegment()}"
        }
    }

    fun webView(url: String, title: String): String =
        "web_view/${url.encodeUrlSegment()}/${title.encodeUrlSegment()}"
}

/**
 * Navigation decodes path segments with [android.net.Uri.decode], which does not turn `+` back into
 * a space, so `URLEncoder` (which emits `+`) would hand screens a mangled title.
 */
private fun String.encodeUrlSegment(): String = Uri.encode(this)
