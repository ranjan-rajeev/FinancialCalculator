package com.financialcalculator.loanprofile;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.roomdb.tables.GenericSearchHistoryEntity;
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;


public class LoanProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<GenericSearchHistoryEntity> detailsEntityList;

    public LoanProfileAdapter(Context context, List<GenericSearchHistoryEntity> list) {
        mContext = context;
        this.detailsEntityList = list;
    }

    public class EmiDetailsHolder extends RecyclerView.ViewHolder {
        TextView tvShortName, tvfullName, tvPaid, tvLeft, tvEnd;
        RelativeLayout llItem_details;
        ImageView ivDelete;
        CardView cvProfile;

        public EmiDetailsHolder(View view) {
            super(view);
            llItem_details = view.findViewById(R.id.llItem_details);
            tvShortName = (TextView) view.findViewById(R.id.tvShortName);
            tvfullName = (TextView) view.findViewById(R.id.tvfullName);
            tvPaid = (TextView) view.findViewById(R.id.tvPaid);
            tvLeft = (TextView) view.findViewById(R.id.tvLeft);
            tvEnd = (TextView) view.findViewById(R.id.tvEnd);
            cvProfile = (CardView) view.findViewById(R.id.cvProfile);
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
            ((EmiDetailsHolder) holder).cvProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ViewLoanProfile) mContext).redirectToCreateLoan(detailsEntity);
                }
            });

            switch (detailsEntity.getType()) {
                case Constants.LOAN_PROFILE:
                    bindLoanProfile(holder, detailsEntity);
                    break;
            }
        }
    }

    private String getLoanName(int loanType) {
        switch (loanType) {
            case 0:
                return "Home Loan";
            case 1:
                return "Personal Loan";
            case 2:
                return "Business Loan";
            default:
                return "Other Loan";
        }
    }

    private void bindLoanProfile(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            String fullName = obj.getString("profilename");
            ((EmiDetailsHolder) holder).tvShortName.setText("" + fullName.charAt(0));
            ((EmiDetailsHolder) holder).tvfullName.setText("" + fullName + "  -  " + getLoanName(obj.getInt("loantype")));
            ((EmiDetailsHolder) holder).tvEnd.setText("" + obj.getString("endDate"));

            ((EmiDetailsHolder) holder).tvLeft.setText("" + Util.getCommaSeparated("" + obj.getDouble("amountLeft")) + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvPaid.setText("" + Util.getCommaSeparated("" + obj.getDouble("principalPaidTillToday")) + " " + Constants.CURRENCY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return detailsEntityList.size();
    }

}