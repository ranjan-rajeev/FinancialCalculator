FinancialCalculator Project Exploration Summary
1. Overall Project Structure
   This is an Android Studio project (Kotlin-based) modernizing a financial calculator app from 100% Java/XML to Kotlin + Jetpack Compose. The project has a single :app module.
   Key Directory Structure:
   /app/src/main/java/com/financialcalculator/
   ├─ model/                     ← 16 entity classes (see below)
   ├─ ui/
   │  ├─ theme/                  ← Color, Type, Shape tokens
   │  └─ components/             ← Legacy wrappers (LegacyTextField, LegacyButton, etc.)
   ├─ emi/emicalculator/         ← EMI calculator implementations
   ├─ emi/emifixedvsreducing/    ← Fixed vs reducing calculator
   ├─ banking/fd/                ← FD/RD calculators
   ├─ banking/ppf/               ← PPF calculator
   ├─ gst/                       ← GST calculator
   ├─ roomdb/                    ← Room database entities/Dao
   ├─ searchhistory/             ← Search history adapters
   ├─ dashboard/                 ← Dashboard screen
   └─ home/                      ← Main activity
   Key Configuration Files:
- MIGRATION_PLAN.md - 556 lines detailing 11-phase migration roadmap
- gradle.properties - Build settings (JDK 17, Gradle 9.6, caching enabled)
- local.properties - SDK path configuration
- settings.gradle.kts - Project inclusion of :app
- build.gradle.kts - Build script (Kotlin DSL)
- docs/baseline/ - Baseline screenshots and golden reference values
- app/src/main/assets/ - JSON calculator specs and dashboard config
2. "Model" and "Phase" References
   Model Classes (16 entities in com.financialcalculator.model):
   Class	Purpose
   ConfigModel	Configuration: banner placement ID, play store version, show ads flag
   HomePageModel	Dashboard carousel items: cmpData, cmpId, cmpPos, cmpType, firebaseId
   CalculatorEntity	Main calculator: input/output lists, version, firebaseId, iconUrl
   GenericOutputEntity	Calculation results: formulae, outKey, outMsg, currency
   DetailsEntity	EMI amortization yearly details: year, month, principal, interest, balance
   YearsDetailsEntity	Container for yearly DetailsEntity list + visibility state
   FDEntity	FD entity: month, year, balance, interest
   FDDetailsEntity	Container for FD entities
   EditTextEntity	Input field: inpType, length, isFocus, regex, data
   InputTypeEntity	Input type metadata: calId, data, firebaseId, inpId, key, title, type
   CarouselEntity	Dashboard carousel item (extends BaseCompModel): cmpId, firebaseId, redUrl, webUrl, interval
   DashboardEntity	Dashboard item: id, name, icon
   DashBoardRowEntity	Dashboard row entity
   MoreInfoEntity	Room DB entity (tableName="MoreInfoEntity") with answer, calId, firebaseId, ques
   GenericViewTypeModel	View type model for calculator inputs (implements Serializable)
   BaseCompModel	Empty base class for CarouselEntity
   Phase/Configuration Concepts:
   The project uses model-driven configuration through JSON specs:
- cmpType in HomePageModel selects dashboard layout type (1-7):
- Type 1: Carousel with dot indicators, 3s auto-scroll
- Type 2: Inline WebView
- Type 3: Auto-scroll pager, no indicators
- Type 4: Section header (marquee)
- Type 5: 3-column calculator grid
- Type 6: Vertical spacer (cmpData = px margin)
- Type 7: Ad slot
- Model selection via JSON specs in app/src/main/assets/:
- 10 calculator JSON files (emi.json, fd.json, rd.json, ppf.json, gst.json, vat.json, sip.json, sip_goal.json, lumpsum.json, compare_loan.json)
- Each maps to a specific calculator activity (e.g., emi.json → EmiCalculatorActivity)
- SplashActivity fetches ConfigModel via HTTP service to determine configuration (ads, version, etc.)
  Phase References (from MIGRATION_PLAN.md):
  The project has a 11-phase migration roadmap:
  Phase	Focus	Exit Criteria
  0	Unbreak build & foundation	assembleDebug + bundleRelease succeed; lint passes; baseline screenshots captured; keystore removed from git
  1	Compose design system + pixel parity	Every ui/components primitive has passing token/geometry tests; eyeballed against Phase 0 baseline
  2	Domain core + goldens	100% domain code covered; all golden tests green; sealed interfaces for CalculatorSpec, InputSpec, OutputSpec
  3	Data layer	All endpoints covered by tests; repository layer has no Android UI deps; Room via KSP
  4	Navigation	Every former activity reachable via type-safe routes; no startActivity for calculator navigation remains
  5	Dashboard screen	Dashboard screenshot matches Phase 0 baseline within threshold
  6	Universal calculator screen	All 3 live JSON calculators (NPS, Atal, CAGR) render correctly including dividers
  7	Convert hard-coded calculators to JSON	Every calculator screen renders identically; zero calculator activities remain; all layouts except nav_header_main deleted
  8	Search history	History lists/restores/deletes work; existing rows still visible; bug #14 fixed (route from stored type, not hard-coded to FD)
  9	Splash, About, WebView	All three match baseline; WebView content loads and back-navigation works
  10	Quality gate	CI green on lint + unit + screenshot + UI tests; detekt + ktlint; R8 full mode
  11	Release plumbing	GitHub Actions with JDK 21; signing via secrets; nothing sensitive in repo
  Additional Notes:
