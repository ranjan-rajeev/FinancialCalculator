# Financial Calculator - Kotlin Compose Rewrite Plan (Simplified Modules)

## Project Overview
Rewrite the existing Java/XML Android app to **Kotlin Compose** with **MVVM + MVI** architecture, **minimal modules**, reusing existing business logic.

---

## Module Structure (4 Modules Only)

```
financial-calculator/
├── finance/                      # Entry point, DI, Navigation, MainActivity (was 'app')
├── core/                         # Shared code: data, domain, ui, utils
├── feature-calculators/          # ALL calculator screens + viewmodels + engines
└── feature-dashboard/            # Dashboard screen + dynamic components
```

### Why This Structure?
| Module | Purpose | Compulsory? |
|--------|---------|-------------|
| `finance` | Application class, Hilt setup, NavHost, MainActivity | Yes |
| `core` | Room DB, Repositories, Use Cases, MVI base, Theme, Common Compose components | Yes |
| `feature-calculators` | All calculator logic (EMI, SIP, FD, GST, Generic, History, Loan Profile) | Yes - separate feature from finance shell |
| `feature-dashboard` | Dynamic dashboard parsing/rendering | Yes - separate dynamic UI from calculators |

**API Specification:** See [`API_SPEC.md`](API_SPEC.md) for complete v2 endpoint definitions, response structures, and Kotlin data models.

**NOT creating:** core-common, core-data, core-domain, core-ui, feature-emi, feature-sip, feature-banking, feature-gst, feature-retirement, feature-loan-profile, feature-history

---

## Architecture: MVVM + MVI at UI Layer

### MVI Pattern
```
┌─────────────┐     Intent      ┌─────────────┐
│   UI        │ ──────────────▶ │  ViewModel  │
│ (Compose)   │                 │  (Reducer)  │
└─────────────┘                 └──────┬──────┘
       ▲                                │
       │ State                          │ Effect
       │                                ▼
┌─────────────┐                 ┌─────────────────┐
│   UI        │ ◀────────────── │  Repository/    │
│  Renders    │    State Flow   │  Use Cases      │
└─────────────┘                 └─────────────────┘
```

### Core MVI Classes (`core/presentation/mvi`)
```kotlin
// Base classes
sealed interface UiState<out T>
data class UiState.Success<T>(val data: T) : UiState<T>
data class UiState.Loading(val message: String?) : UiState<Nothing>
data class UiState.Error(val message: String, val throwable: Throwable?) : UiState<Nothing>

interface MviViewModel<in Intent, State> : ViewModel {
    val state: StateFlow<State>
    fun onIntent(intent: Intent)
}

// Calculator-specific
sealed interface CalculatorIntent {
    data class OnInputChanged(val key: String, val value: Any) : CalculatorIntent
    object OnCalculate : CalculatorIntent
    object OnClear : CalculatorIntent
    data class OnHistorySelected(val history: CalculationHistory) : CalculatorIntent
    object OnShowHistory : CalculatorIntent
}

data class CalculatorState(
    val inputFields: List<InputField> = emptyList(),
    val inputValues: Map<String, Any> = emptyMap(),
    val validationErrors: Map<String, String> = emptyMap(),
    val summary: CalculatorSummary? = null,
    val breakdown: List<BreakdownItem> = emptyList(),
    val moreInfo: List<MoreInfoItem> = emptyList(),
    val isLoading: Boolean = false,
    val showHistory: Boolean = false
)
```

---

## Phase 1: Foundation (Week 1-2)

### 1.1 Project Setup
- [ ] Create 4-module Gradle structure with `settings.gradle.kts`
- [ ] Configure version catalogs (`libs.versions.toml`)
- [ ] Configure Compose Compiler, Material3, Navigation, Hilt, Room, Coroutines/Flow

### 1.2 Core Module (`core`)
```
core/src/main/java/com/financialcalculator/core/
├── data/
│   ├── db/                    # Room: AppDatabase, Daos, Entities
│   ├── repository/            # CalculatorRepository, HistoryRepository, ConfigRepository
│   ├── remote/                # Retrofit service, API models
│   └── local/                 # SharedPrefs, Cache
├── domain/
│   ├── model/                 # CalculatorEntity, InputField, OutputField, BreakdownItem, MoreInfoItem, CalculationHistory
│   ├── usecase/               # CalculateUseCase, GetCalculatorConfigUseCase, SaveHistoryUseCase, GetHistoryUseCase, GetDashboardUseCase
│   └── engine/                # CalculatorEngine interface + implementations (EmiEngine, FdEngine, SipEngine, etc.)
├── presentation/
│   ├── mvi/                   # MviViewModel, UiState, Reducer
│   ├── theme/                 # Material3 Theme, Color, Typography, Shapes
│   ├── component/             # Common composables: InputField, OutputCard, BreakdownList, MoreInfoSection, Toolbar, Loading, Error
│   └── navigation/            # NavGraph, Routes, Extensions
└── util/                      # Extensions, Formatters, Constants, Result wrapper
```

