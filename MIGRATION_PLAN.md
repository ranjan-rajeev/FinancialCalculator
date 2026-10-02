# FinancialCalculator — Migration to Kotlin + Jetpack Compose

**Goal:** Modernize to the current Android stack, convert 100% of Java to Kotlin, rebuild the UI in
Compose with **pixel-identical visual design**, and collapse the 14 hand-coded calculator screens
into one JSON-driven engine.

**Decisions locked in:**
- Remote JSON-driven UI is kept. *Additionally*, all currently hard-coded calculators get converted
  to JSON specs (bundled in `assets/` first, server later).
- `minSdk` 21 → **24**.
- **Single `:app` module**, strict package layering.

---

## 1. Current state — audit

| | |
|---|---|
| Language | 100% Java 8. **Zero Kotlin.** |
| Java | 98 files / **16,237 LOC** |
| Layouts | 48 XML / **7,362 LOC** |
| Activities | 20 · Fragments: 4 · Adapters: 22 · ViewHolders: 19 |
| Build | Gradle 8.7, AGP 8.5.2, compileSdk 34, targetSdk 35, minSdk 21 |
| Networking | Retrofit 2.9 + **RxJava2** + Gson |
| Persistence | SharedPreferences (version-gated cache), Room 2.6.1 via `annotationProcessor` |
| Images | Glide 4.16 |
| UI | ViewBinding-free `findViewById`, CardView, Material 1.12, `com.synnapps:carouselview` |
| DI | Manual singletons |
| Navigation | No Navigation Component. `Util.inAppRedirection()` string-`switch` router |
| Tests | 2 files, 13 `@Test`s, 6 of which are duplicates |

### 1.1 The build is already broken

`com.synnapps:carouselview:0.1.5` was published to **JCenter only**, which is sunset. `jcenter()`
is declared in both `build.gradle` files. `./gradlew assembleDebug` fails at
`checkDebugAarMetadata` with *"Could not find com.synnapps:carouselview:0.1.5"*.

This is fixed first, before anything else.

### 1.2 Architecture as it stands

Two server-driven engines, plus 14 hard-coded screens:

**A. Dashboard** — `GET /Finanace-Calculator-Web/json/dashboard.json` → `List<HomePageModel>`,
each item `{cmpData, cmpId, cmpPos, cmpType, firebaseId}`. `cmpType` selects the layout:

| cmpType | Meaning |
|---|---|
| 1 | Carousel **with dot indicators**, 3 s auto-scroll |
| 2 | Inline WebView |
| 3 | Auto-scroll pager, **no** indicators |
| 4 | Section header (marquee) |
| 5 | 3-column calculator grid |
| 6 | Vertical spacer (`cmpData` = px margin) |
| 7 | Ad slot (parses, renders nothing) |

`cmpData` is a **JSON-encoded string containing another JSON array**, re-parsed inside every
ViewHolder via an inner `AsyncTask`. Offline fallback is a byte-identical `assets/dashboard.json`.

**B. Generic calculator** — per-calculator JSON (`npscalculator.json`, `atalcalculator.json`,
`cagrcalculator.json`) defines both the input form and the output rows.

Inputs: `1` EditText · `2` Date · `3` Spinner · `4` EditText+Spinner · `5` Button · `6` WebView ·
`7` Text label.
Outputs: `1` Key/value · `2` Formula template · `3` Ring-graph · `4` Divider.

Values live in `CalculatorEntity.inputHashmap : HashMap<Character, BigDecimal>`.
Outputs are **chained**: each output row evaluates a formula, writes the result back under
`outKey`, so later rows can reference it. Correctness depends on
`AsyncTask.SERIAL_EXECUTOR` happening to run rows in list order.

`Util.evaluate()` is a shunting-yard BigDecimal parser (`PRECISION = 12`, `+ - * / ^`).
`Util.evaluateString()` is a template DSL: `$x` → raw value, `$X` → spinner label,
`@ expr @` → inline evaluated formula (always `setScale(0)`, i.e. integer).

Cached in SharedPreferences, gated on `version`; keys `<firebaseId>` (full JSON) and
`<firebaseId>version` (int).

**C. 14 hard-coded screens** — EMI, Compare Loan, Flat vs Reducing, SIP, SIP Goal, LumpSum, FD,
RD, PPF, GST, VAT, Create/View Loan Profile, Home Loan Eligibility.

### 1.3 Duplication (what the JSON conversion deletes)

