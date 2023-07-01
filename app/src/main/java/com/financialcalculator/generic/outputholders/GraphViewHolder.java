package com.financialcalculator.generic.outputholders;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.model.GenericOutputEntity;
import com.financialcalculator.utility.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class GraphViewHolder extends RecyclerView.ViewHolder {

    ProgressBar progressPrincipal, progressInterest, progressInterestFull, progressPrincipalFull;
    TextView tvProgressInterestPercent, tvProgressInterest, tvTotalPrincipalPercent, tvTotalPrincipalValue;
    TextView tvIntTitle, tvPrincipalTitle;
    Context context;
    GenericOutputEntity genericOutputEntity;
    GraphViewEntity graphViewEntity;
    BigDecimal totalPrincipal, interest, totalPercent, interestPercent;
    public static final int ANIMATION_TIME = 2000;

    public GraphViewHolder(View itemView) {
        super(itemView);
        progressInterest = itemView.findViewById(R.id.progressInterest);
        progressPrincipal = itemView.findViewById(R.id.progressPrincipal);
        progressInterestFull = itemView.findViewById(R.id.progressInterestFull);
        progressPrincipalFull = itemView.findViewById(R.id.progressPrincipalFull);

        tvProgressInterestPercent = itemView.findViewById(R.id.tvProgressInterestPercent);
        tvProgressInterest = itemView.findViewById(R.id.tvProgressInterest);
        tvTotalPrincipalPercent = itemView.findViewById(R.id.tvTotalPrincipalPercent);
        tvTotalPrincipalValue = itemView.findViewById(R.id.tvTotalPrincipalValue);

        tvIntTitle = itemView.findViewById(R.id.tvIntTitle);
        tvPrincipalTitle = itemView.findViewById(R.id.tvPrincipalTitle);
    }

    public void setData(Context context, GenericOutputEntity genericOutputEntity) {
        this.context = context;
        this.genericOutputEntity = genericOutputEntity;
        new CalculateResult().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void animateProgressBar(ProgressBar progressBar) {
        progressBar.setProgress(100);
       /* ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100); // see this max value coming back here,// we animate towards that value
        animation.setDuration(0); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();*/
    }

    private void bindData() {
        try {
            animateProgressBar(progressInterestFull);
            animateProgressBar(progressPrincipalFull);

            tvPrincipalTitle.setText(graphViewEntity.getTotMsg());
            tvTotalPrincipalValue.setText(Util.getCommaSeparated(totalPrincipal.toPlainString()) + " " + genericOutputEntity.getCurr());
            tvTotalPrincipalPercent.setText("" + totalPercent + "%");

            tvIntTitle.setText(graphViewEntity.getIntMsg());
            tvProgressInterest.setText(Util.getCommaSeparated(interest.toPlainString()) + " " + genericOutputEntity.getCurr());
            tvProgressInterestPercent.setText("" + interestPercent + "%");

            animateProgressBar(progressPrincipal, totalPercent.intValue());
            animateProgressBar(progressInterest, interestPercent.intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private class CalculateResult extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Gson gson = new Gson();
            Type type = new TypeToken<GraphViewEntity>() {
            }.getType();
            graphViewEntity = gson.fromJson(genericOutputEntity.getData(), type);

            try {
                GenericCalculatorActivity activity = (GenericCalculatorActivity) context;
                totalPrincipal = Util.evaluate(graphViewEntity.getTotFormulae(), activity.getCalculatorEntity().inputHashmap);
                totalPrincipal = totalPrincipal.setScale(0, BigDecimal.ROUND_HALF_UP);

                interest = Util.evaluate(graphViewEntity.getIntFormulae(), activity.getCalculatorEntity().inputHashmap);
                interest = interest.setScale(0, BigDecimal.ROUND_HALF_UP);

                BigDecimal totIntPrin = Util.applyOp('+', totalPrincipal, interest);

                totalPercent = Util.applyOp('/', totIntPrin, totalPrincipal);
                totalPercent = Util.applyOp('*', new BigDecimal(100).setScale(0), totalPercent);
                totalPercent = totalPercent.setScale(0, BigDecimal.ROUND_HALF_UP);

                interestPercent = Util.applyOp('/', totIntPrin, interest);
                interestPercent = Util.applyOp('*', new BigDecimal(100).setScale(0), interestPercent);
                interestPercent = interestPercent.setScale(0, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }


        @Override
        protected void onPostExecute(Void bigDecimal) {
            super.onPostExecute(bigDecimal);
            bindData();

            //tvValue.setText(genericOutputEntity.getCurr() + " " + Util.getCommaSeparated(bigDecimal.toPlainString()));
        }
    }

    public void animateProgressBar(ProgressBar progressBar, int max) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, max); // see this max value coming back here,// we animate towards that value
        animation.setDuration(ANIMATION_TIME); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    class GraphViewEntity {
        String totMsg;
        String intMsg;
        String totFormulae;
        String intFormulae;

        public String getTotMsg() {
            return totMsg;
        }

        public void setTotMsg(String totMsg) {
            this.totMsg = totMsg;
        }

        public String getIntMsg() {
            return intMsg;
        }

        public void setIntMsg(String intMsg) {
            this.intMsg = intMsg;
        }

        public String getTotFormulae() {
            return totFormulae;
        }

        public void setTotFormulae(String totFormulae) {
            this.totFormulae = totFormulae;
        }

        public String getIntFormulae() {
            return intFormulae;
        }

        public void setIntFormulae(String intFormulae) {
            this.intFormulae = intFormulae;
        }
    }
}