### 1.3 Business Logic Extraction (Pure Kotlin - No Android Deps)
Extract from existing Java activities into `core/domain/engine/`:
- [ ] `EmiCalculatorEngine.kt` - EMI, total interest, amortization (yearly/monthly)
- [ ] `FdCalculatorEngine.kt` - FD maturity, quarterly/monthly/yearly breakdown
- [ ] `SipCalculatorEngine.kt` - SIP maturity with frequency
- [ ] `RdCalculatorEngine.kt`, `PpfCalculatorEngine.kt`
- [ ] `GstCalculatorEngine.kt`, `VatCalculatorEngine.kt`
- [ ] `CompareLoanEngine.kt`, `FlatVsReducingEngine.kt`
- [ ] `NpsCalculatorEngine.kt`, `AtalCalculatorEngine.kt`, `CagrCalculatorEngine.kt` (formula evaluator)
- [ ] `GenericFormulaEngine.kt` - Evaluates expressions like "a+b", "a*b/100" from JSON

---

## Phase 2: Common Calculator Framework (Week 2-3)

### 2.1 Dynamic Input System (`core/domain/model/InputField.kt`)
```kotlin
sealed interface InputField {
    data class EditText(
        val key: String, val label: String, val hint: String,
        val inputType: InputType, val validation: ValidationRule?, val defaultValue: String?
    ) : InputField
    data class Spinner(
        val key: String, val label: String, val options: List<SpinnerOption>
    ) : InputField
    data class EditTextWithSpinner(
        val key: String, val label: String, val hint: String,
        val spinnerOptions: List<SpinnerOption>
    ) : InputField
    data class DatePicker(val key: String, val label: String) : InputField
    data class Button(val key: String, val label: String, val action: ButtonAction) : InputField
    data class Spacer(val height: Dp) : InputField
    data class InfoText(val text: String) : InputField
}

enum class InputType { NUMBER, DECIMAL, TEXT, PHONE, EMAIL }
data class ValidationRule(val regex: String?, val min: Double?, val max: Double?, val required: Boolean)
data class SpinnerOption(val label: String, val value: String)
enum class ButtonAction { CALCULATE, CLEAR, NAVIGATE }
```

### 2.2 Dynamic Output System (`core/domain/model/OutputSection.kt`)
```kotlin
sealed interface OutputSection {
    data class Summary(
        val items: List<SummaryItem>
    ) : OutputSection
    data class Breakdown(
        val title: String,
        val items: List<BreakdownItem>
    ) : OutputSection
    data class MoreInfo(
        val items: List<MoreInfoItem>
    ) : OutputSection
}

data class SummaryItem(
    val type: SummaryType,
    val label: String,
    val value: String,
    val formula: String? = null,
    val expression: String? = null
)
enum class SummaryType { KEY_VALUE, FORMULA, EXPRESSION_WITH_FORMULA, GRAPH, DIVIDER }

data class BreakdownItem(
    val period: String,
    val principal: String,
    val interest: String,
    val total: String,
    val balance: String,
    val children: List<BreakdownItem> = emptyList()
)

data class MoreInfoItem(val question: String, val answer: String)
```

### 2.3 Common Calculator Screen (`core/presentation/component/CommonCalculatorScreen.kt`)
```kotlin
@Composable
fun CommonCalculatorScreen(
    viewModel: CommonCalculatorViewModel = hiltViewModel(),
    calculatorId: String,
    calculatorName: String
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = { CalculatorTopBar(title = calculatorName, onHistoryClick = { viewModel.onIntent(CalculatorIntent.OnShowHistory) }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Input Section
            if (state.inputFields.isNotEmpty()) {
                CalculatorInputSection(
                    fields = state.inputFields,
                    values = state.inputValues,
                    errors = state.validationErrors,
                    onInputChange = { key, value -> viewModel.onIntent(CalculatorIntent.OnInputChanged(key, value)) },
                    onCalculate = { viewModel.onIntent(CalculatorIntent.OnCalculate) }
                )
            }
            
            // Output Summary
            state.summary?.let { CalculatorSummarySection(summary = it) }
            
            // Breakdown Details
            state.breakdown.ifNotEmpty { CalculatorBreakdownSection(title = "Yearly Breakdown", items = it) }
            
            // More Info (Static Information)
            state.moreInfo.ifNotEmpty { CalculatorMoreInfoSection(items = it) }
        }
    }
}
```

