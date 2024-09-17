package com.financialcalculator.generic.outputholders;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.model.GenericOutputEntity;
import com.financialcalculator.utility.Util;

import java.math.BigDecimal;

public class TextWithValueViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle, tvValue;
    Context context;
    GenericOutputEntity genericOutputEntity;

    public TextWithValueViewHolder(View itemView) {
        super(itemView);
        this.tvTitle = itemView.findViewById(R.id.tvTitle);
        this.tvValue = itemView.findViewById(R.id.tvValue);
    }

    public void setData(Context context, GenericOutputEntity genericOutputEntity) {
        this.context = context;
        this.genericOutputEntity = genericOutputEntity;
        tvTitle.setText(genericOutputEntity.getOutMsg());
        new CalculateResult().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private class CalculateResult extends AsyncTask<Void, Void, BigDecimal> {

        @Override
        protected BigDecimal doInBackground(Void... voids) {
            String exp = genericOutputEntity.getOutMsg();

            BigDecimal result = new BigDecimal(0).setScale(0);
            try {
                GenericCalculatorActivity activity = (GenericCalculatorActivity) context;
                result = Util.evaluate(genericOutputEntity.getFormulae(), activity.getCalculatorEntity().inputHashmap);
                result = result.setScale(0, BigDecimal.ROUND_HALF_UP);
                ((GenericCalculatorActivity) context).setHashMapValue(genericOutputEntity.getOutKey().charAt(0), result);
            } catch (Exception e) {
                e.printStackTrace();
                ((GenericCalculatorActivity) context).setHashMapValue(genericOutputEntity.getOutKey().charAt(0), result);
            }
            return result;
        }


        @Override
        protected void onPostExecute(BigDecimal bigDecimal) {
            super.onPostExecute(bigDecimal);
            if (genericOutputEntity.getCurr() != null && !genericOutputEntity.getCurr().equals("")) {
                tvValue.setText(Util.getCommaSeparated(bigDecimal.toPlainString()) + " " + genericOutputEntity.getCurr());
            } else {
                tvValue.setText(Util.getCommaSeparated(bigDecimal.toPlainString()));
            }

        }
    }
}