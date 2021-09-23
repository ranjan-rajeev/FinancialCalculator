package com.financialcalculator.newdashboard.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.HomePageModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AdViewViewHolder extends RecyclerView.ViewHolder {

    LinearLayout view;
    Context context;
    HomePageModel homePageModel;

    public AdViewViewHolder(View itemView) {
        super(itemView);
        this.view = itemView.findViewById(R.id.view);
    }

    public void setData(Context context, HomePageModel homePageModel) {
        this.context = context;
        this.homePageModel = homePageModel;
        new ConvertAsync().execute();
    }

    class ConvertAsync extends AsyncTask<Void, Void, AdViewEntity> {

        @Override
        protected AdViewEntity doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<AdViewEntity>() {
                }.getType();
                return gson.fromJson(homePageModel.getCmpData(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(AdViewEntity entity) {
            super.onPostExecute(entity);
            if (entity != null) {

            }
        }
    }

    class AdViewEntity {
        int adType;
        String adId;

        public int getAdType() {
            return adType;
        }

        public void setAdType(int adType) {
            this.adType = adType;
        }

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }
    }
}

