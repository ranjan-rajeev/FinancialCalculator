package com.financialcalculator.emicalculator;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.model.DetailsEntity;
import com.financialcalculator.model.YearsDetailsEntity;

import java.util.List;


public class YearEmiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<YearsDetailsEntity> detailsEntityList;

    public YearEmiAdapter(Context context, List<YearsDetailsEntity> list) {
        mContext = context;
        this.detailsEntityList = list;
    }

    public class EmiDetailsHolder extends RecyclerView.ViewHolder {
        TextView yearMonth, principalPaid, intPAid, balanceLeft;
        LinearLayout llItem_details, lldetails;
        RecyclerView rvMonthDetails;

        public EmiDetailsHolder(View view) {
            super(view);
            llItem_details = view.findViewById(R.id.llItem_details);
            lldetails = view.findViewById(R.id.lldetails);
            yearMonth = view.findViewById(R.id.yearMonth);
            principalPaid = (TextView) view.findViewById(R.id.principalPaid);
            intPAid = (TextView) view.findViewById(R.id.intPAid);
            balanceLeft = (TextView) view.findViewById(R.id.balanceLeft);
            rvMonthDetails = view.findViewById(R.id.rvMonthDetails);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_year_loan_details, parent, false);
        return new EmiDetailsHolder(view);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof EmiDetailsHolder) {
            final YearsDetailsEntity detailsEntity = detailsEntityList.get(position);
            if (position % 2 == 0) {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightGrey));
            }
            ((EmiDetailsHolder) holder).yearMonth.setText("" + detailsEntity.getYear());
            ((EmiDetailsHolder) holder).principalPaid.setText("" + detailsEntity.getPrincipal());
            ((EmiDetailsHolder) holder).intPAid.setText("" + detailsEntity.getInterest());
            ((EmiDetailsHolder) holder).balanceLeft.setText("" + detailsEntity.getBalance());

            if (detailsEntity.isChildVisible()) {
                ((EmiDetailsHolder) holder).rvMonthDetails.setVisibility(View.VISIBLE);
            } else {
                ((EmiDetailsHolder) holder).rvMonthDetails.setVisibility(View.GONE);
            }

            if (detailsEntity.getDetailsEntityList() != null && detailsEntity.getDetailsEntityList().size() > 0) {

                EmiAdapter emiAdapter = new EmiAdapter(mContext, detailsEntity.getDetailsEntityList());
                ((EmiDetailsHolder) holder).rvMonthDetails.setLayoutManager(new LinearLayoutManager(mContext));
                ((EmiDetailsHolder) holder).rvMonthDetails.setAdapter(emiAdapter);

            } else {
                ((EmiDetailsHolder) holder).rvMonthDetails.setAdapter(null);
            }
            ((EmiDetailsHolder) holder).yearMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (detailsEntity.isChildVisible()) {
                        ((EmiDetailsHolder) holder).rvMonthDetails.setVisibility(View.GONE);
                        detailsEntity.setChildVisible(false);
                        ((EmiDetailsHolder) holder).yearMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_box_black_24dp, 0, 0, 0);
                    } else {
                        ((EmiDetailsHolder) holder).rvMonthDetails.setVisibility(View.VISIBLE);
                        detailsEntity.setChildVisible(true);
                        ((EmiDetailsHolder) holder).yearMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_minus, 0, 0, 0);
                    }
                }
            });
            ((EmiDetailsHolder) holder).lldetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (detailsEntity.isChildVisible()) {
                        ((EmiDetailsHolder) holder).rvMonthDetails.setVisibility(View.GONE);
                        detailsEntity.setChildVisible(false);
                        ((EmiDetailsHolder) holder).yearMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_box_black_24dp, 0, 0, 0);
                    } else {
                        ((EmiDetailsHolder) holder).rvMonthDetails.setVisibility(View.VISIBLE);
                        detailsEntity.setChildVisible(true);
                        ((EmiDetailsHolder) holder).yearMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_minus, 0, 0, 0);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return detailsEntityList.size();
    }
}