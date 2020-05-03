package com.financialcalculator.newdashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.dashboard.DashBoardFragment;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.DashboardEntity;
import com.financialcalculator.utility.GenericImageLoader;
import com.financialcalculator.utility.Util;

import java.util.List;


public class NewDashboardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<CalculatorEntity> list;

    public NewDashboardItemAdapter(Context context, List<CalculatorEntity> list) {
        mContext = context;
        this.list = list;
    }

    public class DashboardItemHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        RelativeLayout rl;

        public DashboardItemHolder(View view) {
            super(view);
            ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            tvName = (TextView) view.findViewById(R.id.tvName);
            rl = view.findViewById(R.id.rl);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_dashboard, parent, false);
        return new DashboardItemHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof DashboardItemHolder) {
            final CalculatorEntity calculatorEntity = list.get(position);
            // ((DashboardItemHolder) holder).ivIcon.setImageResource(calculatorEntity.getIcon());
            ((DashboardItemHolder) holder).tvName.setText(calculatorEntity.getCalName());
            ((DashboardItemHolder) holder).rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (calculatorEntity.getRedUrl().equalsIgnoreCase("GenericCalculatorActivity")) {
                        Intent intent = new Intent(mContext, GenericCalculatorActivity.class);
                        intent.putExtra("CALCULATOR", calculatorEntity);
                        mContext.startActivity(intent);
                    } else {
                        Util.inAppRedirection(mContext, calculatorEntity.getRedUrl(), calculatorEntity.getCalName());
                    }

                    //((DashBoardFragment) mContext).redirectToResACtivity(dashboardEntity);
                }
            });

            if (!calculatorEntity.getIconUrl().equals("") ) {
                GenericImageLoader.loadImage(mContext, ((DashboardItemHolder) holder).ivIcon, calculatorEntity.getIconUrl(), 0);
            } else {
                ((DashboardItemHolder) holder).ivIcon.setImageResource(Util.getCalculatorIcon(calculatorEntity.getCalId()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}