package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;
import com.google.android.material.textfield.TextInputLayout;

public class EditTextViewHolder extends RecyclerView.ViewHolder {

    public TextInputLayout til;
    public EditText editText;

    public EditTextViewHolder(View itemView) {
        super(itemView);
        this.til = itemView.findViewById(R.id.til);
        this.editText = itemView.findViewById(R.id.editText);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        til.setHint(genericViewTypeModel.getViewTitle());
        if (genericViewTypeModel.getInputType().equalsIgnoreCase("number")) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(genericViewTypeModel.getMaxLength())});
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        //editText.setMaxEms(genericViewTypeModel.getMaxLength());
    }
}