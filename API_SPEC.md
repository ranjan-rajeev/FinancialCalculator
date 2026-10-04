# Financial Calculator - API Specification (v2)


## Overview
Static JSON files hosted on GitHub Pages. Versioned endpoints for backward compatibility.

## Base URL
```
https://<username>.github.io/FinancialCalculator-Web/json/v2/
```

---

## Endpoints

### 1. Dashboard Configuration
**GET** `/dashboard.json`

**Response:**
```json
{
  "version": "2024.01.15",
  "config": {
    "bannerPlacementId": "banner_123",
    "showAds": true,
    "inAppReviewEnabled": true,
    "inAppReviewCooldownDays": 3,
    "playStoreVersion": 15
  },
  "components": [
    {
      "type": "BANNER_CAROUSEL",
      "id": "banner_1",
      "position": 1,
      "data": {
        "images": [
          { "id": "1", "imageUrl": "https://...", "actionUrl": "https://...", "actionType": "WEB" }
        ],
        "autoPlay": true,
        "intervalMs": 5000
      }
    },
    {
      "type": "SECTION_HEADER",
      "id": "header_emi",
      "position": 100,
      "data": { "title": "EMI Calculators", "iconUrl": "https://..." }
    },
    {
      "type": "CALCULATOR_GRID",
      "id": "grid_emi",
      "position": 200,
      "data": {
        "calculators": [
          { "id": "emi", "name": "EMI Calculator", "iconUrl": "", "calculatorType": "EMI", "version": 1 },
          { "id": "compare_loan", "name": "Compare Loan", "iconUrl": "", "calculatorType": "COMPARE_LOAN", "version": 1 },
          { "id": "flat_vs_reducing", "name": "Flat vs Reducing", "iconUrl": "", "calculatorType": "FLAT_VS_REDUCING", "version": 1 }
        ],
        "columns": 2
      }
    },
    {
      "type": "SPACER",
      "id": "spacer_1",
      "position": 300,
      "data": { "heightDp": 16 }
    }
  ]
}
```

**Component Types:**
| Type | Description | Data Fields |
|------|-------------|-------------|
| `BANNER_CAROUSEL` | Scrollable image banner | `images[]`, `autoPlay`, `intervalMs` |
| `SECTION_HEADER` | Category title | `title`, `iconUrl` |
| `CALCULATOR_GRID` | Grid of calculator cards | `calculators[]`, `columns` |
| `CALCULATOR_LIST` | Vertical list of calculators | `calculators[]` |
| `SPACER` | Vertical spacing | `heightDp` |
| `WEB_VIEW` | Embedded web content | `url`, `title` |
| `AD_PLACEHOLDER` | Ad slot | `placementId`, `heightDp` |

**Calculator Card:**
```json
{
  "id": "emi",
  "name": "EMI Calculator",
  "iconUrl": "https://...",
  "calculatorType": "EMI",
  "version": 1
}
```

---

### 2. Calculator Configuration
**GET** `/calculators/{calculatorId}.json`

**Path Parameter:** `calculatorId` - e.g., `emi`, `sip`, `fd`, `nps`, `compare_loan`

