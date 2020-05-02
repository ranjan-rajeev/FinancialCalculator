package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;

public class SpinnerTitleViewHolder extends RecyclerView.ViewHolder {

    TextView spTitle;

    public SpinnerTitleViewHolder(View itemView) {
        super(itemView);
        this.spTitle = itemView.findViewById(R.id.spTitle);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        spTitle.setText(genericViewTypeModel.getTitle());
        genericViewTypeModel.setValid(true);
    }
}