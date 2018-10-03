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
import com.financialcalculator.banking.fd.FDCalculatorActivity;
import com.financialcalculator.banking.ppf.PPFCalculatotActivity;
import com.financialcalculator.banking.rd.RDCalculatorActivity;
import com.financialcalculator.emi.emicalculator.EmiCalculatorActivity;
import com.financialcalculator.emi.emicompare.EmiCompareActivity;
import com.financialcalculator.emi.emifixedvsreducing.FixedVsReducingActivity;
import com.financialcalculator.gst.GstCalculatorActivity;
import com.financialcalculator.gst.VatCalculatorActivity;
import com.financialcalculator.model.DashboardEntity;
import com.financialcalculator.sip.LumpSumpSipActivity;
import com.financialcalculator.sip.SIPCalculatorActivity;
import com.financialcalculator.sip.SIPGoalCalculatorActivity;
import com.financialcalculator.utility.Constants;

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

        emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "EMI Calculator", R.drawable.emi_cal));
        emiCalList.add(new DashboardEntity(Constants.COMPARE_LOAN, "Compare Loan", R.drawable.compare_icon));
        emiCalList.add(new DashboardEntity(Constants.FLAT_VS_REDUCING, "Flat Vs \nReducing Rate", R.drawable.compare_icon));


        loanList = new ArrayList<>();
        loanList.add(new DashboardEntity(Constants.HOME_LOAN_CALCULATOR, "Home Loan\n Calculator", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(Constants.PERSONAL_LOAN_CALCULATOR, "Personal Loan\n Calculator", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(Constants.LAON_AGAINST_PROPERTY, "Loan Against\n Property", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(Constants.GOLD_LOAN_CALCULATOR, "Gold Loan\n Calculator", R.drawable.emi_cal));


        bankingList = new ArrayList<>();
        bankingList.add(new DashboardEntity(Constants.FD_CALCULATOR, "FD Calculator", R.drawable.emi_cal));
        bankingList.add(new DashboardEntity(Constants.RD_CALCULATOR, "RD Calculator", R.drawable.emi_cal));
        bankingList.add(new DashboardEntity(Constants.PPF_CALCULATOR, "PPF Calculator", R.drawable.emi_cal));
        //bankingList.add(new DashboardEntity(Constants.SI_CI_CALCULATOR, "Simple & Compound\n Interest", R.drawable.emi_cal));

        sipList = new ArrayList<>();
        sipList.add(new DashboardEntity(Constants.SIP_CALCULATOR, "Systematic \nInvestment Plan", R.drawable.emi_cal));
        sipList.add(new DashboardEntity(Constants.ADVANCE_SIP_CALCULATOR, "GOAL SIP \nCalculator", R.drawable.emi_cal));
        //sipList.add(new DashboardEntity(Constants.SYSTEMATIC_WITHDRAWAL_PLAN, "Systematic \nWithdrawal Plan", R.drawable.emi_cal));
        sipList.add(new DashboardEntity(Constants.LUMPSUMP_CALCULATOR, "Lumpsum SIP\nCalculator", R.drawable.emi_cal));


        gstList = new ArrayList<>();
        gstList.add(new DashboardEntity(Constants.GST_CALCULATOR, "GST Calculator", R.drawable.emi_cal));
        gstList.add(new DashboardEntity(Constants.VAT_CALCULATOR, "VAT Calculator", R.drawable.emi_cal));

       /* emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "EMI Calculator", R.drawable.ic_menu_camera));
        emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "Pro EMI Calculator", R.drawable.ic_menu_share));
        emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "Compare Loan", R.drawable.ic_menu_manage));
        emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "EMI Calculator", R.drawable.ic_menu_send));*/

    }

    private void setAdapters() {
        rvEmiCAl.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        dashboardItemAdapter = new DashboardItemAdapter(this, emiCalList);
        rvEmiCAl.setAdapter(dashboardItemAdapter);

        rvBanking.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        dashboardItemAdapter = new DashboardItemAdapter(this, bankingList);
        rvBanking.setAdapter(dashboardItemAdapter);

        rvGstVat.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        dashboardItemAdapter = new DashboardItemAdapter(this, gstList);
        rvGstVat.setAdapter(dashboardItemAdapter);

        rvLoan.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        dashboardItemAdapter = new DashboardItemAdapter(this, loanList);
        rvLoan.setAdapter(dashboardItemAdapter);

        rvSip.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
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
            case Constants.EMI_CALCULATOR:
                startActivity(new Intent(getActivity(), EmiCalculatorActivity.class));
                break;
            case Constants.COMPARE_LOAN:
                startActivity(new Intent(getActivity(), EmiCompareActivity.class));
                break;
            case Constants.FLAT_VS_REDUCING:
                startActivity(new Intent(getActivity(), FixedVsReducingActivity.class));
                break;
            case Constants.GST_CALCULATOR:
                startActivity(new Intent(getActivity(), GstCalculatorActivity.class));
                break;
            case Constants.VAT_CALCULATOR:
                startActivity(new Intent(getActivity(), VatCalculatorActivity.class));
                break;
            case Constants.FD_CALCULATOR:
                startActivity(new Intent(getActivity(), FDCalculatorActivity.class));
                break;
            case Constants.RD_CALCULATOR:
                startActivity(new Intent(getActivity(), RDCalculatorActivity.class));
                break;
            case Constants.PPF_CALCULATOR:
                startActivity(new Intent(getActivity(), PPFCalculatotActivity.class));
                break;
            case Constants.SIP_CALCULATOR:
                startActivity(new Intent(getActivity(), SIPCalculatorActivity.class));
                break;
            case Constants.ADVANCE_SIP_CALCULATOR:
                startActivity(new Intent(getActivity(), SIPGoalCalculatorActivity.class));
                break;
            case Constants.LUMPSUMP_CALCULATOR:
                startActivity(new Intent(getActivity(), LumpSumpSipActivity.class));
                break;

        }
    }
}
