package com.financialcalculator.newdashboard.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.utility.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SpaceViewHolder extends RecyclerView.ViewHolder {

    View view;
    Context context;
    HomePageModel homePageModel;

    public SpaceViewHolder(View itemView) {
        super(itemView);
        this.view = itemView.findViewById(R.id.view);
    }

    public void setData(Context context, HomePageModel homePageModel) {
        this.context = context;
        this.homePageModel = homePageModel;
        try {
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, Integer.parseInt(homePageModel.getCmpData()), 0, 0);
            view.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}