package com.financialcalculator.newdashboard.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.utility.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DashboardWebViewViewHolder extends RecyclerView.ViewHolder {

    WebView webView;
    Context context;
    HomePageModel homePageModel;
    WebViewEntity webViewEntity;

    public DashboardWebViewViewHolder(View itemView) {
        super(itemView);
        this.webView = itemView.findViewById(R.id.webView);
    }

    public void setData(Context context, HomePageModel homePageModel) {
        this.context = context;
        this.homePageModel = homePageModel;
        webView.setBackgroundColor(context.getColor(R.color.bg_light));
        new ConvertAsync().execute();
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.webView && event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (webViewEntity.getRedUrl() != null && !webViewEntity.getRedUrl().equalsIgnoreCase(""))
                        Util.inAppRedirection(context, webViewEntity.getRedUrl(), webViewEntity.getTitle());
                }

                return false;
            }
        });
    }

    private class ConvertAsync extends AsyncTask<Void, Void, List<WebViewEntity>> {

        @Override
        protected List<WebViewEntity> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<WebViewEntity>>() {
                }.getType();
                return gson.fromJson(homePageModel.getCmpData(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(List<WebViewEntity> entity) {
            super.onPostExecute(entity);
            if (entity != null) {
                webViewEntity = entity.get(0);
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
        String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

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