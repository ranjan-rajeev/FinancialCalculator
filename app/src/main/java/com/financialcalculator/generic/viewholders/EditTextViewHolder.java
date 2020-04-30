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
import com.financialcalculator.model.GenericViewTypeModel;
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
        //editText.addTextChangedListener(textWatcher);
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
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else if (editTextEntity.getInpType() == TYPE_TEXT) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class EditTextEntity {
        int inpType;
        int length;
        int isFocus;
        String regex;
        String data;

        public int getInpType() {
            return inpType;
        }

        public void setInpType(int inpType) {
            this.inpType = inpType;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getIsFocus() {
            return isFocus;
        }

        public void setIsFocus(int isFocus) {
            this.isFocus = isFocus;
        }

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (editTextEntity.getRegex() != null && !editTextEntity.getRegex().equals("")) {
                if (s.toString().matches(editTextEntity.getRegex())) {

                }
            } else {

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (editTextEntity.getRegex() != null) {
                if (editText.getText().toString().matches(editTextEntity.getRegex())) {

                }
            }
        }
    };

    public void setEditextKeyVaue(String stringValue) {
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
        ((GenericCalculatorActivity) context).setInputHashMap(genericViewTypeModel.getKey(), value);
    }

}