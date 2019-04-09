package com.financialcalculator.loanprofile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.roomdb.tables.GenericSearchHistoryEntity;
import com.financialcalculator.searchhistory.SerachHistoryACtivity;
import com.financialcalculator.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class LoanProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<GenericSearchHistoryEntity> detailsEntityList;

    public LoanProfileAdapter(Context context, List<GenericSearchHistoryEntity> list) {
        mContext = context;
        this.detailsEntityList = list;
    }

    public class EmiDetailsHolder extends RecyclerView.ViewHolder {
        TextView tvPrincipal, tvDate, tvRate, tvTerm;
        RelativeLayout llItem_details;
        ImageView ivDelete;

        public EmiDetailsHolder(View view) {
            super(view);
            llItem_details = view.findViewById(R.id.llItem_details);
            tvPrincipal = (TextView) view.findViewById(R.id.tvPrincipal);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvRate = (TextView) view.findViewById(R.id.tvRate);
            tvTerm = (TextView) view.findViewById(R.id.tvTerm);
            ivDelete = view.findViewById(R.id.ivDelete);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_loan_profile, parent, false);
        return new EmiDetailsHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof EmiDetailsHolder) {
            final GenericSearchHistoryEntity detailsEntity = detailsEntityList.get(position);
            /*if (position % 2 == 0) {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightGrey));
            }*/

            switch (detailsEntity.getType()) {
                case Constants.EMI_CALCULATOR:
                    bindEmiCalculator(holder, detailsEntity);
                    break;
                case Constants.COMPARE_LOAN:
                    bindCompareLoan(holder, detailsEntity);
                    break;
            }

            /*((EmiDetailsHolder) holder).tvPrincipal.setText("" + detailsEntity.getId() + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvDate.setText(Util.getDatefromLong(detailsEntity.getUpdatedTime()));
            ((EmiDetailsHolder) holder).tvRate.setText("" + detailsEntity.getType() + "%");


            ((EmiDetailsHolder) holder).tvTerm.setText("" + detailsEntity.getId() + " " + detailsEntity.getType());*/


           /* ((EmiDetailsHolder) holder).llItem_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SerachHistoryACtivity) mContext).redirectToResACtivity(detailsEntity);
                }
            });
            ((EmiDetailsHolder) holder).ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SerachHistoryACtivity) mContext).deleteSearchItem(detailsEntity);
                }
            });*/
        }
    }

    private void bindEmiCalculator(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + obj.getString("amount") + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvDate.setText("" + obj.getString("date"));
            ((EmiDetailsHolder) holder).tvRate.setText("" + obj.getString("rate") + "%");

            if (obj.getString("tenuretype").equals("YEARS")) {
                ((EmiDetailsHolder) holder).tvTerm.setText("" + obj.getString("tenure") + " Years");
            } else {
                ((EmiDetailsHolder) holder).tvTerm.setText("" + obj.getString("tenure") + " Months");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindCompareLoan(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvRate.setVisibility(View.GONE);
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + obj.getString("amount") + " " + Constants.CURRENCY +
                    "" + obj.getString("amount2") + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvDate.setText("" + obj.getString("rate") + "%" + "" + obj.getString("rate2") + "%");


            if (obj.getString("tenuretype").equals("YEARS")) {
                ((EmiDetailsHolder) holder).tvTerm.setText("" + obj.getString("tenure") + " Years" + "" + obj.getString("tenure2") + " Years");
            } else {
                ((EmiDetailsHolder) holder).tvTerm.setText("" + obj.getString("tenure") + " Months" + "" + obj.getString("tenure2") + " Months");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return detailsEntityList.size();
    }

}