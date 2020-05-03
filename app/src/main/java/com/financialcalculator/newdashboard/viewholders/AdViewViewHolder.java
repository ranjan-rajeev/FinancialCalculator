package com.financialcalculator.newdashboard.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.HomePageModel;

public class AdViewViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    Context context;
    HomePageModel homePageModel;

    public AdViewViewHolder(View itemView) {
        super(itemView);
        this.title = itemView.findViewById(R.id.title);
    }

    public void setData(Context context, HomePageModel homePageModel) {
        this.context = context;
        this.homePageModel = homePageModel;
        this.title.setText(homePageModel.getCmpData());
    }
}