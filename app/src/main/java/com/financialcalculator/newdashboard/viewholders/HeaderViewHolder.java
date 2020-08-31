package com.financialcalculator.newdashboard.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.HomePageModel;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    Context context;
    HomePageModel homePageModel;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        this.title = itemView.findViewById(R.id.title);
    }

    public void setData(Context context, HomePageModel homePageModel) {
        this.context = context;
        this.homePageModel = homePageModel;
        this.title.setText(homePageModel.getCmpData());
        this.title.setSelected(true);
    }
}