**Response (Custom Calculator - EMI):**
```json
{
  "id": "emi",
  "name": "EMI Calculator",
  "type": "CUSTOM",
  "version": 2,
  "category": "LOAN",
  "engine": "EMI_CALCULATOR",
  "inputFields": [
    {
      "key": "principal",
      "label": "Loan Amount",
      "hint": "Enter loan amount",
      "inputType": "DECIMAL",
      "validation": { "required": true, "min": 1000, "max": 100000000 },
      "defaultValue": "",
      "formatter": "CURRENCY_INR",
      "order": 1
    },
    {
      "key": "rate",
      "label": "Interest Rate (% p.a.)",
      "hint": "Enter rate",
      "inputType": "DECIMAL",
      "validation": { "required": true, "min": 0.1, "max": 50 },
      "defaultValue": "",
      "formatter": "PERCENTAGE",
      "order": 2
    },
    {
      "key": "tenure",
      "label": "Tenure",
      "hint": "Enter tenure",
      "inputType": "NUMBER",
      "validation": { "required": true, "min": 1, "max": 360 },
      "defaultValue": "",
      "formatter": "NONE",
      "order": 3
    },
    {
      "key": "tenureType",
      "label": "Tenure Type",
      "options": [
        { "label": "Years", "value": "YEARS" },
        { "label": "Months", "value": "MONTHS" }
      ],
      "defaultValue": "YEARS",
      "order": 4
    },
    {
      "key": "startDate",
      "label": "First Installment Date",
      "defaultValue": "TODAY",
      "order": 5
    },
    {
      "key": "calculate",
      "label": "Calculate",
      "action": "CALCULATE",
      "style": "PRIMARY",
      "order": 6
    }
  ],
  "outputConfig": {
    "summary": [
      { "key": "emi", "label": "Monthly EMI", "type": "KEY_VALUE", "formatter": "CURRENCY_INR", "order": 1 },
      { "key": "totalInterest", "label": "Total Interest", "type": "KEY_VALUE", "formatter": "CURRENCY_INR", "order": 2 },
      { "key": "totalPayable", "label": "Total Payable", "type": "KEY_VALUE", "formatter": "CURRENCY_INR", "order": 3 },
      { "key": "interestPercentage", "label": "Interest %", "type": "KEY_VALUE", "formatter": "PERCENTAGE", "order": 4 },
      { "key": "principalPercentage", "label": "Principal %", "type": "KEY_VALUE", "formatter": "PERCENTAGE", "order": 5 }
    ],
    "breakdown": {
      "enabled": true,
      "groupBy": "YEAR",
      "columns": ["period", "principal", "interest", "total", "balance"],
      "expandable": true
    },
    "charts": [
      { "type": "PIE", "dataKeys": ["principal", "interest"], "title": "Payment Breakdown" }
    ]
  },
  "moreInfo": [
    { "question": "What is EMI?", "answer": "EMI stands for Equated Monthly Installment..." },
    { "question": "How is EMI calculated?", "answer": "EMI = P × r × (1+r)^n / ((1+r)^n - 1)..." }
  ]
}
```

**Response (Generic Calculator - NPS):**
```json
{
  "id": "nps",
  "name": "National Pension System",
  "type": "GENERIC",
  "version": 1,
  "category": "RETIREMENT",
  "engine": "GENERIC_FORMULA",
  "inputFields": [
    {
      "key": "age",
      "label": "Current Age",
      "inputType": "NUMBER",
      "validation": { "required": true, "min": 18, "max": 70 },
      "defaultValue": "",
      "formatter": "NONE",
      "order": 1
    },
    {
      "key": "retirementAge",
      "label": "Retirement Age",
      "inputType": "NUMBER",
      "validation": { "required": true, "min": 50, "max": 75 },
      "defaultValue": "",
      "formatter": "NONE",
      "order": 2
    },
    {
      "key": "monthlyContribution",
      "label": "Monthly Contribution",
      "inputType": "DECIMAL",
      "validation": { "required": true, "min": 500 },
      "defaultValue": "",
      "formatter": "CURRENCY_INR",
      "order": 3
    },
    {
      "key": "expectedReturn",
      "label": "Expected Return (% p.a.)",
      "inputType": "DECIMAL",
      "validation": { "required": true, "min": 1, "max": 20 },
      "defaultValue": "",
      "formatter": "PERCENTAGE",
      "order": 4
    },
    {
      "key": "annuityPercentage",
      "label": "Annuity Purchase %",
      "options": [
        { "label": "40%", "value": "40" },
        { "label": "50%", "value": "50" },
        { "label": "60%", "value": "60" }
      ],
      "defaultValue": "40",
      "order": 5
    },
    {
      "key": "calculate",
      "label": "Calculate",
      "action": "CALCULATE",
      "style": "PRIMARY",
      "order": 6
    }
  ],
  "outputConfig": {
    "summary": [
      { "key": "totalCorpus", "label": "Total Corpus", "type": "KEY_VALUE", "formatter": "CURRENCY_INR", "order": 1 },
      { "key": "maturityValue", "label": "Maturity Value", "type": "KEY_VALUE", "formatter": "CURRENCY_INR", "order": 2 },
      { "key": "pensionAmount", "label": "Monthly Pension", "type": "KEY_VALUE", "formatter": "CURRENCY_INR", "order": 3 },
      { "key": "lumpSum", "label": "Lump Sum Withdrawal", "type": "KEY_VALUE", "formatter": "CURRENCY_INR", "order": 4 }
    ],
    "breakdown": {
      "enabled": true,
      "groupBy": "YEAR",
      "columns": ["year", "contribution", "return", "total"],
      "expandable": false
    },
    "formulas": [
      { "key": "totalCorpus", "label": "Corpus Formula", "expression": "monthlyContribution * ((1 + expectedReturn/1200)^(months) - 1) / (expectedReturn/1200)", "variables": ["monthlyContribution", "expectedReturn", "months"] }
    ]
  },
  "moreInfo": [
    { "question": "What is NPS?", "answer": "National Pension System is a government-sponsored pension scheme..." },
    { "question": "Tax benefits?", "answer": "Up to ₹1.5L under 80CCD(1) + ₹50k under 80CCD(1B)..." }
  ],
  "formulaMap": {
    "totalCorpus": "monthlyContribution * ((1 + expectedReturn/1200)^(months) - 1) / (expectedReturn/1200)",
    "maturityValue": "totalCorpus",
    "lumpSum": "totalCorpus * (100 - annuityPercentage) / 100",
    "annuityAmount": "totalCorpus * annuityPercentage / 100",
    "pensionAmount": "annuityAmount * annuityRate / 12"
  }
}
```