| Duplicated thing | Copies |
|---|---|
| `content_<screen>.xml` calculator layouts, ~370 lines each, **identical view-ID sets**, differing only in hint strings | 8 |
| `getListDetailsYearly(...)` amortisation builder | 7 |
| `getListDetailsYearlyMonthly` + `...Quartely` pair | 4 |
| Compound formula `amount * pow(1 + r/n, n*t)` | 20 |
| EMI formula `effectiveROI = i/1200` | 9 |
| `EmiAdapter` vs `FDAdapter` (differ by **2 lines**) | 2 |
| `YearEmiAdapter` vs `FDYearAdapter` (differ by **1 line**) | 2 |

### 1.4 Live bugs found (to fix during migration)

| # | Bug | Impact |
|---|---|---|
| 1 | `GenericOutputAdapter` returns `KeyValueViewHolder` for `type 4` (divider); `item_divider.xml` has no `tvTitle`/`tvValue` → **NPE** | Crashes NPS/Atal on scroll |
| 2 | `WebViewViewHolder` registers its click listener reading `webViewEntity` **before** the async parse assigns it | NPE on tap |
| 3 | `EditTextSpinnerViewHolder` never sets `isValid` at bind | Calculate aborts unless the text field was touched |
| 4 | `RoomDatabase.APP_DATABASE` is `static final ... = null`, never assigned → a **new DB + connection pool per call** | Leaks, slow |
| 5 | `getCalculatorObserVableType()` `default:` silently returns the **NPS** JSON for unknown `calId` | Wrong calculator shown |
| 6 | `HomeLoanEligibility:222` — `double r = 0 - (rate/100)` | Negative interest rate |
| 7 | PPF monthly branch (`:377`) omits the `/12` present in the yearly branch (`:302`) | 12× wrong principal |
| 8 | `MoreInfoEntity` Room table is **never written or read** | "Did you know?" offline is broken |
| 9 | A new adapter is built on every Calculate tap → whole form + all EditText state rebuilt | Focus/cursor lost |
| 10 | `values/strings.xml` has **25 duplicate resource names** | Silent overrides |
| 11 | `app/finance.jks` committed **with password `finance` in plaintext** in `build.gradle` | **Security** |
| 12 | Parseable-but-unused fields: `EditTextEntity.regex`, `.isFocus`, `MoreInfoEntity.ques`; no bounds validation despite titles promising "(18-60 Years)" | No validation |
| 13 | `SplashActivity` + generic calc launched from `DashBoardFragment` **without** the intent extra → NPE swallowed → blank screen | Dead path |
| 14 | `GenericSearchHistoryAdapter` return target hard-coded to `FDCalculatorActivity` | Wrong screen |

---

## 2. Target stack

| | Current | Target |
|---|---|---|
| Gradle | 8.7 | **9.6** (required by AGP 9.4) |
| AGP | 8.5.2 | **9.4.1** |
| Kotlin | — | **2.4.20** (2.3 hit EOL Jun 2026) |
| JDK | 8 | **17 toolchain** (21 in CI) |
| compileSdk / targetSdk | 34 / 35 | **36 / 36** |
| minSdk | 21 | **24** |
| Compose | — | BOM **2026.09.00** (Compose 1.12.1, Material3 1.4.0) |
| Serialization | Gson | **kotlinx.serialization** |
| Async | RxJava2 + AsyncTask | **Coroutines + Flow** |
| HTTP | Retrofit 2.9 | **Retrofit 3 / OkHttp 5** |
| DI | manual singletons | **Hilt** |
| DB | Room (APT) | **Room + KSP** (KSP version tied to Kotlin 2.4.20 — confirm at implementation) |
| Images | Glide 4.16 | **Coil 3** |
| Navigation | string `switch` | **Navigation Compose** (type-safe routes) |
| Build script | Groovy | **Kotlin DSL + version catalog** |

---

## 3. Phase-by-phase plan

Every phase ends with a **green `assembleDebug` + passing unit tests**. Phases 1–2 are the ones that
protect "design exactly the same".

---

### Phase 0 — Unbreak the build & clean the foundation
*Why first: nothing else can be verified until the project compiles.*

1. Replace `jcenter()` with `mavenCentral()` + `google()` in both build files.
2. **Remove `com.synnapps:carouselview`.** It is the sole unresolvable dependency and is used only
   by the two carousel ViewHolders. Replace with `androidx.viewpager2` + a `LaunchedEffect`
   coroutine for the 3 s auto-scroll. (`Phase 5` does the full Compose version.)
