package com.horizonlabs.financialcalculator.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculator_config")
data class CalculatorConfigEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val type: String,
    val version: Int,
    val category: String,
    val engine: String,
    val inputFieldsJson: String,
    val outputConfigJson: String,
    val moreInfoJson: String,
    val formulaMapJson: String?
)