---

### 3. App Config (Optional - can be in dashboard.json)
**GET** `/config.json`

**Response:**
```json
{
  "bannerPlacementId": "banner_123",
  "showAds": true,
  "inAppReviewEnabled": true,
  "inAppReviewCooldownDays": 3,
  "playStoreVersion": 15,
  "forceUpdateVersion": 10,
  "maintenanceMode": false,
  "maintenanceMessage": "App under maintenance. Please try again later."
}
```

---

### Polymorphic discriminators

`components[]` and `inputFields[]` are polymorphic. The `type` key is the
discriminator and must be one of:

| Field        | Allowed values                                                          |
|--------------|-------------------------------------------------------------------------|
| `components` | `BANNER_CAROUSEL`, `SECTION_HEADER`, `CALCULATOR_GRID`, `CALCULATOR_LIST`, `SPACER`, `WEB_VIEW`, `AD_PLACEHOLDER` |
| `inputFields`| `EDIT_TEXT`, `SPINNER`, `DATE_PICKER`, `BUTTON`, `SPACER`, `INFO_TEXT`   |

Because `type` is reserved as the discriminator, input field objects must not
repeat it as a regular property. Deserialisation uses kotlinx.serialization with
`ignoreUnknownKeys = true` and `classDiscriminator = "type"`.

## Data Models (Kotlin)

