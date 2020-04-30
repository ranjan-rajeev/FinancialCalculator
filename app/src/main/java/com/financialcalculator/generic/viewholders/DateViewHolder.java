package com.financialcalculator.generic.viewholders;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;
import com.financialcalculator.utility.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;

public class DateViewHolder extends RecyclerView.ViewHolder {

    TextInputLayout til;
    EditText editText;
    private DatePickerDialog mDatePickerDialog;
    private DateEntity dateEntity;
    GenericViewTypeModel genericViewTypeModel;
    Context context;

    public DateViewHolder(View itemView) {
        super(itemView);
        this.til = itemView.findViewById(R.id.til);
        this.editText = itemView.findViewById(R.id.editText);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        this.genericViewTypeModel = genericViewTypeModel;
        this.context = context;
        genericViewTypeModel.setValid(true);
        til.setHint(genericViewTypeModel.getTitle());
        editText.setText(Util.getDatefromLong(Calendar.getInstance().getTimeInMillis()));

        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                editText.setText(Util.getDatefromLong(calendar.getTimeInMillis()));

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        //mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatePickerDialog.show();
                return false;
            }
        });

        new ConvertAsync().execute();
    }

    private void bindDate() {
        try {
            mDatePickerDialog.getDatePicker().setMaxDate(dateEntity.getMax());
            mDatePickerDialog.getDatePicker().setMinDate(dateEntity.getMin());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class DateEntity {
        long min;
        long max;

        public long getMin() {
            return min;
        }

        public void setMin(long min) {
            this.min = min;
        }

        public long getMax() {
            return max;
        }

        public void setMax(long max) {
            this.max = max;
        }
    }

    class ConvertAsync extends AsyncTask<Void, Void, DateEntity> {

        @Override
        protected DateEntity doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<DateEntity>() {
                }.getType();
                return gson.fromJson(genericViewTypeModel.getData(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(DateEntity list) {
            super.onPostExecute(list);
            if (list != null) {
                dateEntity = list;
                bindDate();
            }

        }
    }
}