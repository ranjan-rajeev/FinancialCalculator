package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EditTextViewHolder extends RecyclerView.ViewHolder {

    public TextInputLayout til;
    public EditText editText;
    Context context;
    GenericViewTypeModel genericViewTypeModel;

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
            bindEditText(list);
        }
    }

    public void bindEditText(EditTextEntity editTextEntity) {
        if (editTextEntity.getInpType() == 1) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(editTextEntity.getLength())});
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
}