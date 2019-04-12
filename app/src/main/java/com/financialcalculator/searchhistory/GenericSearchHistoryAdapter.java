package com.financialcalculator.searchhistory;

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
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class GenericSearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<GenericSearchHistoryEntity> detailsEntityList;

    public GenericSearchHistoryAdapter(Context context, List<GenericSearchHistoryEntity> list) {
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
            final GenericSearchHistoryEntity detailsEntity = detailsEntityList.get(position);
            /*if (position % 2 == 0) {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                ((EmiDetailsHolder) holder).llItem_details.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightGrey));
            }*/
            ((EmiDetailsHolder) holder).tvRate.setBackgroundResource(Util.getFixedBackground(position));
            switch (detailsEntity.getType()) {
                case Constants.EMI_CALCULATOR:
                    bindEmiCalculator(holder, detailsEntity);
                    break;
                case Constants.SIP_CALCULATOR:
                    bindSIPCalculator(holder, detailsEntity);
                    break;
                case Constants.ADVANCE_SIP_CALCULATOR:
                    bindGOALSIPCalculator(holder, detailsEntity);
                    break;
                case Constants.LUMPSUMP_CALCULATOR:
                    bindLUMPSUMCalculator(holder, detailsEntity);
                    break;
                case Constants.FD_CALCULATOR:
                    bindFDCalculator(holder, detailsEntity);
                    break;
                case Constants.RD_CALCULATOR:
                    bindRDCalculator(holder, detailsEntity);
                    break;
                case Constants.PPF_CALCULATOR:
                    bindPPFCalculator(holder, detailsEntity);
                    break;
            }

            /*((EmiDetailsHolder) holder).tvPrincipal.setText("" + detailsEntity.getId() + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvDate.setText(Util.getDatefromLong(detailsEntity.getUpdatedTime()));
            ((EmiDetailsHolder) holder).tvRate.setText("" + detailsEntity.getType() + "%");


            ((EmiDetailsHolder) holder).tvTerm.setText("" + detailsEntity.getId() + " " + detailsEntity.getType());*/


            ((EmiDetailsHolder) holder).llItem_details.setOnClickListener(new View.OnClickListener() {
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
            });
        }
    }

    private void bindEmiCalculator(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + Util.getNumberFormatted(obj.getString("amount"))
                    + " " + Constants.CURRENCY);
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

    private void bindSIPCalculator(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + Util.getNumberFormatted(obj.getString("amount"))
                    + " " + Constants.CURRENCY);
            //((EmiDetailsHolder) holder).tvDate.setText("" + obj.getString("tenuretype"));
            ((EmiDetailsHolder) holder).tvRate.setText("" + obj.getString("rate") + "%");
            ((EmiDetailsHolder) holder).tvTerm.setText("" + obj.getString("tenure") + " Months");
            ((EmiDetailsHolder) holder).tvDate.setText("" + getFrequencyType(obj.getInt("tenuretype")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getFrequencyType(int i) {
        if (i == 0) {
            return "Paying Monthly";
        } else if (i == 1) {
            return "Paying Quarterly";
        } else if (i == 2) {
            return "Paying Half Yearly";
        } else {
            return "Paying Yearly";
        }
    }

    private String getFDType(int i) {
        if (i == 0) {
            return "Cumulative";
        } else if (i == 1) {
            return "Quarterly Payout";
        } else if (i == 2) {
            return "Monthly Payout";
        } else {
            return "";
        }
    }

    private void bindGOALSIPCalculator(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + Util.getNumberFormatted(obj.getString("amount"))
                    + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvDate.setVisibility(View.GONE);
            ((EmiDetailsHolder) holder).tvRate.setText("" + obj.getString("rate") + "%");
            ((EmiDetailsHolder) holder).tvTerm.setText("" + obj.getString("tenure") + " Years");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindLUMPSUMCalculator(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + Util.getNumberFormatted(obj.getString("amount"))
                    + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvDate.setVisibility(View.GONE);
            ((EmiDetailsHolder) holder).tvRate.setText("" + obj.getString("rate") + "%");
            ((EmiDetailsHolder) holder).tvTerm.setText("" + obj.getString("tenure") + " Years");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindFDCalculator(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + Util.getNumberFormatted(obj.getString("amount"))
                    + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvRate.setText("" + obj.getString("rate") + "%");

            ((EmiDetailsHolder) holder).tvDate.setText("" + getFDType(obj.getInt("fdtype")));

            ((EmiDetailsHolder) holder).tvTerm.setText(""
                    + obj.getString("year") + " Years, "
                    + obj.getString("month") + " Months, "
                    + obj.getString("day") + " Days");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindRDCalculator(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + Util.getNumberFormatted(obj.getString("amount"))
                    + " " + Constants.CURRENCY);
            //((EmiDetailsHolder) holder).tvDate.setText("" + obj.getString("tenuretype"));
            ((EmiDetailsHolder) holder).tvRate.setText("" + obj.getString("rate") + "%");
            ((EmiDetailsHolder) holder).tvTerm.setText("" + obj.getString("tenure") + " Months");
            ((EmiDetailsHolder) holder).tvDate.setText("" + getFrequencyType(obj.getInt("tenuretype")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindPPFCalculator(RecyclerView.ViewHolder holder, GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {
            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            ((EmiDetailsHolder) holder).tvPrincipal.setText("" + Util.getNumberFormatted(obj.getString("amount"))
                    + " " + Constants.CURRENCY);
            ((EmiDetailsHolder) holder).tvRate.setText("" + obj.getString("rate") + "%");

            if (obj.getInt("deposittype") == 0) {
                ((EmiDetailsHolder) holder).tvDate.setText("Yearly Deposit");
            } else {
                ((EmiDetailsHolder) holder).tvDate.setText("Monthly Deposit");
            }


            ((EmiDetailsHolder) holder).tvTerm.setText("" + getPPFType(obj.getInt("term")));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getPPFType(int i) {
        if (i == 0) {
            return "15 Years";
        } else if (i == 1) {
            return "20 Years";
        } else if (i == 2) {
            return "25 Years";
        } else {
            return "30 Years";
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