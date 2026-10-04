package com.horizonlabs.financialcalculator.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.horizonlabs.financialcalculator.core.domain.model.ActionType
import com.horizonlabs.financialcalculator.core.domain.model.AdData
import com.horizonlabs.financialcalculator.core.domain.model.BannerData
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorCard
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorGridData
import com.horizonlabs.financialcalculator.core.domain.model.CalculatorListData
import com.horizonlabs.financialcalculator.core.domain.model.DashboardComponent
import com.horizonlabs.financialcalculator.core.domain.model.DashboardResponse
import com.horizonlabs.financialcalculator.core.domain.model.HeaderData
import com.horizonlabs.financialcalculator.core.domain.model.SpacerData
import com.horizonlabs.financialcalculator.core.domain.model.WebViewData
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorIcon
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorIcons
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorTile
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorTopBar
import com.horizonlabs.financialcalculator.core.presentation.component.DrawerItem
import com.horizonlabs.financialcalculator.core.presentation.component.LegacyDimens
import com.horizonlabs.financialcalculator.core.presentation.component.LegacySectionCard
import com.horizonlabs.financialcalculator.core.presentation.component.LegacySectionTitle
import com.horizonlabs.financialcalculator.core.presentation.component.NavigationDrawer
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors
import com.horizonlabs.financialcalculator.core.presentation.mvi.UiState
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onCalculatorClick: (calculatorId: String, calculatorName: String) -> Unit,
    onBannerClick: (actionUrl: String?) -> Unit,
    onHistoryClick: () -> Unit,
    onWebViewClick: (url: String, title: String) -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val drawerOpen = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DashboardEffect.OpenCalculator ->
                    onCalculatorClick(effect.calculatorId, effect.calculatorName)
                is DashboardEffect.OpenBanner -> onBannerClick(effect.actionUrl)
                DashboardEffect.ShareApp -> shareApp(context)
                DashboardEffect.RateApp -> rateApp(context)
                DashboardEffect.OpenAbout -> onAboutClick()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = LegacyColors.DashboardBackground
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CalculatorTopBar(
                    title = "Financial Calculator",
                    onHistoryClick = onHistoryClick,
                    onDrawerClick = { drawerOpen.value = true },
                    showBackArrow = false
                )

                when (val uiState = state.uiState) {
                    is UiState.Loading -> LoadingView()
                    is UiState.Error -> ErrorView(
                        message = uiState.message,
                        onRetry = { viewModel.onIntent(DashboardIntent.OnRetry) }
                    )
                    is UiState.Success -> state.dashboard?.let { dashboard ->
                        DashboardContent(
                            dashboard = dashboard,
                            onCalculatorClick = { id, name ->
                                viewModel.onIntent(DashboardIntent.OnCalculatorClick(id, name))
                            },
                            onBannerClick = { url, actionType ->
                                viewModel.onIntent(DashboardIntent.OnBannerClick(url, actionType.name))
                            },
                            onWebViewClick = onWebViewClick
                        )
                    }
                }
            }
        }

        NavigationDrawer(
            isOpen = drawerOpen.value,
            onClose = { drawerOpen.value = false },
            onItemClick = { item ->
                drawerOpen.value = false
                // Home is a no-op: the drawer is only reachable from the dashboard itself.
                val intent = when (item) {
                    DrawerItem.Home -> null
                    DrawerItem.Share -> DashboardIntent.OnShareApp
                    DrawerItem.Rate -> DashboardIntent.OnRateApp
                    DrawerItem.About -> DashboardIntent.OnAboutClick
                }
                intent?.let(viewModel::onIntent)
            }
        )
    }
}

/**
 * Prefers WhatsApp like the retired `MainActivity.shareWhatsApp()`, but falls back to the system
 * chooser instead of silently doing nothing when WhatsApp is not installed.
 */
private fun shareApp(context: Context) {
    val link = playStoreLink(context)
    val whatsapp = Intent(Intent.ACTION_SEND).apply {
        setPackage("com.whatsapp")
        putExtra(Intent.EXTRA_TEXT, link)
        type = "text/plain"
    }
    val intent = if (whatsapp.resolveActivity(context.packageManager) != null) {
        whatsapp
    } else {
        Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, link)
                type = "text/plain"
            },
            null
        )
    }
    runCatching { context.startActivity(intent) }
}

/** Opens the Play Store listing, falling back to the web URL when no store app exists. */
private fun rateApp(context: Context) {
    val market = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=${context.packageName}")
    )
    if (market.resolveActivity(context.packageManager) != null) {
        runCatching { context.startActivity(market) }
    } else {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink(context))))
        }
    }
}

