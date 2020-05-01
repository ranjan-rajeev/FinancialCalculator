package com.financialcalculator.generic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.financialcalculator.R;
import com.financialcalculator.home.MainActivity;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Logger;
import com.financialcalculator.utility.Util;
import com.google.android.material.snackbar.Snackbar;

public class WebViewActivity extends BaseActivity {

    WebView webView;
    String url;
    String title;
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        url = getIntent().getStringExtra("URL");
        title = getIntent().getStringExtra("TITLE");

        // check for notification data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //bundle must contain all info sent in "data" field of the notification
            url = bundle.getString("url");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        if (Util.isNetworkConnected(this)) {
            openWebView();
        } else {
            Snackbar.make(webView, "No Internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", v -> openWebView()).show();
        }

        swipe = findViewById(R.id.swipeContainer);
        swipe.setOnRefreshListener(
                () -> {
                    webView.reload();
                    swipe.setRefreshing(false);
                });
    }


    private void openWebView() {
        if (Util.isNetworkConnected(this)) {
            WebSettings settings = webView.getSettings();
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            settings.setUserAgentString("kurlz");
            settings.setJavaScriptEnabled(true);

            settings.setBuiltInZoomControls(false);
            settings.setUseWideViewPort(false);
            settings.setJavaScriptEnabled(true);
            settings.setSupportMultipleWindows(false);

            settings.setLoadsImagesAutomatically(true);
            settings.setLightTouchEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setJavaScriptEnabled(true);

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    showDialog();
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    cancelDialog();
                    super.onPageFinished(view, url);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*if (url.endsWith(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                        String googleDocs = "https://docs.google.com/viewer?url=";
                        webView.loadUrl(googleDocs + url);
                    }
                }*/
                    return false;
                }
            });
            webView.getSettings().setBuiltInZoomControls(false);
            webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
            Logger.d(url);

            if (url.endsWith(".pdf")) {
                webView.loadUrl("https://docs.google.com/viewer?url=" + url);
                //webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
            } else {
                webView.loadUrl(url);
            }
        } else {
            Snackbar.make(webView, "No Internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", v -> openWebView()).show();
        }
        //webView.loadUrl(url);
    }

    class MyJavaScriptInterface {
        @JavascriptInterface
        public void RedirectToHomepage() {//Android.RedirectToHomepage();
            Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
