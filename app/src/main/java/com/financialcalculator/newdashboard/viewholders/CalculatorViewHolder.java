package com.financialcalculator.newdashboard.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.newdashboard.NewDashboardItemAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CalculatorViewHolder extends RecyclerView.ViewHolder {

    Context context;
    HomePageModel homePageModel;
    RecyclerView rvCalList;

    public CalculatorViewHolder(View itemView) {
        super(itemView);
        this.rvCalList = itemView.findViewById(R.id.rvCalList);
    }

    public void setData(Context context, HomePageModel homePageModel) {
        this.context = context;
        this.homePageModel = homePageModel;
        new ConvertAsync().execute();
    }

    private class ConvertAsync extends AsyncTask<Void, Void, List<CalculatorEntity>> {

        @Override
        protected List<CalculatorEntity> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<CalculatorEntity>>() {
                }.getType();
                return gson.fromJson(homePageModel.getCmpData(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(List<CalculatorEntity> list) {
            super.onPostExecute(list);
            if (list != null)
                bindGridRecyclerView(list);
        }
    }

    private void bindGridRecyclerView(List<CalculatorEntity> list) {
        NewDashboardItemAdapter dashboardItemAdapter = new NewDashboardItemAdapter(context, list);
        rvCalList.setLayoutManager(new GridLayoutManager(context, 3));
        rvCalList.setAdapter(dashboardItemAdapter);
    }
}