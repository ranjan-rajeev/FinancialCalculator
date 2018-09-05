package com.financialcalculator.dashboard;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.model.DashboardEntity;

import java.util.List;


public class DashboardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Fragment mContext;
    List<DashboardEntity> list;

    public DashboardItemAdapter(Fragment context, List<DashboardEntity> list) {
        mContext = context;
        this.list = list;
    }

    public class DashboardItemHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;

        public DashboardItemHolder(View view) {
            super(view);
            ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            tvName = (TextView) view.findViewById(R.id.tvName);
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
            ((DashboardItemHolder) holder).ivIcon.setImageResource(list.get(position).getIcon());
            ((DashboardItemHolder) holder).tvName.setText(list.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}