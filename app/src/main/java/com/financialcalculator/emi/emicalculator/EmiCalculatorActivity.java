package com.financialcalculator.emi.emicalculator;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.model.DetailsEntity;
import com.financialcalculator.model.YearsDetailsEntity;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.roomdb.tables.EMISearchHistoryEntity;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

public class EmiCalculatorActivity extends BaseActivity implements View.OnClickListener {

    TextView tvEmi, tvTotalPayable, tvProgressInterestPercent,
            tvProgressInterest, tvTotalPrincipalPercent, tvTotalPrincipalValue;

    TextView tvCalculate, tvDetails;
    NestedScrollView scrollView;
    CardView cvInput, cvDetails, cvResult;
    LinearLayout llEmiCAl;
    EditText etTenure, etInterest, etPrincipal;
    RecyclerView rvEMiDEtails;
    EmiAdapter emiAdapter;
    YearEmiAdapter yearEmiAdapter;
    List<DetailsEntity> detailsEntityList;
    List<YearsDetailsEntity> yearsDetailsEntities;
    ProgressBar progressPrincipal, progressInterest, progressInterestFull, progressPrincipalFull;
    RadioGroup rgYearMonth;
    RadioButton rbYear, rbMonth;

    public static final int ANIMATION_TIME = 3000;
    String[] monthName = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL",
            "AUG", "SEP", "OCT", "NOV",
            "DEC"};

    List<EMISearchHistoryEntity> emiSearchHistoryEntities;
    boolean isEdit = false;
    RoomDatabase roomDatabase;
    EMISearchHistoryEntity emiSearchHistoryEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_emi_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        roomDatabase = RoomDatabase.getAppDatabase(this);


        init();
        init_views();
        setListener();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        animateProgressBar(progressInterestFull);
        animateProgressBar(progressPrincipalFull);

        if (getIntent().hasExtra("LIST")) {
            emiSearchHistoryEntity = getIntent().getExtras().getParcelable("LIST");
            isEdit = true;
            bindValues(emiSearchHistoryEntity);

        }


    }

    private void bindValues(EMISearchHistoryEntity emiSearchHistoryEntity) {
        rgYearMonth.setOnCheckedChangeListener(null);
        if (emiSearchHistoryEntity.getLoanTenureTYpe().equals("YEARS")) {
            rbYear.setChecked(true);
        } else {
            rbMonth.setChecked(true);
        }
        etInterest.setText("" + emiSearchHistoryEntity.getRoi());
        etPrincipal.setText("" + emiSearchHistoryEntity.getPrincipalAmt());
        etTenure.setText("" + emiSearchHistoryEntity.getLoanTenure());
        rgYearMonth.setOnCheckedChangeListener(onCheckedChangeListener);
        tvDetails.performClick();
        hideKeyBoard(etInterest, this);
        scrollToRow(scrollView, llEmiCAl, cvInput);
    }

    private void init_views() {
        cvInput.setVisibility(View.VISIBLE);
        cvResult.setVisibility(GONE);
        cvDetails.setVisibility(GONE);


    }

    public void animateProgressBar(ProgressBar progressBar, int max) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, max); // see this max value coming back here,// we animate towards that value
        animation.setDuration(ANIMATION_TIME); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public void animateProgressBar(ProgressBar progressBar) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100); // see this max value coming back here,// we animate towards that value
        animation.setDuration(0); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void setListener() {
        tvCalculate.setOnClickListener(this);
        tvDetails.setOnClickListener(this);
        rgYearMonth.setOnCheckedChangeListener(onCheckedChangeListener);
        /*rgYearMonth.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (rbMonth.isChecked()) {
                    if (!etTenure.getText().toString().equals("")) {
                        double year = Double.parseDouble(etTenure.getText().toString());
                        etTenure.setText("" + Math.round(year * 12));
                    }
                } else if (rbYear.isChecked()) {
                    if (!etTenure.getText().toString().equals("")) {
                        double month = Double.parseDouble(etTenure.getText().toString());
                        etTenure.setText("" + (month / 12));
                    }
                }
            }
        });*/
    }

    //region checked change listener
    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (rbMonth.isChecked()) {
                if (!etTenure.getText().toString().equals("")) {
                    double year = Double.parseDouble(etTenure.getText().toString());
                    etTenure.setText("" + Math.round(year * 12));
                }
            } else if (rbYear.isChecked()) {
                if (!etTenure.getText().toString().equals("")) {
                    double month = Double.parseDouble(etTenure.getText().toString());
                    etTenure.setText("" + (month / 12));
                }
            }
        }
    };
    //endregion

    private void init() {
        rgYearMonth = findViewById(R.id.rgYearMonth);
        rbYear = findViewById(R.id.rbYear);
        rbMonth = findViewById(R.id.rbMonth);
        tvEmi = findViewById(R.id.tvEmi);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);
        tvProgressInterestPercent = findViewById(R.id.tvProgressInterestPercent);
        tvProgressInterest = findViewById(R.id.tvProgressInterest);
        tvTotalPrincipalPercent = findViewById(R.id.tvTotalPrincipalPercent);
        tvTotalPrincipalValue = findViewById(R.id.tvTotalPrincipalValue);

        etPrincipal = findViewById(R.id.etPrincipal);
        etInterest = findViewById(R.id.etInterest);
        etTenure = findViewById(R.id.etTenure);

        cvDetails = findViewById(R.id.cvDetails);
        cvInput = findViewById(R.id.cvInput);
        cvResult = findViewById(R.id.cvResult);
        llEmiCAl = findViewById(R.id.llEmiCAl);

        progressInterest = findViewById(R.id.progressInterest);
        progressPrincipal = findViewById(R.id.progressPrincipal);
        progressInterestFull = findViewById(R.id.progressInterestFull);
        progressPrincipalFull = findViewById(R.id.progressPrincipalFull);
        scrollView = findViewById(R.id.scrollView);
        tvCalculate = findViewById(R.id.tvCalculate);
        tvDetails = findViewById(R.id.tvDetails);
        rvEMiDEtails = findViewById(R.id.rvEMiDEtails);
        rvEMiDEtails.setLayoutManager(new LinearLayoutManager(this));

    }

    private void scrollToRow(final NestedScrollView scrollView, final LinearLayout linearLayout, final View textViewToShow) {
        long delay = 200; //delay to let finish with possible modifications to ScrollView
        scrollView.postDelayed(new Runnable() {
            public void run() {
                Rect textRect = new Rect(); //coordinates to scroll to
                textViewToShow.getHitRect(textRect); //fills textRect with coordinates of TextView relative to its parent (LinearLayout)
                scrollView.requestChildRectangleOnScreen(linearLayout, textRect, false); //ScrollView will make sure, the given textRect is visible
            }
        }, delay);
    }


    @Override
    public void onClick(View view) {
        hideKeyBoard(view, this);
        switch (view.getId()) {

            case R.id.tvCalculate:
                if (isValidInput()) {
                    scrollToRow(scrollView, llEmiCAl, cvResult);
                    calculateEmi(etPrincipal.getText().toString(), etInterest.getText().toString(), etTenure.getText().toString());
                }
                break;
            case R.id.tvDetails:
                if (isValidInput()) {
                    if (!isEdit) {

                        emiSearchHistoryEntity = new EMISearchHistoryEntity(Constants.EMI_CALCULATOR,
                                etPrincipal.getText().toString(), etInterest.getText().toString(), etTenure.getText().toString(), "MONTHS");

                        if (rbYear.isChecked()) {
                            emiSearchHistoryEntity.setLoanTenureTYpe("YEARS");
                        }
                        isEdit = true;
                        new InsertEMiHistory().execute();
                    } else {
                        if (rbYear.isChecked()) {
                            emiSearchHistoryEntity.setLoanTenureTYpe("YEARS");
                        } else {
                            emiSearchHistoryEntity.setLoanTenureTYpe("MONTHS");
                        }
                        emiSearchHistoryEntity.setLoanTenure(etTenure.getText().toString());

                        emiSearchHistoryEntity.setPrincipalAmt(etPrincipal.getText().toString());
                        emiSearchHistoryEntity.setRoi(etInterest.getText().toString());
                        emiSearchHistoryEntity.setUpdatedTime(Calendar.getInstance().getTimeInMillis());
                        new UpdateEMiHistory().execute();
                    }
                    showLayouts();
                    new AsyncCalculateEMiDetails().execute();
                    calculateEmi(etPrincipal.getText().toString(), etInterest.getText().toString(), etTenure.getText().toString());
                    break;
                }

        }

    }

    private void showLayouts() {
        cvResult.setVisibility(View.VISIBLE);
        cvDetails.setVisibility(View.VISIBLE);
    }


    public boolean isValidInput() {
        if (etPrincipal.getText().toString().equals("")) {
            etPrincipal.requestFocus();
            etPrincipal.setError("Enter Principal");
            return false;
        }
        if (etInterest.getText().toString().equals("")) {
            etInterest.requestFocus();
            etInterest.setError("Enter Rate of Interest");
            return false;
        }
        if (etTenure.getText().toString().equals("")) {
            etTenure.requestFocus();
            etTenure.setError("Enter Tenure");
            return false;
        }
        return true;
    }

    public List<DetailsEntity> getListDetailsMonthly(String Principal, String Interest, String tenure) {

        List<DetailsEntity> detailsEntities = new ArrayList<>();


        double principal = Double.parseDouble(Principal);
        double totalPrincipal = principal;
        double interestPercent = Double.parseDouble(Interest);
        double timeInMonth = Double.parseDouble(tenure);

        double effectiveROI = interestPercent / 1200;
        double top = Math.pow((1 + effectiveROI), timeInMonth);
        double emi = (principal * effectiveROI * (top / (top - 1)));


        for (int i = 1; i <= timeInMonth; i++) {

            double interestPAid = (effectiveROI * principal);
            double principalPaid = (emi - interestPAid);
            principal = (principal - principalPaid);
            double loanPAidPer = 100 - (principal / totalPrincipal);

            DetailsEntity detailsEntity = new DetailsEntity(0, "" + i, (principalPaid), (interestPAid), emi, principal, loanPAidPer);
            detailsEntities.add(detailsEntity);
        }


        return detailsEntities;
    }

    public List<YearsDetailsEntity> getListDetailsYearly(String Principal, String Interest, String tenure) {


        List<YearsDetailsEntity> yearsDetailsEntities = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        int currMonth = calendar.get(Calendar.MONTH);


        List<DetailsEntity> detailsEntities = new ArrayList<>();


        double principal = Double.parseDouble(Principal);
        double totalPrincipal = principal;
        double interestPercent = Double.parseDouble(Interest);
        double timeInMonth = Double.parseDouble(tenure);

        if (rbYear.isChecked()) {
            timeInMonth = timeInMonth * 12;
        }

        double effectiveROI = interestPercent / 1200;
        double top = Math.pow((1 + effectiveROI), timeInMonth);
        double emi = (principal * effectiveROI * (top / (top - 1)));


        double totalPrincipalPAid = 0;
        double totalInterestPAid = 0;
        double totalPayment = 0;

        for (int i = 1; i <= timeInMonth; i++) {

            double interestPAid = (effectiveROI * principal);
            double principalPaid = (emi - interestPAid);
            principal = (principal - principalPaid);
            double loanPAidPer = 100 - (principal / totalPrincipal);

            DetailsEntity detailsEntity = new DetailsEntity(currYear, monthName[currMonth], (principalPaid), (interestPAid), emi, principal, loanPAidPer);
            detailsEntities.add(detailsEntity);

            totalPrincipalPAid = totalPrincipalPAid + principalPaid;
            totalInterestPAid = totalInterestPAid + interestPAid;
            totalPayment = totalPayment + emi;

            currMonth = currMonth + 1;


            if (currMonth == 12 || i == (int) timeInMonth) {
                YearsDetailsEntity yearsDetailsEntity = new YearsDetailsEntity(currYear, "", totalPrincipalPAid,
                        totalInterestPAid, totalPayment, principal, loanPAidPer);
                yearsDetailsEntity.setDetailsEntityList(detailsEntities);
                yearsDetailsEntities.add(yearsDetailsEntity);

                currYear = currYear + 1;
                currMonth = 0;
                //detailsEntities.clear();
                detailsEntities = new ArrayList<>();
                totalPrincipalPAid = 0;
                totalInterestPAid = 0;
                totalPayment = 0;

            }


        }


        return yearsDetailsEntities;
    }


    public void calculateEmi(String Principal, String Interest, String tenure) {

        double principal = Double.parseDouble(Principal);
        double interestPercent = Double.parseDouble(Interest);
        double timeInMonth = Double.parseDouble(tenure);

        if (rbYear.isChecked()) {
            timeInMonth = timeInMonth * 12;
        }

        double effectiveROI = interestPercent / 1200;

        double top = Math.pow((1 + effectiveROI), timeInMonth);

        double emi = (principal * effectiveROI * (top / (top - 1)));
        double totalAmountPayable = (emi * timeInMonth);
        double totalInterestPayable = ((emi * timeInMonth) - principal);
        double InterestPayablePercentage = ((totalInterestPayable / totalAmountPayable) * 100);
        double principalPercent = 100 - InterestPayablePercentage;

        animateProgressBar(progressInterest, (int) InterestPayablePercentage);
        animateProgressBar(progressPrincipal, (int) principalPercent);

        tvEmi.setText("" + new DecimalFormat("#").format(emi) + "\u20B9");
        tvTotalPayable.setText("" + new DecimalFormat("#").format(totalAmountPayable) + "\u20B9");

        tvProgressInterestPercent.setText("" + new DecimalFormat("#").format(InterestPayablePercentage) + "%");
        tvTotalPrincipalPercent.setText("" + new DecimalFormat("#").format(principalPercent) + "%");

        tvTotalPrincipalValue.setText("" + new DecimalFormat("#").format(principal) + "\u20B9");
        tvProgressInterest.setText("" + new DecimalFormat("#").format(totalInterestPayable) + "\u20B9");
        /*Log.d("EMI", "EMI - " + Math.round(emi));
        Log.d("EMI", "Total Payment - " + Math.round(emi * timeInMonth));
        Log.d("EMI", "Total Interest - " + Math.round((emi * timeInMonth) - principal));*/

    }


    class AsyncCalculateEMiDetails extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected String doInBackground(Void... voids) {

            //detailsEntityList = getListDetailsMonthly(etPrincipal.getText().toString(), etInterest.getText().toString(), etTenure.getText().toString());
            yearsDetailsEntities = getListDetailsYearly(etPrincipal.getText().toString(), etInterest.getText().toString(), etTenure.getText().toString());

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            cancelDialog();
            Log.d("EMI", "" + yearsDetailsEntities.size());
            // emiAdapter = new EmiAdapter(EmiCalculatorActivity.this, detailsEntityList);
            // rvEMiDEtails.setAdapter(emiAdapter);

            yearEmiAdapter = new YearEmiAdapter(EmiCalculatorActivity.this, yearsDetailsEntities);
            rvEMiDEtails.setAdapter(yearEmiAdapter);
            scrollToRow(scrollView, llEmiCAl, cvDetails);
        }
    }

    private class InsertEMiHistory extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            roomDatabase.emiSearchHistoryDao().insertAll(emiSearchHistoryEntity);
            return null;
        }
    }

    private class UpdateEMiHistory extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            roomDatabase.emiSearchHistoryDao().update(emiSearchHistoryEntity);
            return null;
        }
    }
}
