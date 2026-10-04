# GitHub Pages JSON

Ready-to-paste API documents for **https://ranjan-rajeev.github.io/Finanace-Calculator-Web/json/**.

Copy the whole `json/` folder into the web repository root, keeping the `v1/` and `v2/`
sub-folders, then enable GitHub Pages on that repository.

```
github-pages/json/
├── v1/     legacy schema   -> already-published APKs
└── v2/     native schema   -> the rewritten APK
```

## What the app requests

`Constants.BASE_URL` is the version-agnostic root; the Retrofit paths add the version.

| Request | File |
|---|---|
| `BASE_URL + "v2/dashboard.json"` | `v2/dashboard.json` |
| `BASE_URL + "v2/config.json"` | `v2/config.json` |
| `BASE_URL + "v2/calculators/EMI.json"` | `v2/calculators/EMI.json` |

The app is v2-only. There is no legacy parsing or migration path in the code.

## v2 — consumed by the rewritten APK

| File | Contents |
|---|---|
| `config.json` | `AppConfig` — ads, in-app review, play store version |
| `dashboard.json` | `DashboardResponse` — banner, section headers, calculator grids, spacer, ad |
| `calculators/*.json` | One `CalculatorConfig` per calculator |

Calculators generated: `EMI`, `COMPARE_LOAN`, `FLAT_VS_REDUCING`, `SIP`, `GOAL_SIP`,
`LUMPSUM_SIP`, `FD`, `RD`, `PPF`, `GST`, `VAT`, `NPS`, `ATAL`, `CAGR`.

Every `inputFields[].key` and `outputConfig.summary[].key` is taken from the matching
Kotlin engine, so changing an engine's keys means regenerating these files.

## v1 — for the already-published APK

Same calculators, expressed in the retired Java schema: `dashboard.json` is an array of
`HomePageModel` rows keyed by `cmpType`, and each calculator carries `input`/`output` as
JSON-encoded strings.

The old APK hard-codes `/Finanace-Calculator-Web/json/...` with no version segment, so it
reads the **unversioned** files that are already live. `v1/` is here so the v1 documents
have an explicit home and can be pointed at deliberately; the nested `v1/nps/`,
`v1/atal/` and `v1/cagr/` folders also reproduce that APK's original calculator paths.

## Regenerating

```bash
python3 tools/generate_hosting_json.py
```

Edit `tools/generate_hosting_json.py`, not the generated files. The generator is the
source of truth and `:core:testDebugUnitTest` fails if the output stops matching the
Kotlin models.

## Before going live

1. Replace the `BANNER_PLACEMENT_ID` placeholder in `tools/generate_hosting_json.py`
   with the real AdMob unit, or set `showAds` to `false`.
2. Upload banner images and calculator icons, then update the URLs. `ICONS` in the
   generator is currently empty for every calculator except `EMI`.
3. Bump `playStoreVersion` when a release ships so older APKs can be prompted to update.
