package com.financialcalculator.emi.emicalculator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.roomdb.tables.EMISearchHistoryEntity;
import com.financialcalculator.searchhistory.SerachHistoryACtivity;
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class EmiSearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<EMISearchHistoryEntity> detailsEntityList;

    public EmiSearchHistoryAdapter(Context context, List<EMISearchHistoryEntity> list) {
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
                R.layout.item_emi_history, parent, false);
        return new EmiDetailsHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof EmiDetailsHolder) {
            final EMISearchHistoryEntity detailsEntity = detailsEntityList.get(position);
            /*if (position % 2 == 0) {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightGrey));
            }*/
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + detailsEntity.getPrincipalAmt() + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvDate.setText(Util.getDatefromLong(detailsEntity.getUpdatedTime()));
            ((EmiDetailsHolder) holder).tvRate.setText("" + detailsEntity.getRoi() + "%");
            ((EmiDetailsHolder) holder).tvTerm.setText("" + detailsEntity.getLoanTenure() + " " + detailsEntity.getLoanTenureTYpe());
            ((EmiDetailsHolder) holder).llItem_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SerachHistoryACtivity) mContext).redirectToEmiCAlculator(detailsEntity);
                }
            });
            ((EmiDetailsHolder) holder).ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SerachHistoryACtivity) mContext).deleteEmiCAlculatot(detailsEntity);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return detailsEntityList.size();
    }

}