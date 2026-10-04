package com.horizonlabs.financialcalculator.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "app_config")
@Serializable
data class ConfigEntity(
    @PrimaryKey
    val key: String = "app_config",

    val bannerPlacementId: String?,

    val showAds: Boolean,

    val inAppReviewEnabled: Boolean,

    val inAppReviewCooldownDays: Int,

    val playStoreVersion: Int,

    val forceUpdateVersion: Int,

    val maintenanceMode: Boolean,

    val maintenanceMessage: String?,

    val lastUpdated: Long = System.currentTimeMillis()
)