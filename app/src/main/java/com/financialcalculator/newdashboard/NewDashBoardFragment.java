package com.financialcalculator.newdashboard;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.BuildConfig;
import com.financialcalculator.R;
import com.financialcalculator.banking.fd.FDCalculatorActivity;
import com.financialcalculator.banking.ppf.PPFCalculatotActivity;
import com.financialcalculator.banking.rd.RDCalculatorActivity;
import com.financialcalculator.dashboard.DashboardRowAdapter;
import com.financialcalculator.emi.emicalculator.EmiCalculatorActivity;
import com.financialcalculator.emi.emicompare.EmiCompareActivity;
import com.financialcalculator.emi.emifixedvsreducing.FixedVsReducingActivity;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.gst.GstCalculatorActivity;
import com.financialcalculator.gst.VatCalculatorActivity;
import com.financialcalculator.home.MainActivity;
import com.financialcalculator.loanprofile.CreateLoanProfileActivity;
import com.financialcalculator.loanprofile.ViewLoanProfile;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.DashBoardRowEntity;
import com.financialcalculator.model.DashboardEntity;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.model.InputTypeEntity;
import com.financialcalculator.sip.LumpSumpSipActivity;
import com.financialcalculator.sip.SIPCalculatorActivity;
import com.financialcalculator.sip.SIPGoalCalculatorActivity;
import com.financialcalculator.utility.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewDashBoardFragment extends Fragment {

    public static final String TAG = "NewDashBoard";
    View view;
    private AdView mAdView;
    RecyclerView rvDashboard;
    List<DashBoardRowEntity> dashBoardRowEntities;
    DashboardRowAdapter dashboardRowAdapter;
    List<DashboardEntity> emiCalList, loanList, bankingList, sipList, gstList, loanProfile;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    HomePageAdapter homePageAdapter;
    List<HomePageModel> homePageModels = new ArrayList<>();
    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //setUPAdd(view);
        //appDatabase = RoomDatabase.getAppDatabase(getActivity().getApplicationContext());
        init_widgets(view);
        //new InitList().execute();
        // setAdapters();
        getDashBoardListFirebase();
        return view;
    }

    public void getDashBoardListFirebase() {
        DatabaseReference myRef = database.getReference("components");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        HomePageModel homePageModel = snapshot.getValue(HomePageModel.class);
                        homePageModels.add(homePageModel);
                        /*if (homePageModel.getCmpType() == 5) {
                            Gson gson = new Gson();
                            Type userListType = new TypeToken<ArrayList<CalculatorEntity>>() {
                            }.getType();
                            ArrayList<CalculatorEntity> userArray = gson.fromJson(homePageModel.getCmpData(), userListType);


                            Type inputTypeToken = new TypeToken<ArrayList<InputTypeEntity>>() {
                            }.getType();
                            ArrayList<InputTypeEntity> inputTypeEntities = gson.fromJson(userArray.get(2).getInput(), inputTypeToken);
                            Log.d(TAG, "" + inputTypeEntities.size());
                        }*/
                        Log.d(TAG, "" + homePageModel.getFirebaseId());
                    } catch (Exception e) {
                        Log.d(TAG, "Unable to parse");
                    }
                }
                homePageAdapter = new HomePageAdapter(homePageModels, activity);
                rvDashboard.setAdapter(homePageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void init_lists() {
        int i = 0;
        emiCalList = new ArrayList<DashboardEntity>();

        emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "EMI Calculator", R.drawable.emi_cal));
        emiCalList.add(new DashboardEntity(Constants.COMPARE_LOAN, "Compare Loan", R.drawable.compare_loan_new));
        emiCalList.add(new DashboardEntity(Constants.FLAT_VS_REDUCING, "Flat Vs \nReducing Rate", R.drawable.compare_icon));


        loanList = new ArrayList<>();
        loanList.add(new DashboardEntity(Constants.HOME_LOAN_CALCULATOR, "Home Loan\n Calculator", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(Constants.PERSONAL_LOAN_CALCULATOR, "Personal Loan\n Calculator", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(Constants.LAON_AGAINST_PROPERTY, "Loan Against\n Property", R.drawable.emi_cal));
        loanList.add(new DashboardEntity(Constants.GOLD_LOAN_CALCULATOR, "Gold Loan\n Calculator", R.drawable.emi_cal));


        bankingList = new ArrayList<>();
        bankingList.add(new DashboardEntity(Constants.FD_CALCULATOR, "FD Calculator", R.drawable.fd));
        bankingList.add(new DashboardEntity(Constants.RD_CALCULATOR, "RD Calculator", R.drawable.emi_cal));
        bankingList.add(new DashboardEntity(Constants.PPF_CALCULATOR, "PPF Calculator", R.drawable.ppf));
        //bankingList.add(new DashboardEntity(Constants.SI_CI_CALCULATOR, "Simple & Compound\n Interest", R.drawable.emi_cal));

        sipList = new ArrayList<>();
        sipList.add(new DashboardEntity(Constants.SIP_CALCULATOR, "Systematic \nInvestment Plan", R.drawable.sip_icons));
        sipList.add(new DashboardEntity(Constants.ADVANCE_SIP_CALCULATOR, "GOAL SIP \nCalculator", R.drawable.goal));
        //sipList.add(new DashboardEntity(Constants.SYSTEMATIC_WITHDRAWAL_PLAN, "Systematic \nWithdrawal Plan", R.drawable.emi_cal));
        sipList.add(new DashboardEntity(Constants.LUMPSUMP_CALCULATOR, "Lumpsum SIP\nCalculator", R.drawable.lumpsum));


        gstList = new ArrayList<>();
        gstList.add(new DashboardEntity(Constants.GST_CALCULATOR, "GST Calculator", R.drawable.gst));
        gstList.add(new DashboardEntity(Constants.VAT_CALCULATOR, "VAT Calculator", R.drawable.vat));


        loanProfile = new ArrayList<>();
        loanProfile.add(new DashboardEntity(Constants.LOAN_PROFILE, "Create Loan Profile", R.drawable.create_loan_));
        loanProfile.add(new DashboardEntity(Constants.LOAN_PROFILE_VIEW, "View Loan Profile", R.drawable.view_loan_));
        loanProfile.add(new DashboardEntity(Constants.HOME_LOAN_ELIGIBLE, "Home Loan Eligibility", R.drawable.home_loan));
       /* emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "EMI Calculator", R.drawable.ic_menu_camera));
        emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "Pro EMI Calculator", R.drawable.ic_menu_share));
        emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "Compare Loan", R.drawable.ic_menu_manage));
        emiCalList.add(new DashboardEntity(Constants.EMI_CALCULATOR, "EMI Calculator", R.drawable.ic_menu_send));*/

        dashBoardRowEntities = new ArrayList<>();

        dashBoardRowEntities.add(new DashBoardRowEntity(++i, "EMI Calculators", emiCalList));
        dashBoardRowEntities.add(new DashBoardRowEntity(++i, "Loan Profile", loanProfile));
        dashBoardRowEntities.add(new DashBoardRowEntity(++i, "Mutual Funds & SIP", sipList));
        dashBoardRowEntities.add(new DashBoardRowEntity(++i, "Banking Calculations", bankingList));
        dashBoardRowEntities.add(new DashBoardRowEntity(++i, "GST", gstList));


    }

    private void init_widgets(View view) {
        rvDashboard = view.findViewById(R.id.rvDashboard);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDashboard.setLayoutManager(layoutManager);
    }

    public void redirectToResACtivity(DashboardEntity dashboardEntity) {
        switch (dashboardEntity.getId()) {
            case Constants.EMI_CALCULATOR:
                startActivity(new Intent(getActivity(), EmiCalculatorActivity.class));
               /* if (emiSearchHistoryEntityLIst != null && emiSearchHistoryEntityLIst.size() > 0) {
                    startActivity(new Intent(getActivity(), SerachHistoryACtivity.class)
                            .putExtra("TYPE", Constants.EMI_CALCULATOR)
                            .putParcelableArrayListExtra("LIST", (ArrayList<? extends Parcelable>) emiSearchHistoryEntityLIst));
                } else {
                    startActivity(new Intent(getActivity(), EmiCalculatorActivity.class));
                }*/

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

            case Constants.LOAN_PROFILE:
                startActivity(new Intent(getActivity(), CreateLoanProfileActivity.class));
                break;
            case Constants.LOAN_PROFILE_VIEW:
                startActivity(new Intent(getActivity(), ViewLoanProfile.class));
                break;
            case Constants.HOME_LOAN_ELIGIBLE:
                startActivity(new Intent(getActivity(), GenericCalculatorActivity.class));
                break;

        }
    }

    private class InitList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            init_lists();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dashBoardRowEntities != null) {
                dashboardRowAdapter = new DashboardRowAdapter(NewDashBoardFragment.this, dashBoardRowEntities);
                rvDashboard.setAdapter(dashboardRowAdapter);
            }

        }
    }
}
