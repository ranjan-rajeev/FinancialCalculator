package com.financialcalculator.generic;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.model.MoreInfoEntity;
import com.financialcalculator.utility.Util;

public class MoreInfoViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle, tvCount;
    Context context;
    LinearLayout mainLayout;
    MoreInfoEntity moreInfoEntity;

    public MoreInfoViewHolder(View itemView) {
        super(itemView);
        this.tvTitle = itemView.findViewById(R.id.tvTitle);
        this.mainLayout = itemView.findViewById(R.id.mainLayout);
    }

    public void setData(Context context, MoreInfoEntity moreInfoEntity, int position) {
        this.context = context;
        this.moreInfoEntity = moreInfoEntity;
        if (position % 2 == 0) {
            mainLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_light));
        } else {
            mainLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey));
        }
        new CalculateResult().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private class CalculateResult extends AsyncTask<Void, Void, SpannableStringBuilder> {

        @Override
        protected SpannableStringBuilder doInBackground(Void... voids) {
            try {
                GenericCalculatorActivity activity = (GenericCalculatorActivity) context;
                return Util.evaluateString(moreInfoEntity.getAnswer(), activity.getCalculatorEntity().inputHashmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(SpannableStringBuilder result) {
            super.onPostExecute(result);
            if (result != null) {
                tvTitle.setText(Html.fromHtml(result.toString()));
            }
        }
    }
}