### Dashboard
```kotlin
data class DashboardResponse(
    @SerialName("version") val version: String,
    @SerialName("config") val config: AppConfig,
    @SerialName("components") val components: List<DashboardComponent>
)

data class AppConfig(
    @SerialName("bannerPlacementId") val bannerPlacementId: String?,
    @SerialName("showAds") val showAds: Boolean,
    @SerialName("inAppReviewEnabled") val inAppReviewEnabled: Boolean,
    @SerialName("inAppReviewCooldownDays") val inAppReviewCooldownDays: Int,
    @SerialName("playStoreVersion") val playStoreVersion: Int
)

sealed interface DashboardComponent {
    @Serializable
    data class BannerCarousel(
        @SerialName("id") val id: String,
        @SerialName("position") val position: Int,
        @SerialName("data") val data: BannerData
    ) : DashboardComponent

    @Serializable
    data class SectionHeader(
        @SerialName("id") val id: String,
        @SerialName("position") val position: Int,
        @SerialName("data") val data: HeaderData
    ) : DashboardComponent

    @Serializable
    data class CalculatorGrid(
        @SerialName("id") val id: String,
        @SerialName("position") val position: Int,
        @SerialName("data") val data: CalculatorGridData
    ) : DashboardComponent

    @Serializable
    data class CalculatorList(
        @SerialName("id") val id: String,
        @SerialName("position") val position: Int,
        @SerialName("data") val data: CalculatorListData
    ) : DashboardComponent

    @Serializable
    data class Spacer(
        @SerialName("id") val id: String,
        @SerialName("position") val position: Int,
        @SerialName("data") val data: SpacerData
    ) : DashboardComponent

    @Serializable
    data class WebView(
        @SerialName("id") val id: String,
        @SerialName("position") val position: Int,
        @SerialName("data") val data: WebViewData
    ) : DashboardComponent

    @Serializable
    data class AdPlaceholder(
        @SerialName("id") val id: String,
        @SerialName("position") val position: Int,
        @SerialName("data") val data: AdData
    ) : DashboardComponent
}

data class BannerData(
    @SerialName("images") val images: List<BannerImage>,
    @SerialName("autoPlay") val autoPlay: Boolean,
    @SerialName("intervalMs") val intervalMs: Long
)

data class BannerImage(
    @SerialName("id") val id: String,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("actionUrl") val actionUrl: String?,
    @SerialName("actionType") val actionType: ActionType
)

enum class ActionType { WEB, DEEP_LINK, NONE }

data class HeaderData(
    @SerialName("title") val title: String,
    @SerialName("iconUrl") val iconUrl: String?
)

data class CalculatorGridData(
    @SerialName("calculators") val calculators: List<CalculatorCard>,
    @SerialName("columns") val columns: Int
)

data class CalculatorListData(
    @SerialName("calculators") val calculators: List<CalculatorCard>
)

data class CalculatorCard(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("iconUrl") val iconUrl: String,
    @SerialName("calculatorType") val calculatorType: String,
    @SerialName("version") val version: Int
)

data class SpacerData(
    @SerialName("heightDp") val heightDp: Int
)

data class WebViewData(
    @SerialName("url") val url: String,
    @SerialName("title") val title: String
)

data class AdData(
    @SerialName("placementId") val placementId: String,
    @SerialName("heightDp") val heightDp: Int
)
```