3. Convert to Kotlin DSL: `settings.gradle.kts`, `build.gradle.kts`, `app/build.gradle.kts`.
4. Add `gradle/libs.versions.toml`; move **every** version/SDK level into it. Single source of truth.
5. Bump: Gradle 9.6, AGP 9.4.1, JDK 17 toolchain, `compileSdk`/`targetSdk` 36, `minSdk` 24,
   `namespace`, `buildConfig = true` (still needed for `HOST`).
6. Delete `android.enableJetifier`, `android.nonTransitiveRClass=false`,
   `android.nonFinalResIds=false`, `multiDexEnabled` (dead at minSdk 24), and the commented-out
   `productFlavors` blocks.
7. **Security:** remove `app/finance.jks` from git and the hardcoded `storePassword 'finance'`.
   Move to `keystore.properties` (gitignored) + GitHub secrets; CI signs via
   `r0adkll/sign-android-release` (already wired) or `signingConfigs` from env.
8. Fix the 25 duplicate names in `values/strings.xml` (`gst_type`, `fd_type`, `ppf_duration`,
   `ppf_frequency`, `sip_frequency`, `app_name`, all `title_activity_*`, …).
9. Delete dead code with zero behaviour change: `GenericInfoActivity`, `InputTypeEntity`,
   `TextWithValueViewHolder`, `dashboard/DashBoardFragment` + its 2 adapters,
   `EmiSearchHistoryAdapter`, all commented-out `AsyncTask` blocks, `BaseActivity.setUpAdView()`.
10. Enable `lint { abortOnError = true }` (currently `false`) and `testOptions.unitTests.isReturnDefaultValues`.
11. **Capture the visual baseline.** Install the current build on 3 profiles (small/normal/tablet),
    screenshot **every** screen and state (including the calculator with inputs filled and results
    shown). Store under `docs/baseline/`. This is the reference for Phase 1 parity.

**Exit criteria:** `assembleDebug` + `bundleRelease` succeed; `lint` passes; baseline screenshots
captured; keystore no longer in git.

---

### Phase 1 — Compose design system with pixel parity
*Why: this is where "design exactly the same" is won or lost. Build the legacy look once, precisely.*

1. Extract the **exact** tokens from XML into `ui/theme/`:

   **Colors actually in use** (out of 211 defined):
   `colorPrimary #3F51B5` · `colorPrimaryDark #303F9F` · `colorAccent #FF1744` ·
   `bg_light #eaeff7` (screen + app-bar background) · `dashboard_bg #e3e3e3` ·
   `header_dark_text #5D5C5D` · `header_light_text #6A6F71` · `description_text #929292` ·
   `secondary_text_color #9ba0a6` · `application_primary_text_color #212121` ·
   `application_secondary_text_color #757575` · `uvv_black #000000` · `divider #BDBDBD` ·
   `colorGreyD8 #d8d8d8` · `colorGreyEA #EAEAEA` · `lightGrey #E0E0E0` ·
   `offer_info_white #DDFFFFFF` (field fill) · `bg #c9c5c5` (field border) ·
   `colorPumpkin #d88600` (Did-you-know heading) · `green_descent #42ceb2` ·
   `red_descent #de6d75` · `blue_descent #4b76c2` · `progress_blue #009fe3`

   **Type** — map `TextViewStyle.{Large,Medium,Small,VerySmall}` + hardcoded sizes onto a
   Material3 `Typography` with **identical sp and identical colours**:
   25sp bold `#303F9F` (output value) · 20sp bold `#d88600` · 15sp `#5D5C5D` ·
   13sp `#6A6F71` · 12sp `#929292` · 10sp `#6A6F71` (dashboard cell label).
   Material3 default line-heights differ from the platform default — **pin
   `lineHeightStyle`/explicit `lineHeight`** per style so line boxes match.

   **Shape** — 8dp cards · 5dp fields & history cards & carousel dots · 15dp button ·
   1dp borders · oval badges.

