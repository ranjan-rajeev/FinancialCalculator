# Golden Reference Values — captured from the pre-migration Java/XML app

Captured on 2026-10-02, branch `migration/kotlin-compose`, from
`app-debug.apk` built at commit `aaa80e9` with only the dead
`com.synnapps:carouselview` dependency removed (the app could not build before
that — JCenter is sunset).


---

## 1. EMI Calculator (`EmiCalculatorActivity`)

Inputs: Principal `1000000`, Rate of Interest `10`, Tenure `10` Years.

| Output | Value |
|---|---|
| EMI (Per Month) | `13,215.07₹` |
| Total Payment (Principal + Interest) | `15,85,808.84₹` |
| Total Principal | `10,00,000.00₹` · `63.06%` |
| Total Interest Payable | `5,85,808.84₹` · `36.94%` |

### EMI Projection (note: these use `.0`, not `.00` — different formatter path)

| Year | Principal | Interest | Balance |
|---|---|---|---|
| 2026 | 14768.0 | 24878.0 | 985232.0 |
| 2027 | 62888.0 | 95693.0 | 922344.0 |
| 2028 | 69473.0 | 89107.0 | 852871.0 |
| 2029 | 76748.0 | 81833.0 | 776123.0 |
| 2030 | 84785.0 | 73796.0 | 691338.0 |
| 2031 | 93663.0 | 64918.0 | 597675.0 |
| 2032 | 103471.0 | 55110.0 | 494205.0 |
| 2033 | 114305.0 | 44276.0 | 379899.0 |
| 2034 | 126274.0 | 32306.0 | 253625.0 |

> Parity traps here:
> - Summary rows use `Util.getCommaSeparated` → 2 decimals (`13,215.07`).
> - Schedule rows use `Util.getNumberFormatted` → 1 decimal (`14768.0`).
> - Currency is appended with **no space**: `13,215.07₹`.
> - The unit is `₹` (U+20B9) and the numbers use Indian lakh/crore grouping.

---

## 2. National Pension System (`GenericCalculatorActivity`, server JSON)

Inputs: Investment `5000` / frequency `Monthly`, Age `30`,
Expected rate of return `10`.

| Output | Value |
|---|---|
| Total Maturity Amount | `1,13,96,627 ₹` |
| Amount Invested | `18,00,000 ₹` |
| Interest Earned | `95,96,627 ₹` |
| Withdrawable Amount | `68,37,976 ₹` |
| Annuity Amount | `45,58,651 ₹` |

### "Did you know ?"

> At Retirement /Exit , you can withdraw a maximum of ₹Rs. **6837976** /-
> (i.e 60% of the total maturity amount ).

> Parity trap: the template value renders as raw `6837976` — **no comma
> grouping**. `Util.evaluateString` resolves `$i` via
> `bigDecimals.get(k).setScale(0, 0).toPlainString()` and never applies
> `getCommaSeparated`. The same applies to `@ expr @`, which is always forced
> to `setScale(0)`.

### Input order observed (NPS `npscalculator.json`)

1. `type 4` EditText+Spinner, key `"pf"` → `p` = typed amount, `f` = spinner
2. `type 1` EditText, key `n` — "Your age (18-60 Years)"
3. `type 1` EditText, key `r` — "Expected rate of return(8-15%)"
4. `type 5` Button, key `a`, action `1` — "Calculate"

Output chaining for NPS: `x` = total maturity → `z = x - y` (interest) →
and `$i`, `@ 0.01 * j * r * 0.083334 @` in the more-info templates.

---

## 3. Screenshots

| File | Screen |
|---|---|
| `01-home.png` | Dashboard (live network data) |
| `02-drawer.png` | Navigation drawer |
| `03-about.png` | About Us |
| `04-emi.png` | EMI Calculator (empty form) |
| `05-emi-results.png` | EMI Calculator (populated + results + projection) |
| `06-nps.png` | Generic NPS calculator (empty form) |
| `07-nps-results.png` | Generic NPS calculator (results + "Did you know?") |

Device: Pixel 10 Pro XL AVD, Android 17 (API 37), 1344 px wide.

---

## 4. Formatting rules that must not change

1. **Indian grouping** — `##,##,###` (`12,34,567` not `1,234,567`).
2. **Two decimals when the input contains a `.`**, otherwise trailing zeros are
   stripped. This is `Util.getCommaSeparated`'s `.00` vs `.##` split.
3. **Input string > 12 characters** switches to the `BigDecimal` overload. All
   four formatters produce identical output, so the branch is currently a no-op
   — preserve it anyway.
4. **`NumberFormatException` returns `""`** (blank cell, not a crash).
5. Currency suffix is appended **with a space** in the generic engine
   (`1,13,96,627 ₹`) but **without** in the EMI summary (`13,215.07₹`).