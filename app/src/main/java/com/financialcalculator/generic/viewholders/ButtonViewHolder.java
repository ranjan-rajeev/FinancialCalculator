package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;

public class ButtonViewHolder extends RecyclerView.ViewHolder {

    TextView button;

    public ButtonViewHolder(View itemView) {
        super(itemView);
        this.button = itemView.findViewById(R.id.button);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        button.setText(genericViewTypeModel.getTitle());
        button.setOnClickListener(v -> Toast.makeText(context, "", Toast.LENGTH_SHORT).show());
    }
}