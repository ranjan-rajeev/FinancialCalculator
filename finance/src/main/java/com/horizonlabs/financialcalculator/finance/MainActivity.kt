package com.horizonlabs.financialcalculator.finance

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.horizonlabs.financialcalculator.calculators.generic.CommonCalculatorScreen
import com.horizonlabs.financialcalculator.calculators.generic.CommonCalculatorViewModel
import com.horizonlabs.financialcalculator.core.domain.model.encodeInputValues
import com.horizonlabs.financialcalculator.core.presentation.navigation.Routes
import com.horizonlabs.financialcalculator.core.presentation.screen.AboutScreen
import com.horizonlabs.financialcalculator.core.presentation.screen.HistoryIntent
import com.horizonlabs.financialcalculator.core.presentation.screen.HistoryScreen
import com.horizonlabs.financialcalculator.core.presentation.screen.HistoryViewModel
import com.horizonlabs.financialcalculator.core.presentation.screen.WebViewScreen
import com.horizonlabs.financialcalculator.core.presentation.theme.FinancialCalculatorTheme
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors
import com.horizonlabs.financialcalculator.dashboard.DashboardScreen
import com.horizonlabs.financialcalculator.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set status bar color to blue (Primary)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = LegacyColors.Primary.toArgb()
        
        // Set navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.navigationBarColor = LegacyColors.Background.toArgb()
        }
        
        setContent {
            FinancialCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = rememberNavController(),
                        dashboardViewModel = dashboardViewModel,
                        onOpenUrl = { url -> openUrl(url) }
                    )
                }
            }
        }
    }

    private fun openUrl(url: String?) {
        val target = url?.takeIf { it.isNotBlank() } ?: return
        val uri = runCatching { Uri.parse(target) }.getOrNull() ?: return
        // The system resolves the best handler; `runCatching` swallows the
        // ActivityNotFoundException when nothing on the device can serve the target.
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, uri)) }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel,
    onOpenUrl: (String?) -> Unit
) {
    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onCalculatorClick = { calculatorId, calculatorName ->
                    navController.navigate(Routes.genericCalculator(calculatorId, calculatorName))
                },
                onBannerClick = { actionUrl -> onOpenUrl(actionUrl) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) },
                onWebViewClick = { url, title -> navController.navigate(Routes.webView(url, title)) },
                onAboutClick = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(
            route = Routes.GENERIC_CALCULATOR,
            arguments = listOf(
                navArgument(CommonCalculatorViewModel.ARG_CALCULATOR_ID) { type = NavType.StringType },
                navArgument(CommonCalculatorViewModel.ARG_CALCULATOR_NAME) { type = NavType.StringType },
                navArgument(CommonCalculatorViewModel.ARG_RESTORE_INPUTS) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val calculatorId =
                backStackEntry.arguments?.getString(CommonCalculatorViewModel.ARG_CALCULATOR_ID).orEmpty()
            val calculatorName =
                backStackEntry.arguments?.getString(CommonCalculatorViewModel.ARG_CALCULATOR_NAME).orEmpty()

            CommonCalculatorScreen(
                viewModel = hiltViewModel<CommonCalculatorViewModel>(backStackEntry),
                onBackClick = { navController.popBackStack() },
                onHistoryClick = { navController.navigate(Routes.HISTORY) }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                appVersion = appVersionName(),
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            val historyState by historyViewModel.state.collectAsStateWithLifecycle()
            HistoryScreen(
                entries = historyState.entries,
                onBackClick = { navController.popBackStack() },
                onClearAllClick = { historyViewModel.onIntent(HistoryIntent.OnClearAll) },
                onEntryClick = { entry ->
                    navController.navigate(
                        Routes.genericCalculator(
                            calculatorId = entry.calculatorId,
                            calculatorName = entry.calculatorName,
                            restoreInputs = encodeInputValues(entry.inputValues)
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.WEB_VIEW,
            arguments = listOf(
                navArgument(ARG_WEB_URL) { type = NavType.StringType },
                navArgument(ARG_WEB_TITLE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            WebViewScreen(
                title = backStackEntry.arguments?.getString(ARG_WEB_TITLE).orEmpty(),
                url = backStackEntry.arguments?.getString(ARG_WEB_URL).orEmpty(),
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

private const val ARG_WEB_URL = "url"
private const val ARG_WEB_TITLE = "title"

@Composable
private fun appVersionName(): String {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "—"
    }
}

@Composable
private fun PlaceholderScreen(title: String, onBackClick: () -> Unit) {
    com.horizonlabs.financialcalculator.core.presentation.component.CalculatorTopBar(
        title = title,
        onBackClick = onBackClick,
        showBackArrow = true
    )
}
