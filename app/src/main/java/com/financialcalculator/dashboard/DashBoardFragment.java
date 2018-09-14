package com.financialcalculator.dashboard;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.financialcalculator.R;
import com.financialcalculator.emi.emicompare.EmiCompareActivity;
import com.financialcalculator.emi.emicalculator.EmiCalculatorActivity;
import com.financialcalculator.emi.emifixedvsreducing.FixedVsReducingActivity;
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

        emiCalList.add(new DashboardEntity(++i, "EMI Calculator", R.drawable.emi_cal));
        emiCalList.add(new DashboardEntity(++i, "Compare Loan", R.drawable.compare_icon));
        emiCalList.add(new DashboardEntity(++i, "Flat Vs \nReducing Rate", R.drawable.compare_icon));


        loanList = new ArrayList<>();
        loanList.add(new DashboardEntity(++i, "Home Loan\n Calculator", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(++i, "Personal Loan\n Calculator", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(++i, "Loan Against\n Property", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(++i, "Gold Loan\n Calculator", R.drawable.emi_cal));


        bankingList = new ArrayList<>();
        bankingList.add(new DashboardEntity(++i, "FD Calculator", R.drawable.emi_cal));
        bankingList.add(new DashboardEntity(++i, "RD Calculator", R.drawable.emi_cal));
        bankingList.add(new DashboardEntity(++i, "PPF Calculator", R.drawable.emi_cal));
        bankingList.add(new DashboardEntity(++i, "Simple & Compound\n Interest", R.drawable.emi_cal));

        sipList = new ArrayList<>();
        sipList.add(new DashboardEntity(++i, "Systematic \nInvestment Plan", R.drawable.emi_cal));
        sipList.add(new DashboardEntity(++i, "Advance SIP \nCalculator", R.drawable.emi_cal));
        sipList.add(new DashboardEntity(++i, "Systematic \nWithdrawal Plan", R.drawable.emi_cal));
        sipList.add(new DashboardEntity(++i, "Lumpsum Calculator", R.drawable.emi_cal));


        gstList = new ArrayList<>();
        gstList.add(new DashboardEntity(++i, "GST Calculator", R.drawable.emi_cal));
        gstList.add(new DashboardEntity(++i, "VAT Calculator", R.drawable.emi_cal));

       /* emiCalList.add(new DashboardEntity(++i, "EMI Calculator", R.drawable.ic_menu_camera));
        emiCalList.add(new DashboardEntity(++i, "Pro EMI Calculator", R.drawable.ic_menu_share));
        emiCalList.add(new DashboardEntity(++i, "Compare Loan", R.drawable.ic_menu_manage));
        emiCalList.add(new DashboardEntity(++i, "EMI Calculator", R.drawable.ic_menu_send));*/

    }

    private void setAdapters() {
        rvEmiCAl.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, emiCalList);
        rvEmiCAl.setAdapter(dashboardItemAdapter);

        rvBanking.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, bankingList);
        rvBanking.setAdapter(dashboardItemAdapter);

        rvGstVat.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, gstList);
        rvGstVat.setAdapter(dashboardItemAdapter);

        rvLoan.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, loanList);
        rvLoan.setAdapter(dashboardItemAdapter);

        rvSip.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
        dashboardItemAdapter = new DashboardItemAdapter(this, sipList);
        rvSip.setAdapter(dashboardItemAdapter);
    }

    private void init_widgets(View view) {
        rvEmiCAl = view.findViewById(R.id.rvEmiCAl);
        rvLoan = view.findViewById(R.id.rvLoan);
        rvBanking = view.findViewById(R.id.rvBanking);
        rvSip = view.findViewById(R.id.rvSip);
        rvGstVat = view.findViewById(R.id.rvGstVat);
    }

    public void redirectToResACtivity(DashboardEntity dashboardEntity) {
        switch (dashboardEntity.getId()) {
            case 1:
                startActivity(new Intent(getActivity(), EmiCalculatorActivity.class));
                break;
            case 2:
                startActivity(new Intent(getActivity(), EmiCompareActivity.class));
                break;
            case 3:
                startActivity(new Intent(getActivity(), FixedVsReducingActivity.class));
                break;

        }
    }
}
