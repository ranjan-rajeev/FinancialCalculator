package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;
import com.google.android.material.textfield.TextInputLayout;

public class EditTextSpinnerViewHolder extends RecyclerView.ViewHolder {

    public TextInputLayout til;
    public EditText editText;
    public Spinner spinner;
    public ArrayAdapter<String> arrayAdapter;

    public EditTextSpinnerViewHolder(View itemView) {
        super(itemView);
        this.til = itemView.findViewById(R.id.til);
        this.editText = itemView.findViewById(R.id.editText);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        til.setHint(genericViewTypeModel.getViewTitle());
        if (genericViewTypeModel.getInputType()!=null && genericViewTypeModel.getInputType().equalsIgnoreCase("number")) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(genericViewTypeModel.getMaxLength())});
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, genericViewTypeModel.getData());
        spinner.setAdapter(arrayAdapter);
        //editText.setMaxEms(genericViewTypeModel.getMaxLength());
    }
}