### 2.4 Generic Calculator ViewModel (`feature-calculators/generic/CommonCalculatorViewModel.kt`)
```kotlin
@HiltViewModel
class CommonCalculatorViewModel @Inject constructor(
    private val getConfigUseCase: GetCalculatorConfigUseCase,
    private val calculateUseCase: CalculateUseCase,
    private val saveHistoryUseCase: SaveHistoryUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    @Assisted private val calculatorId: String
) : MviViewModel<CalculatorIntent, CalculatorState> {
    
    private val _state = MutableStateFlow(CalculatorState())
    override val state: StateFlow<CalculatorState> = _state
    
    init { loadCalculatorConfig() }
    
    private fun loadCalculatorConfig() {
        // Fetch from local/remote based on version
        // Parse input fields, output config, more info from CalculatorEntity
    }
    
    override fun onIntent(intent: CalculatorIntent) {
        when (intent) {
            is CalculatorIntent.OnInputChanged -> updateInput(intent.key, intent.value)
            is CalculatorIntent.OnCalculate -> calculate()
            is CalculatorIntent.OnClear -> clear()
            is CalculatorIntent.OnHistorySelected -> restoreHistory(intent.history)
            is CalculatorIntent.OnShowHistory -> _state.update { it.copy(showHistory = true) }
        }
    }
    
    private fun calculate() {
        val result = calculateUseCase.execute(calculatorId, _state.value.inputValues)
        _state.update { it.copy(
            summary = result.summary,
            breakdown = result.breakdown,
            validationErrors = result.errors
        )}
        if (result.errors.isEmpty()) saveHistoryUseCase.execute(...)
    }
}
```

---

## Phase 3: Feature Modules (Week 3-5)

### 3.1 Dashboard (`feature-dashboard`)
- **Dynamic JSON-driven UI** matching current `dashboard.json`
- Component types (from `cmpType`):
  - `1`: Banner/Carousel (scrollable images)
  - `2`: WebView
  - `3`: Ad placeholder
  - `4`: Section Header
  - `5`: Calculator Grid/List
  - `6`: Spacer
- `DashboardViewModel` fetches config, parses components, renders dynamically
- Navigate to calculator screens on item click

### 3.2 All Calculators (`feature-calculators`)

#### Custom Screens (Dedicated ViewModels + Compose UI)
| Calculator | Screen | Engine |
|------------|--------|--------|
| EMI Calculator | `EmiScreen.kt` + `EmiViewModel.kt` | `EmiCalculatorEngine` |
| Compare Loan | `CompareLoanScreen.kt` + `CompareLoanViewModel.kt` | `CompareLoanEngine` |
| Flat vs Reducing | `FlatVsReducingScreen.kt` + `FlatVsReducingViewModel.kt` | `FlatVsReducingEngine` |
| SIP Calculator | `SipScreen.kt` + `SipViewModel.kt` | `SipCalculatorEngine` |
| Goal SIP | `GoalSipScreen.kt` + `GoalSipViewModel.kt` | `SipCalculatorEngine` |
| Lumpsum SIP | `LumpsumSipScreen.kt` + `LumpsumSipViewModel.kt` | `SipCalculatorEngine` |
| FD Calculator | `FdScreen.kt` + `FdViewModel.kt` | `FdCalculatorEngine` |
| RD Calculator | `RdScreen.kt` + `RdViewModel.kt` | `RdCalculatorEngine` |
| PPF Calculator | `PpfScreen.kt` + `PpfViewModel.kt` | `PpfCalculatorEngine` |
| GST Calculator | `GstScreen.kt` + `GstViewModel.kt` | `GstCalculatorEngine` |
| VAT Calculator | `VatScreen.kt` + `VatViewModel.kt` | `VatCalculatorEngine` |
| Loan Profile | `LoanProfileScreen.kt` + `LoanProfileViewModel.kt` | - |
| Home Loan Eligibility | `HomeLoanEligibilityScreen.kt` | - |
| History | `HistoryScreen.kt` + `HistoryViewModel.kt` | - |

#### Generic Screens (Use Common Framework)
| Calculator | Approach |
|------------|----------|
| NPS, ATAL, CAGR | `CommonCalculatorScreen` + `CommonCalculatorViewModel` (config from JSON) |
| Future generic calculators | Same - zero code, just JSON config |

**ViewModel Pattern for Custom Calculators:**
```kotlin
@HiltViewModel
class EmiViewModel @Inject constructor(
    private val engine: EmiCalculatorEngine,
    private val saveHistoryUseCase: SaveHistoryUseCase,
    private val getHistoryUseCase: GetHistoryUseCase
) : MviViewModel<EmiIntent, EmiState> {
    // EmiIntent: OnPrincipalChanged, OnRateChanged, OnTenureChanged, OnTenureTypeChanged, OnCalculate, OnShowHistory
    // EmiState: principal, rate, tenure, tenureType, emi, totalInterest, totalPayable, breakdown, showHistory
}
```

---

## Phase 4: Integration & Polish (Week 5-6)

