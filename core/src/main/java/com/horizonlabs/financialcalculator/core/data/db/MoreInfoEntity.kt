package com.horizonlabs.financialcalculator.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "more_info")
@Serializable
data class MoreInfoEntity(
    @PrimaryKey
    val firebaseId: String,

    val calculatorId: String,

    val question: String,

    val answer: String
)