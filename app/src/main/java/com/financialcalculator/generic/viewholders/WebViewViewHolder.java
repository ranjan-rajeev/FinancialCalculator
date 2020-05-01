package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.WebViewActivity;
import com.financialcalculator.model.EditTextEntity;
import com.financialcalculator.model.GenericViewTypeModel;
import com.financialcalculator.utility.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class WebViewViewHolder extends RecyclerView.ViewHolder {

    WebView webView;
    Context context;
    GenericViewTypeModel genericViewTypeModel;
    WebViewEntity webViewEntity;

    public WebViewViewHolder(View itemView) {
        super(itemView);
        this.webView = itemView.findViewById(R.id.webView);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        this.context = context;
        this.genericViewTypeModel = genericViewTypeModel;
        new ConvertAsync().execute();
        this.genericViewTypeModel.setValid(true);
        webView.setOnClickListener(v -> Util.inAppRedirection(context, webViewEntity.getRedUrl(), genericViewTypeModel.getTitle()));
    }

    private class ConvertAsync extends AsyncTask<Void, Void, WebViewEntity> {

        @Override
        protected WebViewEntity doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<WebViewEntity>() {
                }.getType();
                return gson.fromJson(genericViewTypeModel.getData(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(WebViewEntity entity) {
            super.onPostExecute(entity);
            if (entity != null) {
                webViewEntity = entity;
                bindWebView();
            }

        }
    }

    private void bindWebView() {

        webView.getSettings().setLightTouchEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setSoundEffectsEnabled(true);
        if (webViewEntity.getWebUrl().equals("")) {
            webView.loadData(webViewEntity.getContent(), "text/html", "UTF-8");
        } else {
            webView.loadUrl(webViewEntity.getWebUrl());
        }

    }

    class WebViewEntity {
        String redUrl;
        String webUrl;
        String content;

        public String getRedUrl() {
            return redUrl;
        }

        public void setRedUrl(String redUrl) {
            this.redUrl = redUrl;
        }

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}