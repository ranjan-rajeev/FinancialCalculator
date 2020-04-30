package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.model.EditTextEntity;
import com.financialcalculator.model.GenericViewTypeModel;
import com.financialcalculator.utility.Logger;
import com.financialcalculator.utility.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;

public class EditTextViewHolder extends RecyclerView.ViewHolder {

    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_DECIMAL = 2;
    public static final int TYPE_TEXT = 3;
    public TextInputLayout til;
    public EditText editText;
    Context context;
    GenericViewTypeModel genericViewTypeModel;
    EditTextEntity editTextEntity;

    public EditTextViewHolder(View itemView) {
        super(itemView);
        this.til = itemView.findViewById(R.id.til);
        this.editText = itemView.findViewById(R.id.editText);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        this.genericViewTypeModel = genericViewTypeModel;
        this.context = context;
        til.setHint(genericViewTypeModel.getTitle());
        new ConvertAsync().execute();
        editText.addTextChangedListener(textWatcher);
        editText.setOnFocusChangeListener(onFocusChangeListener);
        //editText.setMaxEms(genericViewTypeModel.getMaxLength());
    }

    private class ConvertAsync extends AsyncTask<Void, Void, EditTextEntity> {

        @Override
        protected EditTextEntity doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<EditTextEntity>() {
                }.getType();
                return gson.fromJson(genericViewTypeModel.getData(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(EditTextEntity list) {
            super.onPostExecute(list);
            if (list != null) {
                editTextEntity = list;
                bindEditText();
            }

        }
    }

    public void bindEditText() {
        try {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(editTextEntity.getLength())});
            if (editTextEntity.getInpType() == TYPE_NUMBER) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (editTextEntity.getInpType() == TYPE_DECIMAL) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            } else if (editTextEntity.getInpType() == TYPE_TEXT) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Logger.d("Before  : " + before);
            if (s.toString().equals("") && before > 0) {
                genericViewTypeModel.setValid(false);
                showErrorMessage(true);
            } else {
                setEditextKeyVaue(s.toString());
                showErrorMessage(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (v instanceof EditText && !hasFocus) {
                ((EditText) v).setText(Util.getCommaSeparated(Util.removeComma(((EditText) v).getText().toString())));
            }
        }
    };

    public void setEditextKeyVaue(String stringValue) {
        try {
            stringValue = Util.removeComma(stringValue);
            BigDecimal value = null;
            if (editTextEntity != null) {
                switch (editTextEntity.getInpType()) {
                    case TYPE_NUMBER:
                        value = new BigDecimal(stringValue).setScale(0, BigDecimal.ROUND_HALF_UP);
                        break;
                    case TYPE_DECIMAL:
                        value = new BigDecimal(stringValue).setScale(2, BigDecimal.ROUND_HALF_UP);
                        break;
                }
            }
            ((GenericCalculatorActivity) context).setInputHashMap(genericViewTypeModel.getKey().charAt(0), value);
            genericViewTypeModel.setValid(true);
        } catch (Exception e) {
            e.printStackTrace();
            genericViewTypeModel.setValid(false);
        }
        Logger.d(genericViewTypeModel.getKey() + " key is " + genericViewTypeModel.isValid());
    }


    public void showErrorMessage(boolean isError) {
        if (isError) {
            editText.requestFocus();
            editText.setError("Enter Valid " + Util.removeWord(genericViewTypeModel.getTitle(), "Enter"));
        } else {
            editText.setError(null);
        }
    }
}