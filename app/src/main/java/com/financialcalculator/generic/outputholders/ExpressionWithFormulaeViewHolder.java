package com.financialcalculator.generic.outputholders;

import android.content.Context;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.model.GenericOutputEntity;
import com.financialcalculator.utility.Util;

public class ExpressionWithFormulaeViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;
    Context context;
    GenericOutputEntity genericOutputEntity;

    public ExpressionWithFormulaeViewHolder(View itemView) {
        super(itemView);
        this.tvTitle = itemView.findViewById(R.id.tvTitle);
    }

    public void setData(Context context, GenericOutputEntity genericOutputEntity) {
        this.context = context;
        this.genericOutputEntity = genericOutputEntity;
        //tvTitle.setText(genericOutputEntity.getOutMsg());
        new CalculateResult().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private class CalculateResult extends AsyncTask<Void, Void, SpannableStringBuilder> {

        @Override
        protected SpannableStringBuilder doInBackground(Void... voids) {
            try {
                GenericCalculatorActivity activity = (GenericCalculatorActivity) context;
                return Util.evaluateString(genericOutputEntity.getOutMsg(), activity.getCalculatorEntity().inputHashmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(SpannableStringBuilder result) {
            super.onPostExecute(result);
            if (result != null) {
                tvTitle.setText(result);
            }
        }
    }
}