### Calculator Config
```kotlin
data class CalculatorConfig(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("type") val type: CalculatorType,
    @SerialName("version") val version: Int,
    @SerialName("category") val category: String,
    @SerialName("engine") val engine: String,
    @SerialName("inputFields") val inputFields: List<InputFieldConfig>,
    @SerialName("outputConfig") val outputConfig: OutputConfig,
    @SerialName("moreInfo") val moreInfo: List<MoreInfoItem>,
    @SerialName("formulaMap") val formulaMap: Map<String, String>? = null
)

enum class CalculatorType { CUSTOM, GENERIC }

sealed interface InputFieldConfig {
    @Serializable
    data class EditText(
        @SerialName("key") val key: String,
        @SerialName("type") val type: String = "EDIT_TEXT",
        @SerialName("label") val label: String,
        @SerialName("hint") val hint: String?,
        @SerialName("inputType") val inputType: InputType,
        @SerialName("validation") val validation: ValidationRule?,
        @SerialName("defaultValue") val defaultValue: String?,
        @SerialName("formatter") val formatter: FormatterType,
        @SerialName("order") val order: Int,
        @SerialName("visibleWhen") val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig

    @Serializable
    data class Spinner(
        @SerialName("key") val key: String,
        @SerialName("type") val type: String = "SPINNER",
        @SerialName("label") val label: String,
        @SerialName("options") val options: List<SpinnerOption>,
        @SerialName("defaultValue") val defaultValue: String?,
        @SerialName("order") val order: Int,
        @SerialName("visibleWhen") val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig

    @Serializable
    data class DatePicker(
        @SerialName("key") val key: String,
        @SerialName("type") val type: String = "DATE_PICKER",
        @SerialName("label") val label: String,
        @SerialName("defaultValue") val defaultValue: String,
        @SerialName("order") val order: Int,
        @SerialName("visibleWhen") val visibleWhen: VisibilityCondition? = null
    ) : InputFieldConfig

    @Serializable
    data class Button(
        @SerialName("key") val key: String,
        @SerialName("type") val type: String = "BUTTON",
        @SerialName("label") val label: String,
        @SerialName("action") val action: ButtonAction,
        @SerialName("style") val style: ButtonStyle,
        @SerialName("order") val order: Int
    ) : InputFieldConfig
}

enum class InputType { NUMBER, DECIMAL, TEXT, PHONE, EMAIL }

data class ValidationRule(
    @SerialName("required") val required: Boolean,
    @SerialName("min") val min: Double?,
    @SerialName("max") val max: Double?,
    @SerialName("regex") val regex: String?
)

data class SpinnerOption(
    @SerialName("label") val label: String,
    @SerialName("value") val value: String
)

enum class FormatterType { NONE, CURRENCY_INR, PERCENTAGE, COMPACT_NUMBER }

enum class ButtonAction { CALCULATE, CLEAR, NAVIGATE }

enum class ButtonStyle { PRIMARY, SECONDARY, OUTLINE }

data class VisibilityCondition(
    @SerialName("field") val field: String,
    @SerialName("operator") val operator: VisibilityOperator,
    @SerialName("value") val value: String
)

enum class VisibilityOperator { EQ, NE, GT, LT, GTE, LTE, CONTAINS }

data class OutputConfig(
    @SerialName("summary") val summary: List<SummaryItemConfig>,
    @SerialName("breakdown") val breakdown: BreakdownConfig?,
    @SerialName("charts") val charts: List<ChartConfig>,
    @SerialName("formulas") val formulas: List<FormulaConfig>
)

data class SummaryItemConfig(
    @SerialName("key") val key: String,
    @SerialName("label") val label: String,
    @SerialName("type") val type: SummaryType,
    @SerialName("formatter") val formatter: FormatterType,
    @SerialName("order") val order: Int
)

enum class SummaryType { KEY_VALUE, FORMULA, EXPRESSION_WITH_FORMULA, GRAPH, DIVIDER }

data class BreakdownConfig(
    @SerialName("enabled") val enabled: Boolean,
    @SerialName("groupBy") val groupBy: GroupByType,
    @SerialName("columns") val columns: List<String>,
    @SerialName("expandable") val expandable: Boolean
)

enum class GroupByType { YEAR, MONTH, QUARTER, NONE }

data class ChartConfig(
    @SerialName("type") val type: ChartType,
    @SerialName("dataKeys") val dataKeys: List<String>,
    @SerialName("title") val title: String
)

enum class ChartType { PIE, BAR, LINE, DONUT }

data class FormulaConfig(
    @SerialName("key") val key: String,
    @SerialName("label") val label: String,
    @SerialName("expression") val expression: String,
    @SerialName("variables") val variables: List<String>
)

data class MoreInfoItem(
    @SerialName("question") val question: String,
    @SerialName("answer") val answer: String
)
```

---

## Versioning Strategy

### URL Structure
```
https://<username>.github.io/FinancialCalculator-Web/json/
├── v1/                    # Legacy (current structure)
│   ├── dashboard.json
│   ├── nps/npscalculator.json
│   ├── nps/npsmoreinfo.json
│   └── ...
├── v2/                    # New optimized structure
│   ├── dashboard.json
│   ├── calculators/
│   │   ├── emi.json
│   │   ├── sip.json
│   │   ├── fd.json
│   │   ├── rd.json
│   │   ├── ppf.json
│   │   ├── gst.json
│   │   ├── vat.json
│   │   ├── compare_loan.json
│   │   ├── flat_vs_reducing.json
│   │   ├── nps.json
│   │   ├── atal.json
│   │   └── cagr.json
│   └── config.json
└── latest/                # Symlink to current version (v2)
    ├── dashboard.json
    └── calculators/
```

### Client Version Selection
```kotlin
// In ConfigRepository
const val API_VERSION = "v2"  // or "latest"
const val BASE_URL = "https://<username>.github.io/FinancialCalculator-Web/json/$API_VERSION/"
```

---

## Migration Checklist

### Calculator Configs to Create (v2)