2. Build legacy-faithful composables in `ui/components/`:
   - `LegacyTextField` — 50 dp tall, `#DDFFFFFF` fill, 5 dp radius, 1 dp `#c9c5c5` border
     (`#3F51B5` when focused), 10 dp top inner padding, 8 dp horizontal inner padding,
     hint `#929292`, `TextInputLayout`-equivalent floating hint + error,
     **comma-formatting applied on focus loss** (current behaviour).
   - `LegacyButton` — 15 dp radius, fill `#3F51B5` + 1 dp `#303F9F` stroke; pressed →
     fill `#303F9F` + 3 dp `#3F51B5` stroke; 12 dp padding; 20 dp top / 5 dp side margins; white text.
   - `LegacySpinner` — same field chrome + right-aligned dropdown arrow, 50 dp tall.
   - `LegacyCard` — 8 dp radius, 8 dp elevation, 12 dp margin (matches `CardView`).
   - `LegacyRingProgress` — ring with `innerRadiusRatio 2.3`, 5 dp thickness, `#E0E0E0` track,
     `#3F51B5` arc, max 100, **2000 ms `DecelerateInterpolator`** (Compose: `FastOutSlowInEasing`
     is *not* equivalent — hand-roll the interpolator or use a `FrameInterpolator`).
   - `LegacyCircleBadge` — 70 dp oval, green/red/blue descent colours.
   - `ZebraRowBackground` — reproduces `Util.getFixedBackground` / `getRandomBackground`.
   - `LegacyMarquee` — Compose `BasicMarquee` with the current `marqueeRepeatLimit="marquee_forever"`.
3. Replace XML drawables with equivalent Compose `Shape`/`Brush`/`ImageVector` definitions.
4. **Screenshot parity tests** (Roborazzi or Paparazzi) asserting rendered composables match
   `docs/baseline/` within a tight pixel threshold.

**Exit criteria:** every `ui/components` primitive has a passing screenshot-parity test against the
Phase 0 baseline.

---

### Phase 2 — Domain core (pure Kotlin, zero Android deps)
*Why: this is the part that must be provably numerically identical. Test it in isolation first.*

1. Replace `int type` discriminators with **sealed interfaces** (`domain/model/`):
   - `CalculatorSpec(calId, calName, firebaseId, iconUrl, version, inputs, outputs)`
   - `sealed interface InputSpec` → `EditText(key, title, inputType, maxLength, regex, min, max)` ·
     `Date(key, title, minEpochMs, maxEpochMs)` · `Spinner(key, title, options)` ·
     `EditTextWithSpinner(key: Pair2, title, inputType, maxLength, options)` ·
     `Button(title, action, redUrl)` · `TextLabel(title)` · `WebView(redUrl, webUrl, content, title)`
   - `sealed interface OutputSpec` → `KeyValue(outKey, formula, label, currency)` ·
     `Template(outKey, template)` · `RingGraph(outKey, principalMsg, interestMsg, principalFormula, interestFormula, currency)` ·
     `Divider` · **`Schedule(...)`** ← *new, see Phase 7*
   - `sealed interface DashboardNode` → `Carousel(items, showIndicators)` · `WebViewNode` ·
     `Pager(items)` · `Header(text)` · `CalculatorGrid(items)` · `Spacer(heightDp)` · `AdSlot`
2. `domain/eval/ExpressionEvaluator` — faithful port of `Util.evaluate`:
   same shunting-yard, same `PRECISION = 12`, same `hasPrecedence`, same `^`-via-`double`
   (keep it; changing to `BigDecimal.pow` would alter results).
   **Fix:** return `Result<BigDecimal>` instead of NPE-ing on an undefined variable.
   Never mutate a `HashMap<Char, BigDecimal?>`; an undefined variable is an error, not a null.
3. `domain/eval/TemplateRenderer` — port `$x` / `$X` / `@expr@`.
   Produces `AnnotatedString`. The original built `StyleSpan`/`ForegroundColorSpan` and then
   **never applied them** — decide explicitly: keep plain text (exact current pixels) or apply the
   spans. Default: **plain text, for parity.**
4. `domain/calc/` — extract the amortisation engine **once**, replacing 15 copies:
   `AmortizationSchedule(principal, annualRate, periods, frequency) → List<YearBucket>`
   (`YearBucket` = year/month rows), plus `compoundMaturity()`, `emi()`, `sipFutureValue()`,
   `sipGoal()`, `gstSlab()`, `vatSlab()`, `flatVsReducingRate()`.
5. `domain/format/IndianNumberFormat` — **parity trap.** Must reproduce `Util.getCommaSeparated`
   byte-for-byte, including: `##,##,###` lakh/crore grouping; **two decimals when the input
   contains a `.`, else trailing zeros stripped** (`.##`); the >12-character branch; and
   `""` on `NumberFormatException`.
