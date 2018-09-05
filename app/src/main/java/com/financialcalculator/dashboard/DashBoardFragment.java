package com.financialcalculator.dashboard;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.financialcalculator.R;
import com.financialcalculator.model.DashboardEntity;

import java.util.ArrayList;
import java.util.List;

public class DashBoardFragment extends Fragment {
    View view;
    RecyclerView rvEmiCAl, rvLoan, rvBanking, rvSip, rvGstVat;
    DashboardItemAdapter dashboardItemAdapter;
    List<DashboardEntity> emiCalList, loanList, bankingList, sipList, gstList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        init_widgets(view);
        init_lists();
        setAdapters();
        return view;
    }

    private void init_lists() {
        int i = 0;
        emiCalList = new ArrayList<DashboardEntity>();
        emiCalList.add(new DashboardEntity(++i, "EMI Calculator", R.drawable.ic_menu_camera));
        emiCalList.add(new DashboardEntity(++i, "Pro EMI Calculator", R.drawable.ic_menu_share));
        emiCalList.add(new DashboardEntity(++i, "Compare Loan", R.drawable.ic_menu_manage));
        emiCalList.add(new DashboardEntity(++i, "EMI Calculator", R.drawable.ic_menu_send));/*
        emiCalList.add(new DashboardEntity(++i, "EMI Calculator", R.drawable.ic_menu_camera));
        emiCalList.add(new DashboardEntity(++i, "Pro EMI Calculator", R.drawable.ic_menu_share));
        emiCalList.add(new DashboardEntity(++i, "Compare Loan", R.drawable.ic_menu_manage));
        emiCalList.add(new DashboardEntity(++i, "EMI Calculator", R.drawable.ic_menu_send));*/

    }

    private void setAdapters() {
        rvEmiCAl.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, emiCalList);
        rvEmiCAl.setAdapter(dashboardItemAdapter);

        rvBanking.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, emiCalList);
        rvBanking.setAdapter(dashboardItemAdapter);

        rvGstVat.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, emiCalList);
        rvGstVat.setAdapter(dashboardItemAdapter);

        rvLoan.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, emiCalList);
        rvLoan.setAdapter(dashboardItemAdapter);

        rvSip.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, emiCalList);
        rvSip.setAdapter(dashboardItemAdapter);
    }

    private void init_widgets(View view) {
        rvEmiCAl = view.findViewById(R.id.rvEmiCAl);
        rvLoan = view.findViewById(R.id.rvLoan);
        rvBanking = view.findViewById(R.id.rvBanking);
        rvSip = view.findViewById(R.id.rvSip);
        rvGstVat = view.findViewById(R.id.rvGstVat);
    }
}
