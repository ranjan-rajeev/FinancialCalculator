package com.horizonlabs.financialcalculator.core.presentation.navigation

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
    const val HISTORY = "history"
    const val GENERIC_CALCULATOR = "calculator/{calculatorId}/{calculatorName}"

    fun genericCalculator(calculatorId: String, calculatorName: String): String =
        "calculator/$calculatorId/${calculatorName.encodeUrlSegment()}"
}

private fun String.encodeUrlSegment(): String = java.net.URLEncoder.encode(this, "UTF-8")