private fun playStoreLink(context: Context): String =
    "https://play.google.com/store/apps/details?id=${context.packageName}"

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun DashboardContent(
    dashboard: DashboardResponse,
    onCalculatorClick: (String, String) -> Unit,
    onBannerClick: (String?, ActionType) -> Unit,
    onWebViewClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 0.dp, bottom = 16.dp)
    ) {
        items(
            items = dashboard.components.sortedBy { it.position },
            key = { it.id }
        ) { component ->
            DashboardComponentView(
                component = component,
                onCalculatorClick = onCalculatorClick,
                onBannerClick = onBannerClick,
                onWebViewClick = onWebViewClick
            )
        }
    }
}

@Composable
private fun DashboardComponentView(
    component: DashboardComponent,
    onCalculatorClick: (String, String) -> Unit,
    onBannerClick: (String?, ActionType) -> Unit,
    onWebViewClick: (String, String) -> Unit
) {
    when (component) {
        is DashboardComponent.BannerCarousel ->
            BannerCarouselView(data = component.data, onBannerClick = onBannerClick)
        is DashboardComponent.SectionHeader -> SectionHeaderView(data = component.data)
        is DashboardComponent.CalculatorGrid ->
            CalculatorGridView(data = component.data, onCalculatorClick = onCalculatorClick)
        is DashboardComponent.CalculatorList ->
            CalculatorListView(data = component.data, onCalculatorClick = onCalculatorClick)
        is DashboardComponent.Spacer -> SpacerView(data = component.data)
        is DashboardComponent.WebView -> WebViewComponentView(
            data = component.data,
            onWebViewClick = onWebViewClick
        )
        is DashboardComponent.AdPlaceholder -> AdPlaceholderView(data = component.data)
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun BannerCarouselView(
    data: BannerData,
    onBannerClick: (url: String?, actionType: ActionType) -> Unit
) {
    if (data.images.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { data.images.size })

    LaunchedEffect(pagerState, data.autoPlay, data.intervalMs) {
        if (data.autoPlay && data.images.size > 1) {
            while (true) {
                delay(data.intervalMs)
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val image = data.images[page]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = image.actionType != ActionType.NONE) {
                    onBannerClick(image.actionUrl, image.actionType)
                }
        ) {
            AsyncImage(
                model = image.imageUrl,
                contentDescription = "Banner",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
private fun SectionHeaderView(data: HeaderData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LegacyDimens.CardMargin, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        data.iconUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = data.title,
            style = MaterialTheme.typography.titleLarge,
            color = LegacyColors.PrimaryDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = if (data.iconUrl == null) 0.dp else 8.dp)
        )
    }
}

@Composable
private fun CalculatorGridView(
    data: CalculatorGridData,
    onCalculatorClick: (String, String) -> Unit
) {
    val columns = data.columns.coerceAtLeast(1)

    LegacySectionCard {
        Column(modifier = Modifier.padding(vertical = 10.dp)) {
            data.calculators.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { calculator ->
                        CalculatorTile(
                            name = calculator.name,
                            fallbackIcon = CalculatorIcons.fallbackFor(calculator.id),
                            iconUrl = calculator.iconUrl,
                            onClick = { onCalculatorClick(calculator.id, calculator.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorListView(
    data: CalculatorListData,
    onCalculatorClick: (String, String) -> Unit
) {
    LegacySectionCard {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            data.calculators.forEach { calculator ->
                CalculatorRow(
                    name = calculator.name,
                    fallbackIcon = CalculatorIcons.fallbackFor(calculator.id),
                    iconUrl = calculator.iconUrl,
                    onClick = { onCalculatorClick(calculator.id, calculator.name) }
                )
            }
        }
    }
}

@Composable
private fun CalculatorRow(
    name: String,
    @androidx.annotation.DrawableRes fallbackIcon: Int,
    iconUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(LegacyDimens.InputRadius),
        colors = CardDefaults.cardColors(containerColor = LegacyColors.DashboardSectionBackground),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CalculatorIcon(
                name = name,
                fallbackIcon = fallbackIcon,
                iconUrl = iconUrl,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = LegacyColors.TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
private fun SpacerView(data: SpacerData) {
    Spacer(modifier = Modifier.height(data.heightDp.dp))
}

/**
 * An inline WebView inside the dashboard `LazyColumn` would fight the parent's scroll gesture and
 * pin a WebView per dashboard entry, so the card is a tappable preview that opens the full screen
 * host in `finance`'s nav graph.
 */
@Composable
private fun WebViewComponentView(
    data: WebViewData,
    onWebViewClick: (url: String, title: String) -> Unit
) {
    if (data.url.isBlank()) return

    Card(
        onClick = { onWebViewClick(data.url, data.title) },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(LegacyDimens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = LegacyColors.CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open",
                tint = LegacyColors.TextLight,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AdPlaceholderView(data: AdData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(data.heightDp.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(LegacyDimens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = LegacyColors.DashboardSectionBackground
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ad",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}
