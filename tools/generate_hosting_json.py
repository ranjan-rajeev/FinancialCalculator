#!/usr/bin/env python3
"""
Generates the GitHub Pages JSON for the Financial Calculator web repo.

Emits two complete, ready-to-paste trees:

    github-pages/json/v2/   native schema, consumed by the rewritten APK
    github-pages/json/v1/   legacy schema, consumed by already-published APKs

Run from the repo root:

    python3 tools/generate_hosting_json.py

Every calculator's `inputFields` keys and `outputConfig.summary` keys are taken
from the Kotlin engines in core/.../domain/engine, so the hosted documents drive
real calculations rather than rendering empty screens.
"""

import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "github-pages", "json")

VERSION = 2


# --------------------------------------------------------------------------
# v2 building blocks
# --------------------------------------------------------------------------

def edit_text(key, label, order, data_type="DECIMAL", default=None,
              hint=None, formatter="NONE", required=True, minimum=None, maximum=None):
    field = {
        "type": "EDIT_TEXT",
        "key": key,
        "label": label,
        "inputType": data_type,
        "formatter": formatter,
        "order": order,
    }
    if hint:
        field["hint"] = hint
    if default is not None:
        field["defaultValue"] = str(default)
    if required or minimum is not None or maximum is not None:
        field["validation"] = {
            "required": required,
            **({"min": minimum} if minimum is not None else {}),
            **({"max": maximum} if maximum is not None else {}),
        }
    return field


def spinner(key, label, order, options, default=None):
    return {
        "type": "SPINNER",
        "key": key,
        "label": label,
        "options": [{"label": l, "value": v} for l, v in options],
        **({"defaultValue": default} if default else {}),
        "order": order,
    }


def calculate_button(order):
    return {
        "type": "BUTTON",
        "key": "calculate",
        "label": "Calculate",
        "action": "CALCULATE",
        "style": "PRIMARY",
        "order": order,
    }


def summary(key, label, order, stype="KEY_VALUE", formatter="NONE"):
    return {"key": key, "label": label, "type": stype, "formatter": formatter, "order": order}


def output_config(summary_items, breakdown=None, charts=None, formulas=None):
    return {
        "summary": summary_items,
        "breakdown": breakdown,
        "charts": charts or [],
        "formulas": formulas or [],
    }


TENURE_OPTIONS = [("Years", "YEARS"), ("Months", "MONTHS")]
FREQUENCY_OPTIONS = [("Monthly", "MONTHLY"), ("Quarterly", "QUARTELY"), ("Yearly", "YEARLY")]
COMPOUNDING_OPTIONS = [("Monthly", "MONTHLY"), ("Quarterly", "QUARTELY"), ("Yearly", "YEARLY")]
GST_TYPE_OPTIONS = [("Exclusive (add GST)", "EXCLUSIVE"), ("Inclusive (remove GST)", "INCLUSIVE")]
INTERSTATE_OPTIONS = [("Intra-state (CGST + SGST)", "INTRA"), ("Inter-state (IGST)", "INTER")]