### 4.1 Navigation (`core/presentation/navigation/NavGraph.kt`)
```kotlin
@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = DashboardRoute) {
    NavHost(navController, startDestination) {
        composable(DashboardRoute) { DashboardScreen() }
        
        // Custom calculators
        composable(EmiRoute) { EmiScreen() }
        composable(CompareLoanRoute) { CompareLoanScreen() }
        composable(FlatVsReducingRoute) { FlatVsReducingScreen() }
        composable(SipRoute) { SipScreen() }
        composable(GoalSipRoute) { GoalSipScreen() }
        composable(LumpsumSipRoute) { LumpsumSipScreen() }
        composable(FdRoute) { FdScreen() }
        composable(RdRoute) { RdScreen() }
        composable(PpfRoute) { PpfScreen() }
        composable(GstRoute) { GstScreen() }
        composable(VatRoute) { VatScreen() }
        composable(LoanProfileRoute) { LoanProfileScreen() }
        composable(HomeLoanEligibilityRoute) { HomeLoanEligibilityScreen() }
        composable(HistoryRoute) { HistoryScreen() }
        
        // Generic calculators (NPS, ATAL, CAGR, future)
        composable(
            route = GenericCalculatorRoute,
            arguments = listOf(navArgument("calculatorId"), navArgument("calculatorName"))
        ) { backStackEntry ->
            val calculatorId = backStackEntry.getString()!!
            val calculatorName = backStackEntry.getString()!!
            CommonCalculatorScreen(calculatorId = calculatorId, calculatorName = calculatorName)
        }
    }
}
```

### 4.2 Dependency Injection (Hilt)
- `finance`: `FinancialCalculatorApplication`, `AppModule` (OkHttp, Retrofit, Room)
- `core`: `CoreModule` (Repositories, Use Cases, Engines)
- `feature-calculators`: `CalculatorsModule` (Custom ViewModels)
- `feature-dashboard`: `DashboardModule` (DashboardViewModel)

### 4.3 Theming & UI Polish
- Material3 theme matching current app colors (`core/presentation/theme/`)
- Dark mode support
- Consistent spacing, typography
- Animations for expand/collapse breakdown
- Input field focus handling, keyboard management

### 4.4 Configuration & Remote Config
- `ConfigRepository` for app config (banner ID, version, ads, in-app review)
- Version checking for calculator configs (local first, background refresh)

### 4.5 In-App Review
- Port `InAppReviewManager` to Kotlin
- Trigger after successful calculation (with cooldown)

---

## Key Design Decisions

### 1. Reuse Business Logic
- Extract **pure functions** from Java activities → Kotlin `CalculatorEngine` classes in `core/domain/engine/`
- No Android dependencies in calculation logic
- Easy to unit test

### 2. Generic vs Custom Calculators
| Type | Approach |
|------|----------|
| EMI, FD, SIP, RD, PPF, GST, VAT, Compare, Flat vs Reducing, Loan Profile, Eligibility | Custom Compose screens + dedicated ViewModels + shared engines |
| NPS, ATAL, CAGR, future generic | **Common Calculator Framework** (dynamic input/output from JSON) |

### 3. MVI at UI Layer
- Single state flow per screen
- Intents for user actions
- Reducer pattern in ViewModel
- Easy to test, debug, preview

### 4. Dynamic Dashboard
- `DashboardComponent` sealed class hierarchy in `feature-dashboard`
- Each component type → Compose rendering function
- Driven by `dashboard.json` structure

### 5. History Persistence
- Room DB with generic `CalculationHistory` entity
- JSON blob for input values (flexible per calculator)
- Type field for filtering

---

## File Structure Example