- Code comments reference phases: "Phase 2 replaces both calls", "Phase 5 does the full Compose version" - these are migration plan references
- Security issue: app/finance.jks committed with password finance in plaintext - Phase 0 removes this
- Build issue: com.synnapps:carouselview:0.1.5 from JCenter (sunset) - Phase 0 replaces with androidx.viewpager2
- 14 hard-coded calculator screens to be converted to JSON specs (Phase 7): EMI, Compare Loan, Flat vs Reducing, SIP, SIP Goal, LumpSum, FD, RD, PPF, GST, VAT, Create/View Loan Profile, Home Loan Eligibility
- Total estimated effort: ~28-37 working days across all phases
- Golden reference values captured in docs/baseline/GOLDEN_VALUES.md - these are the source of truth for differential tests
  The project is structured as a comprehensive migration from legacy Java/XML to modern Kotlin/Compose, with model-driven configuration via JSON specs enabling a single universal calculator screen to replace 14 separate activities.
  →Read .
+ Thought: 278ms
  Summary
  I've explored the FinancialCalculator codebase and here are the key findings:
  Project Structure
- Android Studio project modernizing from 100% Java/XML to Kotlin + Jetpack Compose
- Single :app module with package com.financialcalculator
- 98 Java files (~16,237 LOC), 48 XML layouts (~7,362 LOC)
- Build: Gradle 8.7, AGP 8.5.2, compileSdk 34, targetSdk 35, minSdk 21
  Model Classes (16 entities in com.financialcalculator.model)
  Model	Purpose
  ConfigModel	Config: banner placement, play store version, show ads
  HomePageModel	Dashboard items: cmpData, cmpId, cmpPos, cmpType, firebaseId
  CalculatorEntity	Main calculator: inputs/outputs, version, redUrl
  GenericOutputEntity	Calculation results: formulae, outKey, outMsg, currency
  DetailsEntity/YearsDetailsEntity	EMI amortization data
  FDEntity/FDDetailsEntity	Fixed Deposit data
  EditTextEntity/InputTypeEntity	Input field metadata
  CarouselEntity	Dashboard carousel item
  DashboardEntity	Dashboard navigation item
  MoreInfoEntity	Room DB entity (never wired)
  Model/Phase Concepts
- cmpType (1-7) in HomePageModel selects dashboard layout type
- JSON calculator specs in assets/ drive the universal calculator (Phase 7 goal)
- SplashActivity fetches ConfigModel via HTTP to determine ads/config
- Phase references in code comments: "Phase 2 replaces both calls", "Phase 5 does full Compose version"
  Phase Migration Roadmap (11 phases from MIGRATION_PLAN.md)
- Phase 0: Unbreak build (remove JCenter carouselview, security fixes, baseline screenshots)
- Phase 1: Compose design system with pixel parity
- Phase 2: Domain core + sealed interfaces + golden tests
- Phase 3: Data layer (Room KSP, Retrofit 3, kotlinx.serialization)
- Phase 4: Navigation Compose with type-safe routes
- Phase 5: Dashboard screen (LazyColumn over DashboardNode)
- Phase 6: Universal calculator screen (single ViewModel replaces 14 activities)
- Phase 7: Convert 14 hard-coded calculators to JSON specs (core deliverable)
- Phase 8: Search history (Room-backed, fix bug #14)
- Phase 9: Splash, About, WebView
- Phase 10: Quality gate (lint, detekt/ktlint, R8, accessibility)
- Phase 11: Release plumbing (GitHub Actions, signing via secrets)
  Key Configuration Files
- MIGRATION_PLAN.md - 556-line roadmap with exit criteria per phase
- gradle.properties - Build config (JDK 17, caching, debug/release hosts)
- local.properties - SDK path
- docs/baseline/GOLDEN_VALUES.md - Golden reference values for differential testing
- app/src/main/assets/dashboard.json - Dashboard config
- app/src/main/assets/calculators/*.json - 10 calculator JSON specs