MORE_INFO = {
    "EMI": [
        {"question": "How is my EMI calculated?",
         "answer": "EMI = P x r x (1 + r)^n / ((1 + r)^n - 1), where P is the loan amount, "
                   "r is the monthly interest rate and n is the number of monthly instalments."},
        {"question": "Does the tenure type change my EMI?",
         "answer": "Yes. Choosing Months keeps the entered value as-is, while Years is converted "
                   "to months before the EMI is computed."},
    ],
    "SIP": [
        {"question": "What happens if I change the frequency?",
         "answer": "The instalment amount is multiplied by the frequency, so a quarterly SIP "
                   "invests three months of contributions at the start of each quarter."},
        {"question": "Are returns guaranteed?",
         "answer": "No. SIP returns depend on market performance; the projection assumes the "
                   "entered rate holds for the full tenure."},
    ],
    "FD": [
        {"question": "How is FD maturity calculated?",
         "answer": "Maturity = Principal x (1 + rate / frequency)^(years x frequency), with the "
                   "compounding frequency taken from the selected option."},
    ],
    "RD": [
        {"question": "What is the total I deposit in RD?",
         "answer": "Total invested equals the monthly deposit multiplied by the number of months "
                   "in the chosen tenure."},
    ],
    "PPF": [
        {"question": "When is PPF interest credited?",
         "answer": "PPF interest is credited annually at the end of each financial year on the "
                   "running balance."},
    ],
    "GST": [
        {"question": "What is the difference between CGST, SGST and IGST?",
         "answer": "Intra-state supplies are split into CGST and SGST, half each. Inter-state "
                   "supplies are charged a single IGST at the full rate."},
        {"question": "What does exclusive vs inclusive mean?",
         "answer": "Exclusive adds GST on top of the entered amount. Inclusive treats the entered "
                   "amount as the final price and extracts GST from it."},
    ],
    "VAT": [
        {"question": "How is VAT different from GST?",
         "answer": "VAT is charged at a single rate with no CGST/SGST split, and applies to "
                   "goods and services in VAT-registered regions."},
    ],
    "NPS": [
        {"question": "How much can I withdraw as a lump sum?",
         "answer": "Under the current rules, up to 60 percent of the corpus is available as a "
                   "lump sum and the remaining 40 percent buys the pension annuity."},
        {"question": "Is the pension guaranteed?",
         "answer": "The pension is not guaranteed. It is allocated from the corpus and the "
                   "monthly amount varies with annuity and pension purchase rates."},
    ],
    "ATAL": [
        {"question": "What is APY in Atal Pension Yojana?",
         "answer": "The pension you receive is based on the age at which you join and the pension "
                   "you choose, paid monthly after you turn 60."},
    ],
    "CAGR": [
        {"question": "What does CAGR tell me?",
         "answer": "CAGR is the constant yearly rate that takes an investment from its beginning "
                   "value to its ending value over the given number of years."},
    ],
}


# --------------------------------------------------------------------------
# Calculator catalogue
# --------------------------------------------------------------------------

