package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;

public class SpinnerViewHolder extends RecyclerView.ViewHolder {

    Spinner spinner;
    ArrayAdapter<String> arrayAdapter;

    public SpinnerViewHolder(View itemView) {
        super(itemView);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    public void setData(Context context,GenericViewTypeModel genericViewTypeModel) {
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,genericViewTypeModel.getData());
        spinner.setAdapter(arrayAdapter);
    }
}