6. Tests:
   - Port all 6 real `CalculatorUnitTest` cases (de-duplicate `ExampleUnitTest`).
   - **Golden tests for the evaluator**: every `formulae` string from all three live calculator
     JSONs (`nps`, `atal`, `cagr`) run against recorded inputs → recorded outputs.
   - **Golden tests for the formatter**: ≥30 cases incl. negative, >12-char, decimals, malformed.
   - Unit tests per calc engine incl. frequency variants (monthly/quarterly/half-yearly/annual).

**Exit criteria:** 100% of domain code covered; all goldens green.

---

### Phase 3 — Data layer
1. **kotlinx.serialization** replacing Gson. Drop `inputHashmap`/`spinnerHashMap` from the
   serializable model entirely — runtime state must never be persisted (this is what forced the
   fragile `HashMap<Character, BigDecimal>` JSON).
2. Parse nested JSON-in-a-string (`cmpData`, `data`) **once in the repository**, pass typed
   objects to the UI. Eliminates every `ConvertAsync` inner AsyncTask.
3. Retrofit 3 + OkHttp 5 + kotlinx-serialization converter; Hilt DI graph.
4. `DashboardRepository` — network-first, `assets/dashboard.json` fallback (identical offline
   behaviour), exposing `Flow<List<DashboardNode>>`.
5. `CalculatorRepository` — **preserve the version-gated cache semantics exactly**: refresh when
   `serverVersion > cachedVersion`, else read cache. Move to DataStore with a **one-time import**
   of the existing SharedPreferences file so current users keep their cache.
6. Room via KSP. Fix the singleton bug (Hilt `@Singleton`). Actually **implement** the `MoreInfo`
   persistence that was never wired, or drop the table if specs become local.
7. **Bug fix:** unknown `calId` must surface an error, never silently return NPS.
8. Repository contract tests with `MockWebServer` — one test per endpoint, asserting the parsed
   spec matches the shipped JSON.

**Exit criteria:** all endpoints covered by tests; repository layer has no Android UI deps.

---

### Phase 4 — Navigation
1. **Navigation Compose** with type-safe `@Serializable` routes:
   `splash` · `home` · `about` · `calculator/{calId}` · `web?url=&title=` · `history?type=`
2. Delete `Util.inAppRedirection()`'s string-`switch`. Replace with a typed
   `CalculatorRoute` resolved from `calId`/`redUrl`, `when`-exhaustive with an explicit error branch.
3. `MainActivity` → thin `ComponentActivity` + `FCFCalculatorApp()`.
4. Recreate the drawer with `ModalNavigationDrawer`: 176 dp header, `side_nav_bar` background,
   80 dp `ic_launcher_round`, "Welcome Guest" as `bodyLarge`, 4 items
   (Home / Share App / Like our App? / About Us), `nav_home` checked by default,
   close-then-switch (keep the 200 ms delay or drop it — cosmetic).
5. Preserve: double-tap-to-exit, share-to-WhatsApp with chooser fallback, `market://` rate link,
   the Play **In-App Update** flow *and* the config-driven "Update App!!!" dialog. Factor these into
   a reusable `AppUpdateCoordinator`.

**Exit criteria:** every former activity is reachable via a route; no `startActivity` for
calculator navigation remains.

---

### Phase 5 — Dashboard screen
`LazyColumn` over `sealed interface DashboardNode`:

- **Carousel / Pager** → `HorizontalPager`, 3000 ms `LaunchedEffect` auto-scroll,
  `withInfiniteScroll`, stop-on-touch. Dot indicators reproduced exactly:
  width `(screenWidthPx - 12dp) / itemCount`, 5 dp radius, `#c5c5c5` unselected / `#949494` selected.
  Tapping an image → `web?url=…`.
- **Header** → `LegacyMarquee` (10 dp margin, centered, bold `#303F9F`, 15 sp).
- **CalculatorGrid** → `LazyVerticalGrid(columns = 3, fixed)` reproducing `item_dashboard.xml`:
  50 dp icon, `RelativeLayout` margins 10 dp top / 15 dp left / 10 dp bottom, 5 dp padding,
  10 dp gap to a `wrap_content`, centered, 10 sp `#6A6F71` label.
  Icon = remote `iconUrl` if non-empty **else** `Util.getCalculatorIcon(calId)` local drawable.