| Calculator ID | Type | Engine | Source Files |
|---------------|------|--------|--------------|
| `emi` | CUSTOM | EMI_CALCULATOR | EmiCalculatorActivity.java |
| `compare_loan` | CUSTOM | COMPARE_LOAN | EmiCompareActivity.java |
| `flat_vs_reducing` | CUSTOM | FLAT_VS_REDUCING | FixedVsReducingActivity.java |
| `sip` | CUSTOM | SIP_CALCULATOR | SIPCalculatorActivity.java |
| `goal_sip` | CUSTOM | SIP_CALCULATOR | SIPGoalCalculatorActivity.java |
| `lumpsum_sip` | CUSTOM | SIP_CALCULATOR | LumpSumpSipActivity.java |
| `fd` | CUSTOM | FD_CALCULATOR | FDCalculatorActivity.java |
| `rd` | CUSTOM | RD_CALCULATOR | RDCalculatorActivity.java |
| `ppf` | CUSTOM | PPF_CALCULATOR | PPFCalculatotActivity.java |
| `gst` | CUSTOM | GST_CALCULATOR | GstCalculatorActivity.java |
| `vat` | CUSTOM | VAT_CALCULATOR | VatCalculatorActivity.java |
| `nps` | GENERIC | GENERIC_FORMULA | npscalculator.json + npsmoreinfo.json |
| `atal` | GENERIC | GENERIC_FORMULA | atalcalculator.json + atalmoreinfo.json |
| `cagr` | GENERIC | GENERIC_FORMULA | cagrcalculator.json + cagrmoreinfo.json |

### Dashboard Components to Map

| Current cmpType | New Component Type |
|-----------------|-------------------|
| 1 (carousel) | BANNER_CAROUSEL |
| 2 (webview) | WEB_VIEW |
| 3 (ad) | AD_PLACEHOLDER |
| 4 (header) | SECTION_HEADER |
| 5 (calculator grid) | CALCULATOR_GRID |
| 6 (spacer) | SPACER |

---

## Backward Compatibility (v1)

Keep existing v1 endpoints unchanged:
- `/v1/dashboard.json` - Current format
- `/v1/nps/npscalculator.json` - Current CalculatorEntity format
- `/v1/nps/npsmoreinfo.json` - Current MoreInfoEntity format
- etc.

App can switch via feature flag:
```kotlin
// In AppConfig from dashboard.json
val useV2Api: Boolean = true  // or from remote config
```

---

## File List for GitHub Pages

```
FinancialCalculator-Web/
└── json/
    ├── v1/                    # Existing files (unchanged)
    │   ├── dashboard.json
    │   ├── config.json
    │   ├── nps/
    │   │   ├── npscalculator.json
    │   │   └── npsmoreinfo.json
    │   ├── atal/
    │   │   ├── atalcalculator.json
    │   │   └── atalmoreinfo.json
    │   ├── cagr/
    │   │   ├── cagrcalculator.json
    │   │   └── cagrmoreinfo.json
    │   └── ...
    ├── v2/                    # NEW optimized structure
    │   ├── dashboard.json
    │   ├── config.json
    │   └── calculators/
    │       ├── emi.json
    │       ├── compare_loan.json
    │       ├── flat_vs_reducing.json
    │       ├── sip.json
    │       ├── goal_sip.json
    │       ├── lumpsum_sip.json
    │       ├── fd.json
    │       ├── rd.json
    │       ├── ppf.json
    │       ├── gst.json
    │       ├── vat.json
    │       ├── nps.json
    │       ├── atal.json
    │       └── cagr.json
    └── latest -> v2/          # Symlink (GitHub Pages doesn't support, use redirect or copy)
```

---

## Testing Endpoints

```bash
# Test v2 dashboard
curl https://<username>.github.io/FinancialCalculator-Web/json/v2/dashboard.json

# Test calculator config
curl https://<username>.github.io/FinancialCalculator-Web/json/v2/calculators/emi.json
curl https://<username>.github.io/FinancialCalculator-Web/json/v2/calculators/nps.json

# Test config
curl https://<username>.github.io/FinancialCalculator-Web/json/v2/config.json
```