```
financial-calculator/
├── finance/
│   └── src/main/java/com/financialcalculator/finance/
│       ├── FinancialCalculatorApplication.kt
│       ├── MainActivity.kt
│       └── di/AppModule.kt
├── core/
│   └── src/main/java/com/financialcalculator/core/
│       ├── data/
│       │   ├── db/AppDatabase.kt, CalculatorHistoryDao.kt, MoreInfoDao.kt
│       │   ├── repository/CalculatorRepositoryImpl.kt, HistoryRepositoryImpl.kt, ConfigRepositoryImpl.kt
│       │   ├── remote/FinanceCalculatorService.kt, ApiModels.kt
│       │   └── local/SharedPrefsManager.kt
│       ├── domain/
│       │   ├── model/CalculatorEntity.kt, InputField.kt, OutputSection.kt, BreakdownItem.kt, CalculationHistory.kt
│       │   ├── usecase/CalculateUseCase.kt, GetCalculatorConfigUseCase.kt, SaveHistoryUseCase.kt, GetDashboardUseCase.kt
│       │   └── engine/EmiCalculatorEngine.kt, FdCalculatorEngine.kt, SipCalculatorEngine.kt, GenericFormulaEngine.kt, ...
│       ├── presentation/
│       │   ├── mvi/MviViewModel.kt, UiState.kt
│       │   ├── theme/Theme.kt, Color.kt, Type.kt
│       │   ├── component/InputField.kt, OutputCard.kt, BreakdownList.kt, MoreInfoSection.kt, CalculatorTopBar.kt
│       │   └── navigation/NavGraph.kt, Routes.kt
│       └── util/Extensions.kt, Formatters.kt, Constants.kt, Result.kt
├── feature-calculators/
│   └── src/main/java/com/financialcalculator/calculators/
│       ├── generic/
│       │   ├── CommonCalculatorViewModel.kt
│       │   └── CommonCalculatorScreen.kt
│       ├── emi/
│       │   ├── EmiViewModel.kt, EmiScreen.kt, EmiInputSection.kt, EmiSummarySection.kt, EmiBreakdownSection.kt
│       ├── compare/
│       │   ├── CompareLoanViewModel.kt, CompareLoanScreen.kt
│       ├── flatvsreducing/
│       │   ├── FlatVsReducingViewModel.kt, FlatVsReducingScreen.kt
│       ├── sip/
│       │   ├── SipViewModel.kt, SipScreen.kt, GoalSipViewModel.kt, GoalSipScreen.kt, LumpsumSipViewModel.kt, LumpsumSipScreen.kt
│       ├── banking/
│       │   ├── FdViewModel.kt, FdScreen.kt, RdViewModel.kt, RdScreen.kt, PpfViewModel.kt, PpfScreen.kt
│       ├── gst/
│       │   ├── GstViewModel.kt, GstScreen.kt, VatViewModel.kt, VatScreen.kt
│       ├── loanprofile/
│       │   ├── LoanProfileViewModel.kt, LoanProfileScreen.kt, HomeLoanEligibilityViewModel.kt, HomeLoanEligibilityScreen.kt
│       ├── history/
│       │   ├── HistoryViewModel.kt, HistoryScreen.kt
│       └── di/CalculatorsModule.kt
└── feature-dashboard/
    └── src/main/java/com/financialcalculator/dashboard/
        ├── DashboardViewModel.kt
        ├── DashboardScreen.kt
        ├── component/DashboardComponent.kt, BannerComponent.kt, HeaderComponent.kt, CalculatorGridComponent.kt, ...
        └── di/DashboardModule.kt
```

---

## Dependencies (libs.versions.toml)

```toml
[versions]
kotlin = "2.0.0"
compose-bom = "2024.08.00"
agp = "8.5.0"
hilt = "2.50"
room = "2.6.1"
coroutines = "1.8.0"
lifecycle = "2.7.0"
navigation = "2.7.7"
material3 = "1.3.0"
coil = "2.5.0"
gson = "2.10.1"
okhttp = "4.12.0"
retrofit = "2.11.0"

[libraries]
# Core
kotlin-stdlib = { group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version.ref = "kotlin" }
kotlinx-coroutines = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-serialization = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.6.0" }

# Compose
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-navigation = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
compose-lifecycle = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
compose-viewmodel = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version = "1.9.0" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# Network
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }

# Image loading
coil = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

# Expression evaluation for generic calculators
mXparser = { group = "org.mariuszgromada.math", name = "MathParser.org-mXparser", version = "5.2.1" }
```

---

## Effort Estimation

| Phase | Duration | Key Deliverables |
|-------|----------|------------------|
| 1. Foundation | 2 weeks | 4-module setup, core module, extracted calculation engines |
| 2. Common Framework | 2 weeks | Dynamic input/output system, generic calculator screen, MVI base |
| 3. Feature Modules | 3 weeks | All calculator screens (custom + generic), dashboard, history |
| 4. Integration | 2 weeks | Navigation, DI, theming, config, polish |
| **Total** | **~9 weeks** | **Production-ready rewrite** |

---

## Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| Formula evaluation for generic calculators | Use `mXparser` library for safe expression evaluation |
| Complex dynamic UI rendering | Build composable component registry pattern |
| Migration of Room DB | Keep same schema, use migration path |
| Performance with large breakdown lists | Use `LazyColumn` with keys |
| Keyboard handling in Compose | Use `WindowInsets`, `IME` animations, focus requesters |

---

## Optimized JSON Response Structure (GitHub Pages)

Since the backend is static JSON on GitHub Pages, we can redesign the response structure for optimal Compose/MVI consumption.

### Current Problems
1. **Double JSON encoding** - `cmpData` is a JSON string inside JSON
2. **Separate endpoints** - Calculator config + more info = 2 requests
3. **String-based input/output** - Requires Gson parsing at runtime
4. **No type safety** - Dynamic parsing, runtime errors
5. **Manual version checking** - Custom logic per calculator

### New Optimized Structure