- **Spacer** → `Spacer(heightDp)` (note: current code treats `cmpData` as **px**; keep px for parity).
- **AdSlot** → render nothing (it already does) and delete the dead ViewHolder.
- Loading → replace the deprecated `ProgressDialog` with an inline indicator matching
  `BaseFragment.showDialog()`.

**Exit criteria:** dashboard screenshot matches the Phase 0 baseline within threshold.

---

### Phase 6 — The universal calculator screen
*This is the core deliverable: one screen + one ViewModel replaces 14 activities.*

`CalculatorViewModel` exposing a single immutable `StateFlow<CalculatorUiState>`:

```kotlin
data class CalculatorUiState(
    val spec: CalculatorSpec?,
    val values: Map<Char, BigDecimal>,        // no nulls — validation runs first
    val spinnerLabels: Map<Char, String>,
    val fieldErrors: Map<Int, String>,
    val results: List<RenderedOutput>,        // computed, not mutated during render
    val schedule: List<YearBucket>?,
    val isLoading: Boolean,
)
```

Key decisions:

1. **Deterministic result computation.** Replace the accidental `AsyncTask.SERIAL_EXECUTOR`
   ordering with an explicit pure fold over `outputs`: each row evaluates, writes back under
   `outKey`, and yields a rendered row. Same results, guaranteed order, no `AsyncTask`.
2. **Inputs hoisted into the ViewModel** (`rememberSaveable`) → **fixes the focus/cursor loss on
   Calculate for free.**
3. **Validation** runs before calculation, producing per-field errors and focusing the first
   invalid field (parity with `showResult()`). Add optional bound/`regex` validation driven by the
   fields the JSON *already carries but the old code ignored* — opt-in per spec field so existing
   calculators behave identically until you enable it.
