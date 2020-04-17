package com.financialcalculator.generic.viewholders;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;
import com.financialcalculator.utility.Util;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class DateViewHolder extends RecyclerView.ViewHolder {

    TextInputLayout til;
    EditText editText;
    private DatePickerDialog mDatePickerDialog;

    public DateViewHolder(View itemView) {
        super(itemView);
        this.til = itemView.findViewById(R.id.til);
        this.editText = itemView.findViewById(R.id.editText);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        editText.setText(Util.getDatefromLong(Calendar.getInstance().getTimeInMillis()));
        editText.setEnabled(false);

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
    }
}