package com.horizonlabs.financialcalculator.core.domain.model

import com.horizonlabs.financialcalculator.core.data.db.CalculationHistoryEntity
import com.horizonlabs.financialcalculator.core.data.db.CalculatorConfigEntity
import com.horizonlabs.financialcalculator.core.data.db.ConfigEntity
import com.horizonlabs.financialcalculator.core.data.db.MoreInfoEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val json = Json { ignoreUnknownKeys = true }

private val gson = Gson()

private val anyMapType = object : TypeToken<Map<String, Any>>() {}.type
private val anyMapListType = object : TypeToken<List<Map<String, Any>>>() {}.type

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