package com.horizonlabs.financialcalculator.core.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val version: String,
    val config: AppConfig,
    val components: List<DashboardComponent>
)

@Serializable
data class AppConfig(
    val bannerPlacementId: String?,
    val showAds: Boolean,
    val inAppReviewEnabled: Boolean,
    val inAppReviewCooldownDays: Int,
    val playStoreVersion: Int,
    val forceUpdateVersion: Int = 0,
    val maintenanceMode: Boolean = false,
    val maintenanceMessage: String? = null
)

@Serializable
sealed interface DashboardComponent {
    val id: String
    val position: Int

    @Serializable
    @SerialName("BANNER_CAROUSEL")
    data class BannerCarousel(
        override val id: String,
        override val position: Int,
        val data: BannerData
    ) : DashboardComponent

    @Serializable
    @SerialName("SECTION_HEADER")
    data class SectionHeader(
        override val id: String,
        override val position: Int,
        val data: HeaderData
    ) : DashboardComponent

    @Serializable
    @SerialName("CALCULATOR_GRID")
    data class CalculatorGrid(
        override val id: String,
        override val position: Int,
        val data: CalculatorGridData
    ) : DashboardComponent

    @Serializable
    @SerialName("CALCULATOR_LIST")
    data class CalculatorList(
        override val id: String,
        override val position: Int,
        val data: CalculatorListData
    ) : DashboardComponent

    @Serializable
    @SerialName("SPACER")
    data class Spacer(
        override val id: String,
        override val position: Int,
        val data: SpacerData
    ) : DashboardComponent

    @Serializable
    @SerialName("WEB_VIEW")
    data class WebView(
        override val id: String,
        override val position: Int,
        val data: WebViewData
    ) : DashboardComponent

    @Serializable
    @SerialName("AD_PLACEHOLDER")
    data class AdPlaceholder(
        override val id: String,
        override val position: Int,
        val data: AdData
    ) : DashboardComponent
}

@Serializable
data class BannerData(
    val images: List<BannerImage>,
    val autoPlay: Boolean = true,
    val intervalMs: Long = 5000
)

@Serializable
data class BannerImage(
    val id: String,
    val imageUrl: String,
    val actionUrl: String? = null,
    val actionType: ActionType = ActionType.WEB
)

@Serializable
enum class ActionType {
    WEB, DEEP_LINK, NONE
}

@Serializable
data class HeaderData(
    val title: String,
    val iconUrl: String? = null
)

@Serializable
data class CalculatorGridData(
    val calculators: List<CalculatorCard>,
    val columns: Int = 2
)

@Serializable
data class CalculatorListData(
    val calculators: List<CalculatorCard>
)

@Serializable
data class CalculatorCard(
    val id: String,
    val name: String,
    val iconUrl: String,
    val calculatorType: String,
    val version: Int
)

@Serializable
data class SpacerData(
    val heightDp: Int = 16
)

@Serializable
data class WebViewData(
    val url: String,
    val title: String
)

@Serializable
data class AdData(
    val placementId: String,
    val heightDp: Int = 50
)