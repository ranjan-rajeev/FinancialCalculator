package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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
import java.util.LinkedHashMap;
import java.util.List;

import static com.financialcalculator.generic.viewholders.EditTextViewHolder.TYPE_DECIMAL;
import static com.financialcalculator.generic.viewholders.EditTextViewHolder.TYPE_NUMBER;
import static com.financialcalculator.generic.viewholders.EditTextViewHolder.TYPE_TEXT;

public class EditTextSpinnerViewHolder extends RecyclerView.ViewHolder {

    public TextInputLayout til;
    public EditText editText;
    public Spinner spinner;
    public ArrayAdapter<String> arrayAdapter;
    LinkedHashMap<String, String> keyValueHashMap = new LinkedHashMap<>();
    Context context;
    GenericViewTypeModel genericViewTypeModel;
    EditTextEntity editTextEntity;

    public EditTextSpinnerViewHolder(View itemView) {
        super(itemView);
        this.til = itemView.findViewById(R.id.til);
        this.editText = itemView.findViewById(R.id.editText);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        this.context = context;
        this.genericViewTypeModel = genericViewTypeModel;
        til.setHint(genericViewTypeModel.getTitle());
        new ConvertAsync().execute();
        editText.addTextChangedListener(textWatcher);
        editText.setOnFocusChangeListener(onFocusChangeListener);
        /*if (genericViewTypeModel.getInputType()!=null && genericViewTypeModel.getInputType().equalsIgnoreCase("number")) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(genericViewTypeModel.getMaxLength())});
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, genericViewTypeModel.getData());
        spinner.setAdapter(arrayAdapter);*/
        //editText.setMaxEms(genericViewTypeModel.getMaxLength());
    }

    private class ConvertAsync extends AsyncTask<Void, Void, EditTextEntity> {

        @Override
        protected EditTextEntity doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<EditTextEntity>() {
                }.getType();
                EditTextEntity editTextEntity = gson.fromJson(genericViewTypeModel.getData(), type);

                Type newType = new TypeToken<ArrayList<SpinnerViewHolder.SpinnerEntity>>() {
                }.getType();
                List<SpinnerViewHolder.SpinnerEntity> spinnerEntities = gson.fromJson(editTextEntity.getData(), newType);
                for (SpinnerViewHolder.SpinnerEntity spinnerEntity : spinnerEntities) {
                    keyValueHashMap.put(spinnerEntity.getKey(), spinnerEntity.getValue());
                }

                return editTextEntity;
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
                bindSpinnerList();
            }

        }
    }

    private void bindSpinnerList() {
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new ArrayList<>(keyValueHashMap.keySet()));
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    BigDecimal value = null;
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    value = new BigDecimal(keyValueHashMap.get(selectedItem)).setScale(0, BigDecimal.ROUND_HALF_UP);
                    ((GenericCalculatorActivity) context).setHashMapValue(genericViewTypeModel.getKey().charAt(1), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            ((GenericCalculatorActivity) context).setHashMapValue(genericViewTypeModel.getKey().charAt(0), value);
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