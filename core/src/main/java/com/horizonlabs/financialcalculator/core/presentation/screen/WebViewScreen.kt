package com.horizonlabs.financialcalculator.core.presentation.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.horizonlabs.financialcalculator.core.presentation.component.CalculatorTopBar
import com.horizonlabs.financialcalculator.core.presentation.theme.LegacyColors

private const val PDF_VIEWER_PREFIX = "https://docs.google.com/viewer?url="

/**
 * In-app replacement for the retired `WebViewActivity`. The dashboard hands the published
 * [com.horizonlabs.financialcalculator.core.domain.model.WebViewData] url straight to this screen
 * instead of bouncing the user out to an external browser.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    title: String,
    url: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var progress by remember(url) { mutableIntStateOf(0) }
    var failed by remember(url) { mutableStateOf(!context.hasInternet()) }
    // Bumped on retry so the WebView is rebuilt from scratch; the failed one is torn down.
    var generation by remember(url) { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        CalculatorTopBar(
            title = title,
            onBackClick = onBackClick,
            showBackArrow = true
        )

        if (failed) {
            WebViewErrorView(
                onRetry = {
                    failed = !context.hasInternet()
                    generation++
                }
            )
            return@Column
        }

        LegacyWebView(
            url = url,
            generation = generation,
            modifier = Modifier.fillMaxSize(),
            onProgress = { progress = it },
            onFailed = { failed = true }
        )

        if (progress in 1..99) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LegacyWebView(
    url: String,
    generation: Int,
    modifier: Modifier = Modifier,
    onProgress: (Int) -> Unit,
    onFailed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var canGoBack by remember(url, generation) { mutableStateOf(false) }
    val webView = remember(url, generation) {
        buildWebView(
            context = context,
            url = url,
            onProgress = onProgress,
            onHistoryChanged = { canGoBack = it },
            onFailed = onFailed
        )
    }

    DisposableEffect(lifecycleOwner, webView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> webView.onResume()
                Lifecycle.Event.ON_PAUSE -> webView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            webView.stopLoading()
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView.destroy()
        }
    }

    // Web history takes precedence over leaving the screen, matching legacy `onKeyDown`.
    BackHandler(enabled = canGoBack) {
        webView.goBack()
        canGoBack = webView.canGoBack()
    }

    AndroidView(factory = { webView }, modifier = modifier)
}

@SuppressLint("SetJavaScriptEnabled")
private fun buildWebView(
    context: Context,
    url: String,
    onProgress: (Int) -> Unit,
    onHistoryChanged: (Boolean) -> Unit,
    onFailed: () -> Unit
): WebView = WebView(context).apply {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    setBackgroundColor(LegacyColors.Background.toArgb())
    overScrollMode = View.OVER_SCROLL_NEVER
    isHorizontalScrollBarEnabled = false

    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        loadsImagesAutomatically = true
        lightTouchEnabled = true
        cacheMode = WebSettings.LOAD_DEFAULT
        // Remote pages are untrusted: keep the file/content bridges closed and never expose a
        // @JavascriptInterface to them (legacy did, via `Android.RedirectToHomepage()`).
        allowFileAccess = false
        allowContentAccess = false
        javaScriptCanOpenWindowsAutomatically = false
        setSupportMultipleWindows(false)
    }

    webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView, pageUrl: String, favicon: Bitmap?) {
            super.onPageStarted(view, pageUrl, favicon)
            onProgress(0)
        }

        override fun onPageFinished(view: WebView, pageUrl: String) {
            super.onPageFinished(view, pageUrl)
            onProgress(100)
            onHistoryChanged(view.canGoBack())
        }

        override fun doUpdateVisitedHistory(view: WebView, historyUrl: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, historyUrl, isReload)
            onHistoryChanged(view.canGoBack())
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            // Sub-resource failures must not blank out an otherwise good page.
            if (request.isForMainFrame) onFailed()
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val target = request.url ?: return false
            return shouldOverride(context, target)
        }

        @Deprecated("Kept for API < 24 parity with legacy WebViewActivity")
        @Suppress("DEPRECATION")
        override fun shouldOverrideUrlLoading(view: WebView, target: String): Boolean =
            shouldOverride(context, Uri.parse(target))
    }

    loadUrl(withPdfSupport(url))
}

/**
 * Legacy routed `.pdf` targets through the Google Docs viewer and handed `mailto:`/`tel:` style
 * links to the system handler. Anything that is not plain http(s) is refused, so the WebView cannot
 * be steered into `file:`, `content:`, `javascript:` or `intent:` handling.
 */
private fun shouldOverride(context: Context, target: Uri): Boolean = when (target.scheme?.lowercase()) {
    null, "http", "https" -> false
    "mailto", "tel", "sms", "market" -> {
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, target)) }
        true
    }
    else -> true
}

private fun withPdfSupport(url: String): String =
    if (url.substringBefore('?').endsWith(".pdf", ignoreCase = true)) PDF_VIEWER_PREFIX + url else url

private fun Context.hasInternet(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val network = manager.activeNetwork ?: return false
    val capabilities = manager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
private fun WebViewErrorView(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "No internet connection",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}