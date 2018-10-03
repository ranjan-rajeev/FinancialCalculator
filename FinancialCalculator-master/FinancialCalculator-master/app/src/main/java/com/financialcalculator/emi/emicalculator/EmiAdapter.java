package com.financialcalculator.emi.emicalculator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.model.DetailsEntity;

import java.util.List;


public class EmiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<DetailsEntity> detailsEntityList;

    public EmiAdapter(Context context, List<DetailsEntity> list) {
        mContext = context;
        this.detailsEntityList = list;
    }

    public class EmiDetailsHolder extends RecyclerView.ViewHolder {
        TextView yearMonth, principalPaid, intPAid, balanceLeft;
        LinearLayout llItem_details;

        public EmiDetailsHolder(View view) {
            super(view);
            llItem_details = (LinearLayout) view.findViewById(R.id.llItem_details);
            yearMonth = (TextView) view.findViewById(R.id.yearMonth);
            principalPaid = (TextView) view.findViewById(R.id.principalPaid);
            intPAid = (TextView) view.findViewById(R.id.intPAid);
            balanceLeft = (TextView) view.findViewById(R.id.balanceLeft);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_loan_details, parent, false);
        return new EmiDetailsHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof EmiDetailsHolder) {
            DetailsEntity detailsEntity = detailsEntityList.get(position);
          /*  if (position % 2 == 0) {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightGrey));
            }*/
            ((EmiDetailsHolder) holder).yearMonth.setText("" + detailsEntity.getMonth());
            ((EmiDetailsHolder) holder).principalPaid.setText("" + detailsEntity.getPrincipal());
            ((EmiDetailsHolder) holder).intPAid.setText("" + detailsEntity.getInterest());
            ((EmiDetailsHolder) holder).balanceLeft.setText("" + detailsEntity.getBalance());
        }
    }

    @Override
    public int getItemCount() {
        return detailsEntityList.size();
    }
}