4. Output rendering — one composable per `OutputSpec` subtype:
   `KeyValue` (25 sp bold `#303F9F`, centered, comma-formatted + `curr` suffix),
   `Template` (HTML → `AnnotatedString`), `RingGraph` (2 s decelerating ring pair),
   `Divider` (**fixes crash #1** with a dedicated item), `Schedule` (below).
5. Preserve: hide-keyboard + smooth-scroll to the output card on Calculate.
6. Card reveal behaviour: output/more-info cards start hidden and appear on calculate.

**Exit criteria:** all three live JSON calculators (NPS, Atal, CAGR) render correctly, including
their dividers which crash today.

---

### Phase 7 — Convert the hard-coded calculators to JSON specs
*This is the explicit goal: make every static calculator JSON-driven.*

1. Add a new output type **`Schedule`** (the one genuinely new engine capability), because the
   formula DSL has no loops. Schema sketch:

   ```json
   { "type": 5, "outKey": "s", "curr": "₹", "outMsg": "Amortization Schedule",
     "data": "{\"algo\":\"loan_amortization\",\"frequency\":\"$d\",\"principal\":\"x\",\"rate\":\"r\"}" }
   ```
   Rendered with the existing expand/collapse pattern (`EmiAdapter` → `YearEmiAdapter` →
   month rows), reproducing `item_loan_details.xml`: 4 equal-weight centered 12 sp `#5D5C5D` columns
   (year, principal paid, interest paid, balance), 8 dp vertical padding, zebra striping, and
   `ic_add_box_black_24dp` disclosure on the year row.

2. Author `app/src/main/assets/calculators/*.json` in the **existing** schema so the same engine
   and the same `<key>/<formulae>` DSL apply:

   | File | Replaces | Notes |
   |---|---|---|
   | `emi.json` | `EmiCalculatorActivity` | + Schedule |
   | `compare_loan.json` | `EmiCompareActivity` | two loans, comparison output |
   | `flat_vs_reducing.json` | `FixedVsReducingActivity` | |
   | `sip.json` | `SIPCalculatorActivity` | + Schedule |
   | `sip_goal.json` | `SIPGoalCalculatorActivity` | uses `SIPGoal`'s own math (not the compound formula) |
   | `lumpsum.json` | `LumpSumpSipActivity` | + Schedule |
   | `fd.json` | `FDCalculatorActivity` | + Schedule |
   | `rd.json` | `RDCalculatorActivity` | + Schedule |
   | `ppf.json` | `PPFCalculatotActivity` | + Schedule |
   | `gst.json` | `GstCalculatorActivity` | slabs 5/12/18/28 |
   | `vat.json` | `VatCalculatorActivity` | slabs 1/4/5/12.5 |
   | `loan_profile.json` | `CreateLoanProfileActivity` / `ViewLoanProfile` | |
   | `home_loan_eligibility.json` | `HomeLoanEligibility` | **fix bug #6** (negative rate) and re-derive |

3. **Numerical proof.** Before deleting the Java, build a throwaway JVM test that runs the *old*
   Java formula and the *new* engine on the same input vectors and asserts equality. For the
   amortisation screens also diff full schedule tables. Green before any deletion.
4. **Delete:** 14 activities, 20 adapters, 14 `content_*.xml` layouts,
   `FDAdapter`/`FDYearAdapter`/`EmiAdapter`/`YearEmiAdapter`/`LoanProfileAdapter`, and the
   duplicated `getListDetailsYearly` family. **≈7,000 LOC of Java + 5,000 LOC of XML deleted.**
5. Push the specs to the GitHub Pages backend later — the app needs **no** change for that,
   which is the payoff of keeping the schema stable.

**Exit criteria:** every calculator screen renders and computes identically; zero calculator
activities remain; all layouts except `nav_header_main` are deleted.

---

### Phase 8 — Search history
1. Room-backed, `Flow`-driven. Preserve the `GenericSearchHistoryEntity.listKeyValues` blob shape
   **and its existing rows** (schema-compatible migration, no data loss).
2. `item_emi_history.xml` design: 5 dp radius / 5 dp elevation card, 10 dp margins,
   70 dp circular rate badge (colour by `Util.getFixedBackground`), 1 dp `#BDBDBD` divider,
   24 dp forward arrow, right-aligned date `#757575`.
3. Zebra rate-badge colours via `getFixedBackground(position)` and `getRandomBackground()`.
4. FAB (`ic_dialog_email`, `fab_margin` 16 dp) → delete-entry, matching `RESULT_CANCELED`.
5. **Fix bug #14:** return route derived from the stored `type`, not hard-coded to FD.
6. Reuse the Phase 6 `Schedule` renderer for schedule previews.
7. Delete `EmiSearchHistoryAdapter` and the dead EMI-history Room path if genuinely unused.

**Exit criteria:** history lists, restores, and deletes; existing rows still visible.

---

### Phase 9 — Splash, About, WebView
1. **Splash** — keep the exact composition: `bg.webp` at `alpha 0.2` `centerCrop` full-bleed,
   200 dp ring `ProgressBar` using the `circular` ring drawable, "Finance\nCalculator" 25 sp bold
   `#3F51B5` centered. Keep the 2000 ms delay (from `BuildConfig`). Compose-only (no Android
   view needed — the ring is a Canvas draw). Also performs the `config.json` fetch.
2. **About** — static `title`/`desc`/`version` text + the 5-entry `AboutUsEntity` list rendered in
   `layout_whats_new_item.xml`.
3. **WebView** — genuinely cannot be pure Compose; wrap in `AndroidView`. Preserve
   `javaScriptEnabled`, `geolocationEnabled`, `lightTouchEnabled`, `soundEffectsEnabled`, and the
   `webUrl == "" → loadData(content, "text/html", "UTF-8")` branch. **Fix bug #2** by binding state
   before attaching the click listener.

**Exit criteria:** all three match baseline; WebView content loads and back-navigation works.

---

### Phase 10 — Quality gate
1. `lint` clean with `abortOnError = true`; add **detekt** + **ktlint** to CI.
2. **Compose UI tests for every calculator spec**: fill a fixed input vector, assert the exact
   rendered output strings (regression-locked by the Phase 2 goldens).
3. **Screenshot regression** vs `docs/baseline/` across small / normal / tablet profiles.
4. R8 **full mode** + resource shrinking; verify `minSdk 24` + no reflection breakage
   (kotlinx.serialization needs its generated serializers kept).
5. **Baseline profile** via Macrobenchmark for the dashboard + calculator screens.
6. Accessibility pass — the current app has **no** `contentDescription` on any icon-only control.
7. Strict-mode / memory pass — confirm no `Context` leaks (cf. `LongBannerFragment`'s
   `public static Context activity`).

**Exit criteria:** CI green on lint + unit + screenshot + UI tests.

---

### Phase 11 — Release plumbing
1. GitHub Actions: `ubuntu-latest` (**`ubuntu-20.04` is retired**), JDK 21, Gradle caching via
   `gradle/actions/setup-gradle`, `actions/upload-artifact@v4` (**v3 is deprecated**),
   `actions/checkout@v4`.
2. Read `versionCode`/`versionName` from `libs.versions.toml` (the workflow currently greps
   `build.gradle`, which breaks under Kotlin DSL).
3. Signing entirely via secrets; nothing sensitive in the repo.
4. Add a `main`/`develop` trigger and keep `workflow_dispatch` for manual builds.
5. Optional: Play Integrity, DataStore migration notes in release docs.

---

## 4. Suggested package layout (single module)

```
com.financialcalculator
├── domain/                     ← pure Kotlin, no Android imports
│   ├── model/                  sealed spec types
│   ├── eval/                   ExpressionEvaluator, TemplateRenderer
│   ├── calc/                   AmortizationSchedule + per-calculator engines
│   └── format/                 IndianNumberFormat, DateFormatting
├── data/
│   ├── remote/                 Retrofit API, DTOs
│   ├── local/                  Room, DataStore
│   ├── repository/             DashboardRepository, CalculatorRepository, HistoryRepository
│   └── mapper/                 DTO → domain
├── ui/
│   ├── theme/                  Color, Type, Shape — pixel-exact tokens
│   ├── components/             Legacy* wrappers
│   ├── navigation/             Routes, AppNavHost, Drawer
│   ├── splash/  dashboard/  calculator/  history/  about/  web/
└── di/                         Hilt modules
```

Dependency rule (enforce with a Gradle lint/`dependencyAnalysis` check or an ArchUnit-style test):
`ui → domain`, `data → domain`, `domain → nothing`.

---

## 5. Risk register

| Risk | Impact | Mitigation |
|---|---|---|
| Visual drift in Compose | Fails the core requirement | Phase 0 captures baselines **before** any UI change; Phase 1 screenshot-parity tests; pixel-threshold CI gate |
| Numeric drift in the evaluator / amortisation | Wrong money figures — the worst possible bug | Phase 2 goldens locked from live JSON; Phase 7 old-vs-new differential test **before** deleting any Java |
| `##,##,###` lakh/crore grouping parity | Every formatted number changes | Dedicated golden test; do **not** "modernise" to `en-IN` `NumberFormat` casually — the `.00` vs `.##` trailing-zero rule differs |
| Ring animation timing/interpolator | Visible difference | `DecelerateInterpolator` ≠ `FastOutSlowInEasing`; hand-roll or use `FrameInterpolator` |
| Material3 dynamic colour / theming | Design changes on Android 12+ | Disable dynamic colour explicitly; the app is light-only |
| Compose marquee behaviour | Header animation differs | `BasicMarquee` param-by-param against `item_header.xml` |
| Losing the user's cached calculator JSON | Extra network on first launch | DataStore migration from the existing SharedPreferences file; keep the `<firebaseId>`/`<firebaseId>version` key scheme |
| Search-history Room data loss | Users lose saved entries | Schema-compatible migration; never bump the DB version destructively (current `fallbackToDestructiveMigration()` must be replaced) |
| 14 screens is a lot of parallel work | Schedule slip | Phases are independently shippable; Phase 7's 13 specs are independent of each other |
| Remote JSON is unvalidated | Crash / blank screen | Phase 2 sealed types + Phase 3 graceful failure + local `assets/` fallback for every spec |

---

## 6. Effort estimate

| Phase | Work |
|---|---|
| 0 · Unbreak build & foundation | 0.5–1 d |
| 1 · Compose design system + parity tests | 3–4 d |
| 2 · Domain core + goldens | 3–4 d |
| 3 · Data layer | 2–3 d |
| 4 · Navigation | 2 d |
| 5 · Dashboard | 2–3 d |
| 6 · Universal calculator screen | 4–5 d |
| 7 · 13 calculator JSON specs + differential proof | **6–8 d** |
| 8 · Search history | 1–2 d |
| 9 · Splash / About / WebView | 1 d |
| 10 · Quality gate | 2–3 d |
| 11 · Release plumbing | 0.5–1 d |
| **Total** | **~28–37 working days** |

Phases 0–6 + 9–11 (≈20–27 d) deliver a fully modernized app on the existing 3 calculators.
Phases 7–8 (≈7–10 d) are the JSON conversion of the static calculators and can be split or
parallelized per calculator.

---

## 7. Immediate next actions

1. Create a branch: `git checkout -b migration/kotlin-compose`.
2. **Commit the Phase 0 baseline screenshots to `docs/baseline/`** *before* touching any UI code.
3. Execute Phase 0. Nothing else can be verified until the project compiles.