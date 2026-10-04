package com.horizonlabs.financialcalculator.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Entity(tableName = "calculation_history")
@Serializable
data class CalculationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @SerializedName("calculatorId")
    val calculatorId: String,

    @SerializedName("calculatorName")
    val calculatorName: String,

    @SerializedName("calculatorType")
    val calculatorType: String,

    @SerializedName("inputValues")
    val inputValuesJson: String,

    @SerializedName("outputSummary")
    val outputSummaryJson: String?,

    @SerializedName("breakdownData")
    val breakdownDataJson: String?,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)