def calculators():
    return [
        {
            "id": "EMI", "name": "EMI Calculator", "category": "LOAN",
            "engine": "EMI_CALCULATOR",
            "inputs": [
                edit_text("principal", "Loan amount", 1, formatter="CURRENCY_INR", minimum=1, hint="e.g. 500000"),
                edit_text("rate", "Interest rate (% p.a.)", 2, minimum=0, maximum=100, hint="e.g. 8.5"),
                edit_text("tenure", "Tenure", 3, minimum=1),
                spinner("tenureType", "Tenure type", 4, TENURE_OPTIONS, "YEARS"),
                calculate_button(5),
            ],
            "output": output_config([
                summary("emi", "Monthly EMI", 1, formatter="CURRENCY_INR"),
                summary("totalInterest", "Total interest", 2, formatter="CURRENCY_INR"),
                summary("totalPayable", "Total payable", 3, formatter="CURRENCY_INR"),
                summary("interestPercentage", "Interest share", 4, formatter="PERCENTAGE"),
                summary("principalPercentage", "Principal share", 5, formatter="PERCENTAGE"),
            ]),
        },
        {
            "id": "COMPARE_LOAN", "name": "Compare Loan", "category": "LOAN",
            "engine": "COMPARE_LOAN",
            "inputs": [
                edit_text("principal", "Loan amount", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("rate", "Interest rate (% p.a.)", 2, minimum=0, maximum=100),
                edit_text("tenure", "Tenure", 3, minimum=1),
                spinner("tenureType", "Tenure type", 4, TENURE_OPTIONS, "YEARS"),
                calculate_button(5),
            ],
            "output": output_config([
                summary("savings", "Savings", 1, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "FLAT_VS_REDUCING", "name": "Flat vs Reducing", "category": "LOAN",
            "engine": "FLAT_VS_REDUCING",
            "inputs": [
                edit_text("principal", "Loan amount", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("rate", "Interest rate (% p.a.)", 2, minimum=0, maximum=100),
                edit_text("tenure", "Tenure", 3, minimum=1),
                spinner("tenureType", "Tenure type", 4, TENURE_OPTIONS, "YEARS"),
                calculate_button(5),
            ],
            "output": output_config([
                summary("flatEmi", "Flat EMI", 1, formatter="CURRENCY_INR"),
                summary("flatTotalInterest", "Flat total interest", 2, formatter="CURRENCY_INR"),
                summary("flatTotalPayable", "Flat total payable", 3, formatter="CURRENCY_INR"),
                summary("reducingEmi", "Reducing EMI", 4, formatter="CURRENCY_INR"),
                summary("reducingTotalInterest", "Reducing total interest", 5, formatter="CURRENCY_INR"),
                summary("reducingTotalPayable", "Reducing total payable", 6, formatter="CURRENCY_INR"),
                summary("emiDifference", "EMI difference", 7, formatter="CURRENCY_INR"),
                summary("interestDifference", "Interest difference", 8, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "SIP", "name": "SIP Calculator", "category": "SIP",
            "engine": "SIP_CALCULATOR",
            "inputs": [
                edit_text("monthlyInvestment", "Monthly investment", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("rate", "Expected return (% p.a.)", 2, minimum=0, maximum=100),
                edit_text("tenure", "Tenure", 3, minimum=1),
                spinner("tenureType", "Tenure type", 4, TENURE_OPTIONS, "YEARS"),
                spinner("frequency", "Investment frequency", 5, FREQUENCY_OPTIONS, "MONTHLY"),
                calculate_button(6),
            ],
            "output": output_config([
                summary("totalInvested", "Total invested", 1, formatter="CURRENCY_INR"),
                summary("totalReturns", "Total returns", 2, formatter="CURRENCY_INR"),
                summary("maturity", "Maturity value", 3, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "GOAL_SIP", "name": "Goal SIP Calculator", "category": "SIP",
            "engine": "SIP_CALCULATOR",
            "inputs": [
                edit_text("goalAmount", "Goal amount", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("rate", "Expected return (% p.a.)", 2, minimum=0, maximum=100),
                edit_text("tenure", "Tenure", 3, minimum=1),
                spinner("tenureType", "Tenure type", 4, TENURE_OPTIONS, "YEARS"),
                spinner("frequency", "Investment frequency", 5, FREQUENCY_OPTIONS, "MONTHLY"),
                calculate_button(6),
            ],
            "output": output_config([
                summary("requiredMonthly", "Required monthly investment", 1, formatter="CURRENCY_INR"),
                summary("totalInvested", "Total invested", 2, formatter="CURRENCY_INR"),
                summary("totalReturns", "Total returns", 3, formatter="CURRENCY_INR"),
                summary("goalAmount", "Goal amount", 4, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "LUMPSUM_SIP", "name": "Lumpsum Calculator", "category": "SIP",
            "engine": "SIP_CALCULATOR",
            "inputs": [
                edit_text("lumpsumAmount", "Lumpsum amount", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("rate", "Expected return (% p.a.)", 2, minimum=0, maximum=100),
                edit_text("tenure", "Tenure", 3, minimum=1),
                spinner("tenureType", "Tenure type", 4, TENURE_OPTIONS, "YEARS"),
                spinner("frequency", "Investment frequency", 5, FREQUENCY_OPTIONS, "MONTHLY"),
                calculate_button(6),
            ],
            "output": output_config([
                summary("lumpsumAmount", "Invested amount", 1, formatter="CURRENCY_INR"),
                summary("totalReturns", "Total returns", 2, formatter="CURRENCY_INR"),
                summary("maturity", "Maturity value", 3, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "FD", "name": "FD Calculator", "category": "BANKING",
            "engine": "FD_CALCULATOR",
            "inputs": [
                edit_text("principal", "Deposit amount", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("rate", "Interest rate (% p.a.)", 2, minimum=0, maximum=100),
                edit_text("year", "Years", 3, minimum=0, default=5),
                edit_text("month", "Months", 4, minimum=0, maximum=11, default=0),
                edit_text("day", "Days", 5, minimum=0, maximum=30, default=0),
                spinner("compoundingType", "Compounding", 6, COMPOUNDING_OPTIONS, "QUARTELY"),
                calculate_button(7),
            ],
            "output": output_config([
                summary("principal", "Principal", 1, formatter="CURRENCY_INR"),
                summary("totalInterest", "Interest earned", 2, formatter="CURRENCY_INR"),
                summary("maturity", "Maturity amount", 3, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "RD", "name": "RD Calculator", "category": "BANKING",
            "engine": "RD_CALCULATOR",
            "inputs": [
                edit_text("monthlyDeposit", "Monthly deposit", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("rate", "Interest rate (% p.a.)", 2, minimum=0, maximum=100),
                edit_text("tenure", "Tenure", 3, minimum=1),
                spinner("tenureType", "Tenure type", 4, TENURE_OPTIONS, "YEARS"),
                calculate_button(5),
            ],
            "output": output_config([
                summary("totalInvested", "Total invested", 1, formatter="CURRENCY_INR"),
                summary("totalInterest", "Interest earned", 2, formatter="CURRENCY_INR"),
                summary("maturity", "Maturity amount", 3, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "PPF", "name": "PPF Calculator", "category": "BANKING",
            "engine": "PPF_CALCULATOR",
            "inputs": [
                edit_text("yearlyDeposit", "Yearly deposit", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("rate", "Interest rate (% p.a.)", 2, minimum=0, maximum=100),
                edit_text("tenure", "Tenure", 3, minimum=1, maximum=15, default=15),
                calculate_button(4),
            ],
            "output": output_config([
                summary("totalInvested", "Total invested", 1, formatter="CURRENCY_INR"),
                summary("totalInterest", "Interest earned", 2, formatter="CURRENCY_INR"),
                summary("maturity", "Maturity amount", 3, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "GST", "name": "GST Calculator", "category": "TAX",
            "engine": "GST_CALCULATOR",
            "inputs": [
                edit_text("amount", "Amount", 1, formatter="CURRENCY_INR", minimum=0),
                edit_text("gstRate", "GST rate (%)", 2, minimum=0, maximum=100, default=18),
                spinner("calculationType", "GST calculation", 3, GST_TYPE_OPTIONS, "EXCLUSIVE"),
                spinner("isInterstate", "Supply type", 4, INTERSTATE_OPTIONS, "INTRA"),
                calculate_button(5),
            ],
            "output": output_config([
                summary("baseAmount", "Base amount", 1, formatter="CURRENCY_INR"),
                summary("gstRate", "GST rate", 2, formatter="PERCENTAGE"),
                summary("gstAmount", "GST amount", 3, formatter="CURRENCY_INR"),
                summary("totalAmount", "Total amount", 4, formatter="CURRENCY_INR"),
                summary("cgst", "CGST", 5, formatter="CURRENCY_INR"),
                summary("sgst", "SGST", 6, formatter="CURRENCY_INR"),
                summary("igst", "IGST", 7, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "VAT", "name": "VAT Calculator", "category": "TAX",
            "engine": "VAT_CALCULATOR",
            "inputs": [
                edit_text("amount", "Amount", 1, formatter="CURRENCY_INR", minimum=0),
                edit_text("vatRate", "VAT rate (%)", 2, minimum=0, maximum=100, default=5),
                spinner("calculationType", "VAT calculation", 3, GST_TYPE_OPTIONS, "EXCLUSIVE"),
                calculate_button(4),
            ],
            "output": output_config([
                summary("baseAmount", "Base amount", 1, formatter="CURRENCY_INR"),
                summary("vatRate", "VAT rate", 2, formatter="PERCENTAGE"),
                summary("vatAmount", "VAT amount", 3, formatter="CURRENCY_INR"),
                summary("totalAmount", "Total amount", 4, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "NPS", "name": "NPS Calculator", "category": "RETIREMENT",
            "engine": "GENERIC_FORMULA",
            "inputs": [
                edit_text("age", "Current age", 1, data_type="NUMBER", minimum=18, maximum=59, default=30),
                edit_text("retirementAge", "Retirement age", 2, data_type="NUMBER", minimum=40, maximum=70, default=60),
                edit_text("monthlyContribution", "Monthly contribution", 3, formatter="CURRENCY_INR", minimum=0, default=5000),
                edit_text("expectedReturn", "Expected return (% p.a.)", 4, minimum=0, maximum=100, default=10),
                edit_text("annuityPercentage", "Lump sum %", 5, minimum=0, maximum=100, default=40),
                edit_text("annuityRate", "Annuity rate (% p.a.)", 6, minimum=0, maximum=100, default=6),
                calculate_button(7),
            ],
            "output": output_config([
                summary("totalCorpus", "Total corpus", 1, formatter="CURRENCY_INR"),
                summary("maturityValue", "Maturity value", 2, formatter="CURRENCY_INR"),
                summary("pensionAmount", "Monthly pension", 3, formatter="CURRENCY_INR"),
                summary("lumpSum", "Lump sum withdrawal", 4, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "ATAL", "name": "Atal Pension Calculator", "category": "RETIREMENT",
            "engine": "GENERIC_FORMULA",
            "inputs": [
                edit_text("age", "Current age", 1, data_type="NUMBER", minimum=18, maximum=59, default=30),
                edit_text("retirementAge", "Retirement age", 2, data_type="NUMBER", minimum=40, maximum=70, default=60),
                edit_text("monthlyContribution", "Monthly contribution", 3, formatter="CURRENCY_INR", minimum=0, default=5000),
                edit_text("expectedReturn", "Expected return (% p.a.)", 4, minimum=0, maximum=100, default=10),
                edit_text("pensionRate", "Pension rate (% p.a.)", 5, minimum=0, maximum=100, default=6),
                calculate_button(6),
            ],
            "output": output_config([
                summary("totalContribution", "Total contribution", 1, formatter="CURRENCY_INR"),
                summary("totalCorpus", "Total corpus", 2, formatter="CURRENCY_INR"),
                summary("maturityValue", "Maturity value", 3, formatter="CURRENCY_INR"),
                summary("monthlyPension", "Monthly pension", 4, formatter="CURRENCY_INR"),
            ]),
        },
        {
            "id": "CAGR", "name": "CAGR Calculator", "category": "INVESTMENT",
            "engine": "GENERIC_FORMULA",
            "inputs": [
                edit_text("beginningValue", "Beginning value", 1, formatter="CURRENCY_INR", minimum=1),
                edit_text("endingValue", "Ending value", 2, formatter="CURRENCY_INR", minimum=0),
                edit_text("tenure", "Number of years", 3, minimum=1, default=5),
                calculate_button(4),
            ],
            "output": output_config([
                summary("cagrPercent", "CAGR", 1, formatter="PERCENTAGE"),
                summary("endingValue", "Ending value", 2, formatter="CURRENCY_INR"),
            ]),
        },
    ]


DASHBOARD_ORDER = [
    ("EMI Calculators", ["EMI", "COMPARE_LOAN", "FLAT_VS_REDUCING"]),
    ("Mutual Funds & SIP", ["SIP", "GOAL_SIP", "LUMPSUM_SIP"]),
    ("Banking & Investments", ["FD", "RD", "PPF", "CAGR"]),
    ("Tax", ["GST", "VAT"]),
    ("Retirement", ["NPS", "ATAL"]),
]

ICONS = {
    "EMI": "https://raw.githubusercontent.com/ranjan-rajeev/Finanace-Calculator-Web/main/icons/emi.png",
}


def icon_for(calc_id):
    return ICONS.get(calc_id, "")


def app_config():
    return {
        "bannerPlacementId": "ca-app-pub-0000000000000000/0000000000",
        "showAds": True,
        "inAppReviewEnabled": True,
        "inAppReviewCooldownDays": 2,
        "playStoreVersion": 1,
        "forceUpdateVersion": 0,
        "maintenanceMode": False,
        "maintenanceMessage": None,
    }


# --------------------------------------------------------------------------
# v2 writers
# --------------------------------------------------------------------------

def write_v2_config():
    path = os.path.join(OUT, "v2", "config.json")
    write(path, app_config())


def write_v2_calculator(calc):
    doc = {
        "id": calc["id"],
        "name": calc["name"],
        "type": "CUSTOM",
        "version": VERSION,
        "category": calc["category"],
        "engine": calc["engine"],
        "inputFields": calc["inputs"],
        "outputConfig": calc["output"],
        "moreInfo": MORE_INFO.get(calc["id"], []),
    }
    write(os.path.join(OUT, "v2", "calculators", f"{calc['id']}.json"), doc)


def write_v2_dashboard():
    components = []

    components.append({
        "type": "BANNER_CAROUSEL",
        "id": "banner",
        "position": 1,
        "data": {
            "images": [
                {
                    "id": "banner_1",
                    "imageUrl": "https://raw.githubusercontent.com/ranjan-rajeev/Finanace-Calculator-Web/main/assets/banner-1.png",
                    "actionUrl": "https://ranjan-rajeev.github.io/Finanace-Calculator-Web/",
                    "actionType": "WEB",
                },
            ],
            "autoPlay": True,
            "intervalMs": 5000,
        },
    })

    position = 2
    by_id = {c["id"]: c for c in calculators()}
    for title, ids in DASHBOARD_ORDER:
        components.append({
            "type": "SECTION_HEADER",
            "id": f"header_{title.lower().replace(' ', '_').replace('&', 'and')}",
            "position": position,
            "data": {"title": title},
        })
        position += 1
        components.append({
            "type": "CALCULATOR_GRID",
            "id": f"grid_{title.lower().replace(' ', '_').replace('&', 'and')}",
            "position": position,
            "data": {
                "columns": 3,
                "calculators": [
                    {
                        "id": cid,
                        "name": by_id[cid]["name"],
                        "iconUrl": icon_for(cid),
                        "calculatorType": by_id[cid]["category"],
                        "version": VERSION,
                    }
                    for cid in ids
                ],
            },
        })
        position += 1

    components.append({
        "type": "SPACER",
        "id": "spacer_bottom",
        "position": position,
        "data": {"heightDp": 24},
    })
    position += 1

    components.append({
        "type": "AD_PLACEHOLDER",
        "id": "ad_bottom",
        "position": position,
        "data": {"placementId": app_config()["bannerPlacementId"], "heightDp": 50},
    })

    write(os.path.join(OUT, "v2", "dashboard.json"), {
        "version": f"v{VERSION}",
        "config": app_config(),
        "components": components,
    })


# --------------------------------------------------------------------------
# v1 (legacy schema) writers
#
# Type codes are the retired Java constants:
#   cmpType        1 carousel, 2 webview, 3 view pager, 4 header,
#                  5 calculator list, 6 spacer, 7 ad
#   input type     1 edit text, 2 date, 3 spinner, 4 edit text + spinner,
#                  5 button, 7 spinner title
#   input inpType  1 number, 2 decimal, 3 text
#   output type    1 formula, 2 expression with formula, 3 graph, 4 divider
# --------------------------------------------------------------------------

LEGACY_INPUT_TYPE = {"EDIT_TEXT": 1, "DATE_PICKER": 2, "SPINNER": 3, "BUTTON": 5}
LEGACY_INP_TYPE = {"NUMBER": 1, "DECIMAL": 2, "TEXT": 3}
LEGACY_RED_URL = {
    "EMI": "EmiCalculatorActivity",
    "COMPARE_LOAN": "EmiCompareActivity",
    "FLAT_VS_REDUCING": "FixedVsReducingActivity",
    "SIP": "SIPCalculatorActivity",
    "GOAL_SIP": "SIPGoalCalculatorActivity",
    "LUMPSUM_SIP": "LumpSumpSipActivity",
    "FD": "FDCalculatorActivity",
    "RD": "RDCalculatorActivity",
    "PPF": "PPFCalculatotActivity",
    "GST": "GstCalculatorActivity",
    "VAT": "VatCalculatorActivity",
    "NPS": "GenericCalculatorActivity",
    "ATAL": "GenericCalculatorActivity",
    "CAGR": "GenericCalculatorActivity",
}


def legacy_input_entries(calc):
    entries = []
    spinner_index = 0
    for field in calc["inputs"]:
        ftype = field["type"]
        order = field["order"]
        if ftype == "EDIT_TEXT":
            entries.append({
                "calId": order,
                "data": json.dumps({
                    "inpType": LEGACY_INP_TYPE.get(field["inputType"], 3),
                    "length": 12,
                    "isFocus": 0,
                    "regex": (field.get("validation") or {}).get("regex") or "",
                }, separators=(",", ":")),
                "firebaseId": "",
                "inpId": order,
                "key": field["key"],
                "title": field["label"],
                "type": LEGACY_INPUT_TYPE["EDIT_TEXT"],
            })
        elif ftype == "SPINNER":
            # The retired UI paired a SPINNER_TITLE row with the SPINNER row.
            entries.append({
                "calId": order, "data": field["label"], "firebaseId": "", "inpId": order,
                "key": f"{field['key']}_title", "title": field["label"],
                "type": 7,
            })
            options = [{"key": o["label"], "value": o["value"]} for o in field["options"]]
            entries.append({
                "calId": order,
                "data": json.dumps(options, separators=(",", ":")),
                "firebaseId": "", "inpId": order,
                "key": field["key"], "title": field["label"],
                "type": LEGACY_INPUT_TYPE["SPINNER"],
            })
            spinner_index += 1
        elif ftype == "BUTTON":
            entries.append({
                "calId": order,
                "data": json.dumps({"redUrl": "", "action": 1}, separators=(",", ":")),
                "firebaseId": "", "inpId": order,
                "key": field["key"], "title": field["label"],
                "type": LEGACY_INPUT_TYPE["BUTTON"],
            })
    return entries


def legacy_output_entries(calc):
    return [
        {
            "calId": item["order"],
            "curr": "₹",
            "data": "",
            "firebaseId": "",
            "formulae": "",
            "outId": item["order"],
            "outKey": item["key"],
            "outMsg": item["label"],
            "type": 1,
        }
        for item in calc["output"]["summary"]
    ]


def write_v1_calculator(calc):
    doc = {
        "calId": VERSION,
        "calName": calc["name"],
        "firebaseId": calc["id"],
        "iconUrl": icon_for(calc["id"]),
        "input": json.dumps(legacy_input_entries(calc), separators=(",", ":")),
        "output": json.dumps(legacy_output_entries(calc), separators=(",", ":")),
        "redUrl": LEGACY_RED_URL.get(calc["id"], "GenericCalculatorActivity"),
        "version": 1,
    }
    write(os.path.join(OUT, "v1", "calculators", f"{calc['id']}.json"), doc)


def write_v1_config():
    write(os.path.join(OUT, "v1", "config.json"), {
        "BANNER_PLACEMENT_ID": app_config()["bannerPlacementId"],
        "playStoreVersion": 1,
        "showAds": True,
        "inAppReviewEnabled": True,
        "inAppReviewCooldownDays": 2,
    })


def write_v1_dashboard():
    rows = []
    pos = 0

    def row(cmp_type, cmp_data, cmp_id):
        nonlocal pos
        pos += 1
        rows.append({
            "cmpData": cmp_data,
            "cmpId": cmp_id,
            "cmpPos": pos,
            "cmpType": cmp_type,
            "firebaseId": "",
        })

    row(1, json.dumps([{
        "cmpId": "1", "firebaseId": "", "interval": 5000,
        "redUrl": "https://raw.githubusercontent.com/ranjan-rajeev/Finanace-Calculator-Web/main/assets/banner-1.png",
        "webUrl": "https://ranjan-rajeev.github.io/Finanace-Calculator-Web/",
    }], separators=(",", ":")), "banner")

    by_id = {c["id"]: c for c in calculators()}
    for title, ids in DASHBOARD_ORDER:
        row(4, title, f"header_{title.lower().replace(' ', '_').replace('&', 'and')}")
        tiles = [
            {
                "calId": VERSION,
                "calName": by_id[cid]["name"],
                "firebaseId": cid,
                "iconUrl": icon_for(cid),
                "redUrl": LEGACY_RED_URL.get(cid, "GenericCalculatorActivity"),
                "version": 1,
            }
            for cid in ids
        ]
        row(5, json.dumps(tiles, separators=(",", ":")), f"grid_{title.lower().replace(' ', '_').replace('&', 'and')}")

    row(6, "24", "spacer_bottom")
    row(7, "", "ad_bottom")

    write(os.path.join(OUT, "v1", "dashboard.json"), rows)


def write_v1_retirement_copies():
    """The retired APK fetched NPS/ATAL/CAGR from their own nested paths."""
    for calc in calculators():
        if calc["id"] not in ("NPS", "ATAL", "CAGR"):
            continue
        folder = calc["id"].lower()
        doc = {
            "calId": VERSION,
            "calName": calc["name"],
            "firebaseId": calc["id"],
            "iconUrl": icon_for(calc["id"]),
            "input": json.dumps(legacy_input_entries(calc), separators=(",", ":")),
            "output": json.dumps(legacy_output_entries(calc), separators=(",", ":")),
            "redUrl": "GenericCalculatorActivity",
            "version": 1,
        }
        write(os.path.join(OUT, "v1", folder, f"{folder}calculator.json"), doc)

        more_info = [
            {
                "answer": item["answer"],
                "calId": calc["id"],
                "firebaseId": calc["id"],
                "ques": item["question"],
            }
            for item in MORE_INFO.get(calc["id"], [])
        ]
        write(os.path.join(OUT, "v1", folder, f"{folder}moreinfo.json"), more_info)


# --------------------------------------------------------------------------

def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as handle:
        json.dump(data, handle, indent=2, ensure_ascii=False)
        handle.write("\n")
    print(f"  {os.path.relpath(path, ROOT)}")


def main():
    print("Generating hosting JSON")
    write_v2_config()
    for calc in calculators():
        write_v2_calculator(calc)
    write_v2_dashboard()

    write_v1_config()
    for calc in calculators():
        write_v1_calculator(calc)
    write_v1_dashboard()
    write_v1_retirement_copies()
    print("Done")


if __name__ == "__main__":
    main()