#### 1. Single Dashboard Endpoint (`dashboard.json`)
```json
{
  "version": "2024.01.15",
  "config": {
    "bannerPlacementId": "banner_123",
    "showAds": true,
    "inAppReviewEnabled": true,
    "inAppReviewCooldownDays": 3
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

#### 2. Calculator Config Endpoint (Per Calculator Type) - `calculators/{type}.json`
```json
{
  "id": "emi",
  "name": "EMI Calculator",
  "type": "EMI",
  "version": 2,
  "category": "LOAN",
  "inputFields": [
    {
      "key": "principal",
      "type": "EDIT_TEXT",
      "label": "Loan Amount",
      "hint": "Enter loan amount",
      "inputType": "DECIMAL",
      "validation": { "required": true, "min": 1000, "max": 100000000 },
      "defaultValue": "",
      "formatter": "CURRENCY_INR"
    },
    {
      "key": "rate",
      "type": "EDIT_TEXT",
      "label": "Interest Rate (% p.a.)",
      "hint": "Enter rate",
      "inputType": "DECIMAL",
      "validation": { "required": true, "min": 0.1, "max": 50 },
      "defaultValue": "",
      "formatter": "PERCENTAGE"
    },
    {
      "key": "tenure",
      "type": "EDIT_TEXT",
      "label": "Tenure",
      "hint": "Enter tenure",
      "inputType": "NUMBER",
      "validation": { "required": true, "min": 1, "max": 360 },
      "defaultValue": "",
      "formatter": "NONE"
    },
    {
      "key": "tenureType",
      "type": "SPINNER",
      "label": "Tenure Type",
      "options": [
        { "label": "Years", "value": "YEARS" },
        { "label": "Months", "value": "MONTHS" }
      ],
      "defaultValue": "YEARS"
    },
    {
      "key": "startDate",
      "type": "DATE_PICKER",
      "label": "First Installment Date",
      "defaultValue": "TODAY"
    },
    {
      "key": "calculate",
      "type": "BUTTON",
      "label": "Calculate",
      "action": "CALCULATE",
      "style": "PRIMARY"
    }
  ],
  "outputConfig": {
    "summary": [
      { "key": "emi", "label": "Monthly EMI", "type": "KEY_VALUE", "formatter": "CURRENCY_INR" },
      { "key": "totalInterest", "label": "Total Interest", "type": "KEY_VALUE", "formatter": "CURRENCY_INR" },
      { "key": "totalPayable", "label": "Total Payable", "type": "KEY_VALUE", "formatter": "CURRENCY_INR" },
      { "key": "interestPercentage", "label": "Interest %", "type": "KEY_VALUE", "formatter": "PERCENTAGE" },
      { "key": "principalPercentage", "label": "Principal %", "type": "KEY_VALUE", "formatter": "PERCENTAGE" }
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
  ],
  "engine": "EMI_CALCULATOR"
}
```

#### 3. Generic Calculator Config (NPS, ATAL, CAGR) - `calculators/nps.json`
```json
{
  "id": "nps",
  "name": "National Pension System",
  "type": "GENERIC",
  "version": 1,
  "category": "RETIREMENT",
  "inputFields": [
    {
      "key": "age",
      "type": "EDIT_TEXT",
      "label": "Current Age",
      "inputType": "NUMBER",
      "validation": { "required": true, "min": 18, "max": 70 }
    },
    {
      "key": "retirementAge",
      "type": "EDIT_TEXT",
      "label": "Retirement Age",
      "inputType": "NUMBER",
      "validation": { "required": true, "min": 50, "max": 75 }
    },
    {
      "key": "monthlyContribution",
      "type": "EDIT_TEXT",
      "label": "Monthly Contribution",
      "inputType": "DECIMAL",
      "validation": { "required": true, "min": 500 }
    },
    {
      "key": "expectedReturn",
      "type": "EDIT_TEXT",
      "label": "Expected Return (% p.a.)",
      "inputType": "DECIMAL",
      "validation": { "required": true, "min": 1, "max": 20 }
    },
    {
      "key": "annuityPercentage",
      "type": "SPINNER",
      "label": "Annuity Purchase %",
      "options": [
        { "label": "40%", "value": "40" },
        { "label": "50%", "value": "50" },
        { "label": "60%", "value": "60" }
      ]
    },
    { "key": "calculate", "type": "BUTTON", "label": "Calculate", "action": "CALCULATE" }
  ],
  "outputConfig": {
    "summary": [
      { "key": "totalCorpus", "label": "Total Corpus", "type": "KEY_VALUE", "formatter": "CURRENCY_INR" },
      { "key": "maturityValue", "label": "Maturity Value", "type": "KEY_VALUE", "formatter": "CURRENCY_INR" },
      { "key": "pensionAmount", "label": "Monthly Pension", "type": "KEY_VALUE", "formatter": "CURRENCY_INR" },
      { "key": "lumpSum", "label": "Lump Sum Withdrawal", "type": "KEY_VALUE", "formatter": "CURRENCY_INR" }
    ],
    "breakdown": {
      "enabled": true,
      "groupBy": "YEAR",
      "columns": ["year", "contribution", "return", "total"],
      "expandable": false
    },
    "formulas": [
      { "label": "Corpus Formula", "expression": "monthlyContribution * ((1 + expectedReturn/1200)^(months) - 1) / (expectedReturn/1200)", "variables": ["monthlyContribution", "expectedReturn", "months"] }
    ]
  },
  "moreInfo": [
    { "question": "What is NPS?", "answer": "National Pension System is a government-sponsored pension scheme..." },
    { "question": "Tax benefits?", "answer": "Up to ₹1.5L under 80CCD(1) + ₹50k under 80CCD(1B)..." }
  ],
  "engine": "GENERIC_FORMULA",
  "formulaMap": {
    "totalCorpus": "monthlyContribution * ((1 + expectedReturn/1200)^(months) - 1) / (expectedReturn/1200)",
    "maturityValue": "totalCorpus",
    "lumpSum": "totalCorpus * (100 - annuityPercentage) / 100",
    "annuityAmount": "totalCorpus * annuityPercentage / 100",
    "pensionAmount": "annuityAmount * annuityRate / 12"
  }
}
```

### Key Improvements

| Aspect | Current | Optimized |
|--------|---------|-----------|
| **Parsing** | Double JSON + Gson at runtime | Single parse, typed Kotlin data classes |
| **Requests** | Dashboard + Calculator + MoreInfo (3) | Dashboard + Calculator (2) or bundled (1) |
| **Versioning** | Per-calculator manual check | Global dashboard version + per-calculator version |
| **Input Types** | Integer codes (1,2,3...) | String enums (`EDIT_TEXT`, `SPINNER`, `DATE_PICKER`) |
| **Validation** | Custom logic in Java | Declarative rules in JSON |
| **Formatters** | Hardcoded in adapters | Configurable (`CURRENCY_INR`, `PERCENTAGE`, `NONE`) |
| **Conditional Fields** | Not supported | `visibleWhen`, `dependsOn` in field config |
| **Engine Mapping** | Hardcoded switch in Repository | `engine` field in config (`EMI_CALCULATOR`, `GENERIC_FORMULA`) |

### Conditional Fields Example
```json
{
  "key": "annuityRate",
  "type": "EDIT_TEXT",
  "label": "Annuity Rate (%)",
  "inputType": "DECIMAL",
  "visibleWhen": { "field": "annuityPercentage", "operator": ">", "value": 0 }
}
```

### Kotlin Data Classes (Auto-generated from JSON Schema)
```kotlin
// Dashboard
data class DashboardResponse(
    val version: String,
    val config: AppConfig,
    val components: List<DashboardComponent>
)

sealed interface DashboardComponent {
    data class BannerCarousel(val id: String, val position: Int, val data: BannerData) : DashboardComponent
    data class SectionHeader(val id: String, val position: Int, val data: HeaderData) : DashboardComponent
    data class CalculatorGrid(val id: String, val position: Int, val data: CalculatorGridData) : DashboardComponent
    data class Spacer(val id: String, val position: Int, val data: SpacerData) : DashboardComponent
    data class WebView(val id: String, val position: Int, val data: WebViewData) : DashboardComponent
    data class AdPlaceholder(val id: String, val position: Int, val data: AdData) : DashboardComponent
}

// Calculator Config
data class CalculatorConfig(
    val id: String,
    val name: String,
    val type: CalculatorType,
    val version: Int,
    val category: String,
    val inputFields: List<InputFieldConfig>,
    val outputConfig: OutputConfig,
    val moreInfo: List<MoreInfoItem>,
    val engine: String,
    val formulaMap: Map<String, String>? = null
)

sealed interface InputFieldConfig {
    data class EditText(...) : InputFieldConfig
    data class Spinner(...) : InputFieldConfig
    data class DatePicker(...) : InputFieldConfig
    data class Button(...) : InputFieldConfig
}

data class OutputConfig(
    val summary: List<SummaryItemConfig>,
    val breakdown: BreakdownConfig?,
    val charts: List<ChartConfig>,
    val formulas: List<FormulaConfig>
)
```

### Migration Strategy
1. **Keep old endpoints** for backward compatibility
2. **Add new endpoints** with optimized structure
3. **Feature flag** in app config to switch
4. **Gradual migration** per calculator type
5. **Delete old** after full migration

---

## Build Status

`./gradlew clean :finance:assembleDebug` passes with no errors or warnings
(`finance/build/outputs/apk/debug/finance-debug.apk`). `:core:testDebugUnitTest` passes.

### Package
`com.horizonlabs.financialcalculator` — matches the retired `applicationId`, so the rewrite
publishes as an update to the existing Play Store listing. Sub-packages:
`...financialcalculator.core`, `.calculators`, `.dashboard`, `.finance`.

### API
v2 only. `Constants.BASE_URL` is the version-agnostic root
`https://ranjan-rajeev.github.io/Finanace-Calculator-Web/json/`; Retrofit paths add the
version, so there is no duplicated `/v2/v2` segment.

| Request | Hosted file |
|---|---|
| `v2/dashboard.json` | banner, section headers, calculator grids, spacer, ad |
| `v2/config.json` | ads, in-app review, play store version |
| `v2/calculators/{id}.json` | one `CalculatorConfig` per calculator |

There is deliberately **no** legacy parsing or migration code. Published APKs keep reading
the unversioned v1 documents on GitHub Pages with their own retired deserializers.

### Completed
- [x] 4-module Gradle setup (`finance`, `core`, `feature-calculators`, `feature-dashboard`); legacy `app/` excluded but kept for reference
- [x] Package rename to `com.horizonlabs.financialcalculator` across all modules, Gradle namespaces, `applicationId` and ProGuard rules
- [x] Core: MVI base, domain models, Room DB + DAOs, Retrofit service, repositories, suspend use cases, Hilt modules
- [x] Engines: EMI, FD, SIP, RD, PPF, GST, VAT, CompareLoan, FlatVsReducing, GenericFormula (mXparser)
- [x] JSON contract: kotlinx.serialization Retrofit converter, `classDiscriminator = "type"`, `@SerialName` on `DashboardComponent` / `InputFieldConfig`
- [x] `tools/generate_hosting_json.py` emits the complete `v1` + `v2` trees (36 files) into `github-pages/json`
- [x] `HostingJsonContractTest` decodes every generated file against the real Kotlin models and checks dashboard ids resolve to hosted calculators
- [x] Common calculator: `InputFieldComponents.kt`, `OutputComponents.kt`, `CommonCalculatorViewModel`, `CommonCalculatorScreen`
- [x] Dashboard: JSON-driven rendering, `HorizontalPager` banner with auto-play, chunked calculator grid, list, spacer, ad and webview placeholders
- [x] Navigation + Hilt end to end; CI workflow builds `:finance` and publishes `finance-debug.apk`
- [x] Removed Gradle/Kotlin deprecations (`packagingOptions`, library `targetSdk`, `buildDir`, `-Xopt-in`)
- [x] `Formatters`: Indian digit grouping computed explicitly. `DecimalFormat("#,##,##0")` and `NumberFormat.getNumberInstance(en-IN)` both fall back to western grouping on some JDK/ICU data, which rendered `1041387.88` as `1,041,387.88`
- [x] `Formatters.formatCurrencyINR` returns exact `₹10,41,387.88` values; `formatCompact` no longer falls through to `Double.toString()`, which had leaked values such as `5.413878800386408 L`
- [x] Input fields keep raw text instead of round-tripping through `Double`, so `500000` no longer displays as `500000.0` and a cleared field stays empty instead of silently becoming `0.0`
- [x] `FormattersTest` covers Indian grouping, paise rounding, compact abbreviation and parsing (10 tests, passing)
- [x] On-device EMI flow verified on `emulator-5554` API 37 against live `v2` JSON: `₹4,339.12` EMI, `₹5,41,387.88` total interest, `₹10,41,387.88` total payable, 51.99% interest, and a year-by-year breakdown

### Outstanding
- [ ] AdMob banner (`bannerPlacementId`) and in-app review are not implemented; the id in the generated JSON is a placeholder
- [ ] Banner images and calculator icons are not uploaded; every icon URL is empty except `EMI`
- [ ] Custom (non-generic) calculator screens are not written; everything routes through the common calculator screen
- [ ] `WebViewData` renders a placeholder card instead of a real WebView
- [ ] `InputFieldConfig.visibleWhen` is not applied in the UI (fields always render)
- [ ] Legacy `DashboardIntent.OnCalculatorClick` / `OnBannerClick` intents are inert; navigation happens through screen callbacks
- [ ] `GenericFormulaEngine` derives NPS/ATAL/CAGR formulas internally; verify results against the retired Java engines
- [ ] `CommonCalculatorViewModel` injects `calculatorId` into the engine value map so `GenericFormulaEngine` and `SipCalculatorEngine` can dispatch on type without a hidden JSON field
- [ ] History screen is a placeholder; `GetHistoryUseCase` is injected but unused
- [ ] `visibleWhen` conditions in the spec are unimplemented, so conditional fields cannot be published yet
- [ ] No unit tests for business logic or engines yet (deprioritised by request); `Formatters` and the JSON contract are covered
- [ ] `versionCode` is still `1` while the retired APK published `19`, so a Play Store update will be rejected
- [ ] The `WebViewData` placeholder opens the legacy web calculator in an external Chrome browser; it should render in-app

---

*This plan preserves all existing business logic while modernizing to Kotlin Compose, MVVM+MVI, with only 4 modules and optimized JSON structure.*
