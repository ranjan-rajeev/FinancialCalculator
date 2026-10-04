package com.horizonlabs.financialcalculator.core.util

object Constants {
    const val CURRENCY_SYMBOL = "\u20B9"
    const val API_VERSION = "v2"
    const val LEGACY_API_VERSION = "v1"

    /** Version-agnostic root. Retrofit resolves `v2/...` and `v1/...` relative paths against this. */
    const val BASE_URL = "https://ranjan-rajeev.github.io/Finanace-Calculator-Web/json/"

    object CalculatorType {
        const val EMI = "EMI"
        const val COMPARE_LOAN = "COMPARE_LOAN"
        const val FLAT_VS_REDUCING = "FLAT_VS_REDUCING"
        const val SIP = "SIP"
        const val GOAL_SIP = "GOAL_SIP"
        const val LUMPSUM_SIP = "LUMPSUM_SIP"
        const val FD = "FD"
        const val RD = "RD"
        const val PPF = "PPF"
        const val GST = "GST"
        const val VAT = "VAT"
        const val NPS = "NPS"
        const val ATAL = "ATAL"
        const val CAGR = "CAGR"
        const val LOAN_PROFILE_CREATE = "LOAN_PROFILE_CREATE"
        const val LOAN_PROFILE_VIEW = "LOAN_PROFILE_VIEW"
        const val HOME_LOAN_ELIGIBILITY = "HOME_LOAN_ELIGIBILITY"
        const val HISTORY = "HISTORY"
    }

    object EngineType {
        const val EMI_CALCULATOR = "EMI_CALCULATOR"
        const val COMPARE_LOAN = "COMPARE_LOAN"
        const val FLAT_VS_REDUCING = "FLAT_VS_REDUCING"
        const val SIP_CALCULATOR = "SIP_CALCULATOR"
        const val FD_CALCULATOR = "FD_CALCULATOR"
        const val RD_CALCULATOR = "RD_CALCULATOR"
        const val PPF_CALCULATOR = "PPF_CALCULATOR"
        const val GST_CALCULATOR = "GST_CALCULATOR"
        const val VAT_CALCULATOR = "VAT_CALCULATOR"
        const val GENERIC_FORMULA = "GENERIC_FORMULA"
    }

    object InputType {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val SPINNER = "SPINNER"
        const val DATE_PICKER = "DATE_PICKER"
        const val BUTTON = "BUTTON"
        const val SPACER = "SPACER"
        const val INFO_TEXT = "INFO_TEXT"
    }

    object InputDataType {
        const val NUMBER = "NUMBER"
        const val DECIMAL = "DECIMAL"
        const val TEXT = "TEXT"
        const val PHONE = "PHONE"
        const val EMAIL = "EMAIL"
    }

    object FormatterType {
        const val NONE = "NONE"
        const val CURRENCY_INR = "CURRENCY_INR"
        const val PERCENTAGE = "PERCENTAGE"
        const val COMPACT_NUMBER = "COMPACT_NUMBER"
    }

    object ButtonAction {
        const val CALCULATE = "CALCULATE"
        const val CLEAR = "CLEAR"
        const val NAVIGATE = "NAVIGATE"
    }

    object ButtonStyle {
        const val PRIMARY = "PRIMARY"
        const val SECONDARY = "SECONDARY"
        const val OUTLINE = "OUTLINE"
    }

    object SummaryType {
        const val KEY_VALUE = "KEY_VALUE"
        const val FORMULA = "FORMULA"
        const val EXPRESSION_WITH_FORMULA = "EXPRESSION_WITH_FORMULA"
        const val GRAPH = "GRAPH"
        const val DIVIDER = "DIVIDER"
    }

    object GroupByType {
        const val YEAR = "YEAR"
        const val MONTH = "MONTH"
        const val QUARTER = "QUARTER"
        const val NONE = "NONE"
    }

    object ChartType {
        const val PIE = "PIE"
        const val BAR = "BAR"
        const val LINE = "LINE"
        const val DONUT = "DONUT"
    }

    object VisibilityOperator {
        const val EQ = "EQ"
        const val NE = "NE"
        const val GT = "GT"
        const val LT = "LT"
        const val GTE = "GTE"
        const val LTE = "LTE"
        const val CONTAINS = "CONTAINS"
    }

    object ActionType {
        const val WEB = "WEB"
        const val DEEP_LINK = "DEEP_LINK"
        const val NONE = "NONE"
    }

    object ComponentType {
        const val BANNER_CAROUSEL = "BANNER_CAROUSEL"
        const val SECTION_HEADER = "SECTION_HEADER"
        const val CALCULATOR_GRID = "CALCULATOR_GRID"
        const val CALCULATOR_LIST = "CALCULATOR_LIST"
        const val SPACER = "SPACER"
        const val WEB_VIEW = "WEB_VIEW"
        const val AD_PLACEHOLDER = "AD_PLACEHOLDER"
    }

    object PrefKeys {
        const val DASHBOARD_VERSION = "dashboard_version"
        const val DASHBOARD_DATA = "dashboard_data"
        const val CALCULATOR_CONFIG_PREFIX = "calculator_config_"
        const val CALCULATOR_VERSION_PREFIX = "calculator_version_"
        const val IN_APP_REVIEW_LAST_SHOWN = "in_app_review_last_shown"
        const val IN_APP_REVIEW_COUNT = "in_app_review_count"
    }

    object Database {
        const val NAME = "financial_calculator.db"
        const val VERSION = 1
    }
}