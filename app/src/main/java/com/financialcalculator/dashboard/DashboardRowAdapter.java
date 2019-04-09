package com.financialcalculator.dashboard;

import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.model.DashBoardRowEntity;
import com.financialcalculator.model.DashboardEntity;

import java.util.List;


public class DashboardRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Fragment mContext;
    List<DashBoardRowEntity> list;

    public DashboardRowAdapter(Fragment context, List<DashBoardRowEntity> list) {
        mContext = context;
        this.list = list;
    }

    public class DashboardItemHolder extends RecyclerView.ViewHolder {
        TextView title;
        RecyclerView rvEmiCAl;

        public DashboardItemHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            rvEmiCAl = (RecyclerView) view.findViewById(R.id.rvEmiCAl);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_dashboard, parent, false);
        return new DashboardItemHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof DashboardItemHolder) {
            final DashBoardRowEntity dashboardEntity = list.get(position);
            ((DashboardItemHolder) holder).title.setText(dashboardEntity.getTitle());

            DashboardItemAdapter dashboardItemAdapter = new DashboardItemAdapter(mContext, dashboardEntity.getDashboardEntities());
            ((DashboardItemHolder) holder).rvEmiCAl.setLayoutManager(new GridLayoutManager(mContext.getActivity(), 3));
            ((DashboardItemHolder) holder).rvEmiCAl.setAdapter(dashboardItemAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}