package com.horizonlabs.financialcalculator.core.domain.model

import com.horizonlabs.financialcalculator.core.data.db.CalculationHistoryEntity
import com.horizonlabs.financialcalculator.core.data.db.CalculatorConfigEntity
import com.horizonlabs.financialcalculator.core.data.db.ConfigEntity
import com.horizonlabs.financialcalculator.core.data.db.MoreInfoEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs

val json = Json { ignoreUnknownKeys = true }

private val gson = Gson()

private val anyMapType = object : TypeToken<Map<String, Any>>() {}.type
private val anyMapListType = object : TypeToken<List<Map<String, Any>>>() {}.type

/** Serialises an input map for hand-off through a navigation argument. */
fun encodeInputValues(values: Map<String, Any>): String = gson.toJson(values)

/**
 * Reads an input map back, normalising Gson-decoded numbers into the raw text the input fields
 * render. Gson turns every JSON number into a `Double`, so without this a stored `500000` would
 * come back as `500000.0` — the exact round-trip the calculator inputs deliberately avoid.
 */
fun decodeInputValues(encoded: String): Map<String, Any> {
    // Reached from a navigation argument, so the payload is not trusted: a malformed value
    // must degrade to "no restore" rather than crash the calculator.
    val parsed: Map<String, Any> = runCatching {
        gson.fromJson(encoded, anyMapType) as? Map<String, Any>
    }.getOrNull() ?: return emptyMap()

    return parsed.mapValues { (_, value) ->
        when (value) {
            is Double -> if (value.isFinite()) value.toRawNumberString() else value
            is Float -> value.toDouble().toRawNumberString()
            else -> value
        }
    }
}

private fun Double.toRawNumberString(): String =
    if (this % 1.0 == 0.0 && abs(this) < Long.MAX_VALUE) toLong().toString() else toString()

fun CalculationHistoryEntity.toDomain(): CalculationHistory {
    val inputValues: Map<String, Any> = gson.fromJson(inputValuesJson, anyMapType) ?: emptyMap()
    val outputSummary: Map<String, Any>? = outputSummaryJson?.let { gson.fromJson(it, anyMapType) }
    val breakdownData: List<Map<String, Any>>? = breakdownDataJson?.let { gson.fromJson(it, anyMapListType) }
    
    return CalculationHistory(
        id = id,
        calculatorId = calculatorId,
        calculatorName = calculatorName,
        calculatorType = calculatorType,
        inputValues = inputValues,
        outputSummary = outputSummary,
        breakdownData = breakdownData,
        timestamp = timestamp
    )
}

fun CalculationHistory.toEntity(): CalculationHistoryEntity {
    return CalculationHistoryEntity(
        id = id,
        calculatorId = calculatorId,
        calculatorName = calculatorName,
        calculatorType = calculatorType,
        inputValuesJson = gson.toJson(inputValues),
        outputSummaryJson = outputSummary?.let { gson.toJson(it) },
        breakdownDataJson = breakdownData?.let { gson.toJson(it) },
        timestamp = timestamp
    )
}

fun CalculatorConfigEntity.toDomain(): CalculatorConfig {
    val inputFields = json.decodeFromString<List<InputFieldConfig>>(inputFieldsJson)
    val outputConfig = json.decodeFromString<OutputConfig>(outputConfigJson)
    val moreInfo = json.decodeFromString<List<MoreInfoItem>>(moreInfoJson)
    val formulaMap = formulaMapJson?.let { json.decodeFromString<Map<String, String>>(it) }
    
    return CalculatorConfig(
        id = id,
        name = name,
        type = CalculatorType.valueOf(type),
        version = version,
        category = category,
        engine = engine,
        inputFields = inputFields,
        outputConfig = outputConfig,
        moreInfo = moreInfo,
        formulaMap = formulaMap
    )
}

fun CalculatorConfig.toEntity(): CalculatorConfigEntity {
    return CalculatorConfigEntity(
        id = id,
        name = name,
        type = type.name,
        version = version,
        category = category,
        engine = engine,
        inputFieldsJson = json.encodeToString<List<InputFieldConfig>>(inputFields),
        outputConfigJson = json.encodeToString<OutputConfig>(outputConfig),
        moreInfoJson = json.encodeToString<List<MoreInfoItem>>(moreInfo),
        formulaMapJson = formulaMap?.let { json.encodeToString<Map<String, String>>(it) }
    )
}

fun AppConfig.toEntity(): ConfigEntity {
    return ConfigEntity(
        bannerPlacementId = bannerPlacementId,
        showAds = showAds,
        inAppReviewEnabled = inAppReviewEnabled,
        inAppReviewCooldownDays = inAppReviewCooldownDays,
        playStoreVersion = playStoreVersion,
        forceUpdateVersion = forceUpdateVersion,
        maintenanceMode = maintenanceMode,
        maintenanceMessage = maintenanceMessage
    )
}

fun MoreInfoEntity.toDomain(): MoreInfoItem {
    return MoreInfoItem(question = question, answer = answer)
}