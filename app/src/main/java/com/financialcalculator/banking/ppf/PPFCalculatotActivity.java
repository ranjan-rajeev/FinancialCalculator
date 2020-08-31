package com.financialcalculator.banking.ppf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.financialcalculator.BuildConfig;
import com.financialcalculator.R;
import com.financialcalculator.banking.fd.FDYearAdapter;
import com.financialcalculator.model.FDDetailsEntity;
import com.financialcalculator.model.FDEntity;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.roomdb.tables.GenericSearchHistoryEntity;
import com.financialcalculator.searchhistory.SerachHistoryACtivity;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Logger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

public class PPFCalculatotActivity extends BaseActivity implements View.OnClickListener {


    EditText etPrincipal, etInterest, etYear, etMonth, etDay;
    TextView tvCalculate, tvPrincipal, tvTotalInterest, tvMaturity;

    NestedScrollView scrollView;
    CardView cvInput, cvDetails, cvResult;
    LinearLayout llEmiCAl;
    RecyclerView rvEMiDEtails;

    String[] monthName = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL",
            "AUG", "SEP", "OCT", "NOV",
            "DEC"};
    double amount = 0, principalAmount = 0, rate = 0, year = 0, month = 0, day = 0, totalINterest = 0, maturityAmt = 0;


    List<FDDetailsEntity> fdDetailsEntityList;
    FDYearAdapter yearEmiAdapter;

    Spinner spFdTYpe, spPPFDuration;
    ArrayAdapter<String> fdType, PPFDuration;


    RoomDatabase roomDatabase;
    GenericSearchHistoryEntity genericSearchHistoryEntity;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppfcalculatot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUPAdd();
        roomDatabase = RoomDatabase.getAppDatabase(this);
        //region floating button
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        //endregion
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init_widgets();
        init_views();
        setAdapter();
        setListeners();
    }
    private void setUPAdd() {

        mAdView = findViewById(R.id.adView);

        if (BuildConfig.DEBUG) {

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("5C24676FE04113F56F0B0A9566555BCD")
                    .build();
            mAdView.loadAd(adRequest);

        } else {

            if (BuildConfig.FLAVOR.equals("free") && Constants.APP_TYPE == 0) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            } else {
                mAdView.setVisibility(View.GONE);
            }

        }
    }
    private void setAdapter() {
        fdType = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.ppf_frequency));
        spFdTYpe.setAdapter(fdType);

        PPFDuration = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.ppf_duration));
        spPPFDuration.setAdapter(PPFDuration);
    }

    private void init_views() {
        cvInput.setVisibility(View.VISIBLE);
        cvResult.setVisibility(GONE);
        cvDetails.setVisibility(GONE);


    }

    private void showLayouts() {
        cvResult.setVisibility(View.VISIBLE);
        cvDetails.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        tvCalculate.setOnClickListener(this);
        applyCommaTextChange(etPrincipal);
    }

    private void init_widgets() {

        etPrincipal = findViewById(R.id.etPrincipal);
        etInterest = findViewById(R.id.etInterest);
        etYear = findViewById(R.id.etYear);
        etMonth = findViewById(R.id.etMonth);
        etDay = findViewById(R.id.etDay);

        tvCalculate = findViewById(R.id.tvCalculate);
        tvPrincipal = findViewById(R.id.tvPrincipal);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvMaturity = findViewById(R.id.tvMaturity);

        cvDetails = findViewById(R.id.cvDetails);
        cvInput = findViewById(R.id.cvInput);
        cvResult = findViewById(R.id.cvResult);
        llEmiCAl = findViewById(R.id.llEmiCAl);
        scrollView = findViewById(R.id.scrollView);
        rvEMiDEtails = findViewById(R.id.rvEMiDEtails);
        rvEMiDEtails.setLayoutManager(new LinearLayoutManager(this));
        spFdTYpe = findViewById(R.id.spFdTYpe);
        spPPFDuration = findViewById(R.id.spPPFDuration);
    }

    @Override
    public void onClick(View view) {
        hideKeyBoard(view, this);
        switch (view.getId()) {
            case R.id.tvCalculate:
                if (isValidInput()) {
                    showLayouts();
                    new AsyncCalculateEMiDetails().execute();
                    bindDeposits();
                    updateGenericHistory();
                }
                break;
        }
    }

    private void calculateFixedDeposit() {
        if (!etPrincipal.getText().toString().equals(""))
            amount = Double.parseDouble(getCommaRemovedText(etPrincipal));
        else
            amount = 0;

        if (!etInterest.getText().toString().equals(""))
            rate = Double.parseDouble(getCommaRemovedText(etInterest));
        else
            rate = 0;

        if (!etYear.getText().toString().equals(""))
            year = Double.parseDouble(etYear.getText().toString());
        else
            year = 0;

        if (!etMonth.getText().toString().equals(""))
            month = Double.parseDouble(etMonth.getText().toString());
        else
            month = 0;

        if (!etDay.getText().toString().equals(""))
            day = Double.parseDouble(etDay.getText().toString());
        else
            day = 0;

        double r = rate / 100;
        double n = 4;
        double t = year + (month / 12) + (day / 365);
        maturityAmt = (amount * (Math.pow((1 + (r / n)), (n * t))));
        totalINterest = maturityAmt - amount;

        switch (spFdTYpe.getSelectedItemPosition()) {
            case 0:
                getListDetailsYearly(amount, rate, year, month, day);
                break;
            case 1:
                getListDetailsYearlyMonthly(amount, rate, year, month, day);
                break;
            case 2:
                getListDetailsYearlyQuartely(amount, rate, year, month, day);
                break;

        }
    }


    public List<FDDetailsEntity> getListDetailsYearly(double amount, double rate, double year, double month, double day) {

        totalINterest = 0;
        fdDetailsEntityList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        int currMonth = calendar.get(Calendar.MONTH);

        double r = rate / 100;
        double n = 4;
        double t = year / 12 + (month / 12) + (day / 365);
        maturityAmt = (amount * (Math.pow((1 + (r / n)), (n * t))));
        //totalINterest = maturityAmt - amount;


        List<FDEntity> fdEntities = new ArrayList<>();

        double balance = amount;
        double prevBalance = amount;
        double balanceToShow = amount;
        double interest = 0, yearlyInterest = 0, interestToShow = 0;

        double timeInMonth = getTimeINYear();


        for (int i = 1; i <= timeInMonth; i++) {

            //balance = (prevBalance * (Math.pow((1 + (r / n)), (n * (1.0 / 12)))));
            balance = prevBalance + ((prevBalance * r * 1) / 12);
            interest = balance - prevBalance;


            yearlyInterest = yearlyInterest + interest;
            interestToShow = interestToShow + interest;
            totalINterest = totalINterest + interest;
            if (i % 12 == 0 || i == timeInMonth) {
                balanceToShow = prevBalance + amount + interestToShow;
                principalAmount = balanceToShow;
                FDEntity fdEntity = new FDEntity(monthName[currMonth], "" + currYear, getFormattedDouble(balanceToShow), getFormattedDouble(interest));
                fdEntity.setInterestTotal(getFormattedDouble(interestToShow));
                fdEntities.add(fdEntity);

                prevBalance = prevBalance + amount + interestToShow;
                interestToShow = 0;

            } else {
                FDEntity fdEntity = new FDEntity(monthName[currMonth], "" + currYear, getFormattedDouble(balanceToShow), getFormattedDouble(interest));
                fdEntities.add(fdEntity);
            }

            currMonth = currMonth + 1;


            if (currMonth == 12 || i == (int) timeInMonth) {
                FDDetailsEntity fdDetailsEntity = new FDDetailsEntity();
                fdDetailsEntity.setFdEntityLIst(fdEntities);
                fdDetailsEntity.setInterest(getFormattedDouble(yearlyInterest));
                fdDetailsEntity.setYear("" + currYear);
                fdDetailsEntity.setMonth("");
                fdDetailsEntity.setBalance(getFormattedDouble(principalAmount));
                fdDetailsEntityList.add(fdDetailsEntity);

                currYear = currYear + 1;
                currMonth = 0;
                fdEntities = new ArrayList<>();
                yearlyInterest = 0;
            }


        }

        maturityAmt = balanceToShow;
        principalAmount = (amount * timeInMonth) / 12;
        return fdDetailsEntityList;
    }

    public List<FDDetailsEntity> getListDetailsYearlyMonthly(double amount, double rate, double year, double month, double day) {

        totalINterest = 0;
        fdDetailsEntityList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        int currMonth = calendar.get(Calendar.MONTH);

        double r = rate / 100;
        double n = 4;
        double t = year / 12 + (month / 12) + (day / 365);
        maturityAmt = (amount * (Math.pow((1 + (r / n)), (n * t))));
        //totalINterest = maturityAmt - amount;


        List<FDEntity> fdEntities = new ArrayList<>();

        double balance = amount;
        double prevBalance = amount;
        double balanceToShow = amount;
        double interest = 0, yearlyInterest = 0, interestToShow = 0;

        double timeInMonth = getTimeINYear();


        for (int i = 1; i <= timeInMonth; i++) {

            //balance = (prevBalance * (Math.pow((1 + (r / n)), (n * (1.0 / 12)))));
            balance = prevBalance + ((prevBalance * r * 1) / 12);
            interest = balance - prevBalance;
            prevBalance = prevBalance + amount;

            yearlyInterest = yearlyInterest + interest;
            interestToShow = interestToShow + interest;
            totalINterest = totalINterest + interest;
            if (i % 12 == 0 || i == timeInMonth) {
                prevBalance = prevBalance + interestToShow;
                balanceToShow = prevBalance;
                principalAmount = balanceToShow;
                FDEntity fdEntity = new FDEntity(monthName[currMonth], "" + currYear, getFormattedDouble(balanceToShow), getFormattedDouble(interest));
                fdEntity.setInterestTotal(getFormattedDouble(interestToShow));
                fdEntities.add(fdEntity);
                interestToShow = 0;

            } else {
                FDEntity fdEntity = new FDEntity(monthName[currMonth], "" + currYear, getFormattedDouble(prevBalance), getFormattedDouble(interest));
                fdEntities.add(fdEntity);
            }

            currMonth = currMonth + 1;


            if (currMonth == 12 || i == (int) timeInMonth) {
                FDDetailsEntity fdDetailsEntity = new FDDetailsEntity();
                fdDetailsEntity.setFdEntityLIst(fdEntities);
                fdDetailsEntity.setInterest(getFormattedDouble(yearlyInterest));
                fdDetailsEntity.setYear("" + currYear);
                fdDetailsEntity.setMonth("");
                fdDetailsEntity.setBalance(getFormattedDouble(principalAmount));
                fdDetailsEntityList.add(fdDetailsEntity);

                currYear = currYear + 1;
                currMonth = 0;
                fdEntities = new ArrayList<>();
                yearlyInterest = 0;
            }


        }

        maturityAmt = balanceToShow;
        principalAmount = (amount * timeInMonth);
        return fdDetailsEntityList;
    }

    public List<FDDetailsEntity> getListDetailsYearlyQuartely(double amount, double rate, double year, double month, double day) {

        totalINterest = 0;
        fdDetailsEntityList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        int currMonth = calendar.get(Calendar.MONTH);

        double r = rate / 100;
        double n = 4;
        double t = year + (month / 12) + (day / 365);
        maturityAmt = (amount * (Math.pow((1 + (r / n)), (n * t))));
        //totalINterest = maturityAmt - amount;


        List<FDEntity> fdEntities = new ArrayList<>();

        double balance = amount;
        double prevBalance = amount;
        double balanceToShow = amount;
        double interest = 0, yearlyInterest = 0, interestToShow = 0;

        double timeInMonth = getTimeINMOnth();


        for (int i = 1; i <= timeInMonth; i++) {
            if (i == timeInMonth && day != 0 && (day % 30 != 0)) {

                double ti = ((day % 30) / 365);
                balance = (prevBalance * (Math.pow((1 + (r / n)), (n * ti))));
            } else {
                balance = (prevBalance * (Math.pow((1 + (r / n)), (n * (1.0 / 12)))));
            }


            interest = balance - prevBalance;
            prevBalance = balance;

            yearlyInterest = yearlyInterest + interest;
            interestToShow = interestToShow + interest;
            totalINterest = totalINterest + interest;

            if (i % 3 == 0 || i == timeInMonth) {
                balanceToShow = balance;

                FDEntity fdEntity = new FDEntity(monthName[currMonth], "" + currYear, getFormattedDouble(amount), getFormattedDouble(interest));
                fdEntity.setInterestTotal(getFormattedDouble(interestToShow));
                fdEntities.add(fdEntity);
                interestToShow = 0;
                prevBalance = amount;

            } else {
                FDEntity fdEntity = new FDEntity(monthName[currMonth], "" + currYear, getFormattedDouble(amount), getFormattedDouble(interest));
                fdEntities.add(fdEntity);
            }

            currMonth = currMonth + 1;


            if (currMonth == 12 || i == (int) timeInMonth) {
                FDDetailsEntity fdDetailsEntity = new FDDetailsEntity();
                fdDetailsEntity.setFdEntityLIst(fdEntities);
                fdDetailsEntity.setInterest(getFormattedDouble(yearlyInterest));
                fdDetailsEntity.setYear("" + currYear);
                fdDetailsEntity.setMonth("");
                fdDetailsEntity.setBalance(getFormattedDouble(amount));
                fdDetailsEntityList.add(fdDetailsEntity);

                currYear = currYear + 1;
                currMonth = 0;
                fdEntities = new ArrayList<>();
                yearlyInterest = 0;
            }


        }

        maturityAmt = amount;
        return fdDetailsEntityList;
    }

    private double getTimeINMOnth() {

        double timeinMonth = 0;

        if (!etYear.getText().toString().equals(""))
            year = Double.parseDouble(etYear.getText().toString());
        else
            year = 0;
        if (!etMonth.getText().toString().equals(""))
            month = Double.parseDouble(etMonth.getText().toString());
        else
            month = 0;

        if (!etDay.getText().toString().equals(""))
            day = Double.parseDouble(etDay.getText().toString());
        else
            day = 0;


        if (day == 0 || (day % 30 == 0)) {
            timeinMonth = month + (year * 12) + (day / 30);
        } else {
            timeinMonth = month + (year * 12) + Math.ceil(day / 30);
        }
        return timeinMonth;
    }

    private double getTimeINYear() {

        double timeinMonth = 0;
        switch (spPPFDuration.getSelectedItemPosition()) {

            case 0:
                return (12 * 15);
            case 1:
                return (12 * 20);
            case 2:
                return (12 * 25);
            case 3:
                return (12 * 30);
        }

        return timeinMonth;
    }

    private void bindDeposits() {
        tvPrincipal.setText("" + getFormattedDoubleUpToDecimal(principalAmount) + "\u20B9");
        tvTotalInterest.setText("" + getFormattedDoubleUpToDecimal(maturityAmt - principalAmount) + "\u20B9");
        tvMaturity.setText("" + getFormattedDoubleUpToDecimal(maturityAmt) + "\u20B9");
    }

    private boolean isValidInput() {

        if (etPrincipal.getText().toString().equals("")) {
            etPrincipal.requestFocus();
            etPrincipal.setError("Enter Amount");
            return false;
        }
        if (etInterest.getText().toString().equals("")) {
            etInterest.requestFocus();
            etInterest.setError("Enter Rate of Interest");
            return false;
        }

        return true;
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
            //fdDetailsEntityList = getListDetailsYearly(etPrincipal.getText().toString(), etInterest.getText().toString(), etTenure.getText().toString());
            calculateFixedDeposit();
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            cancelDialog();

            // emiAdapter = new EmiAdapter(EmiCalculatorActivity.this, detailsEntityList);
            // rvEMiDEtails.setAdapter(emiAdapter);
            bindDeposits();
            yearEmiAdapter = new FDYearAdapter(PPFCalculatotActivity.this, fdDetailsEntityList);
            rvEMiDEtails.setAdapter(yearEmiAdapter);
            scrollToRow(scrollView, llEmiCAl, cvDetails);
        }
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

    private void updateGenericHistory() {


        if (genericSearchHistoryEntity == null) {
            genericSearchHistoryEntity = new GenericSearchHistoryEntity();
            genericSearchHistoryEntity.setUpdatedTime(Calendar.getInstance().getTimeInMillis());
            genericSearchHistoryEntity.setType(Constants.PPF_CALCULATOR);
            genericSearchHistoryEntity.setListKeyValues(getListKeyVAlues());
            new InsertHistory().execute(genericSearchHistoryEntity);

        } else {
            genericSearchHistoryEntity.setUpdatedTime(Calendar.getInstance().getTimeInMillis());
            genericSearchHistoryEntity.setType(Constants.PPF_CALCULATOR);
            genericSearchHistoryEntity.setListKeyValues(getListKeyVAlues());

            new UpdateHistory().execute(genericSearchHistoryEntity);
        }
    }

    private String getListKeyVAlues() {
        String keyValues = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", "" + getCommaRemovedText(etPrincipal));
            jsonObject.put("rate", "" + getCommaRemovedText(etInterest));
            jsonObject.put("term", "" + spPPFDuration.getSelectedItemPosition());
            jsonObject.put("deposittype", spFdTYpe.getSelectedItemPosition());
            keyValues = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return keyValues;
    }

    private void bindValues(GenericSearchHistoryEntity genericSearchHistoryEntity) {
        try {

            JSONObject obj = new JSONObject(genericSearchHistoryEntity.getListKeyValues());
            Logger.d(obj.toString());
            etPrincipal.setText(obj.getString("amount"));
            etInterest.setText(obj.getString("rate"));
            spFdTYpe.setSelection(obj.getInt("deposittype"));
            spPPFDuration.setSelection(obj.getInt("term"));
            tvCalculate.performClick();
            hideKeyBoard(etInterest, this);
            scrollToRow(scrollView, llEmiCAl, cvInput);
        } catch (Throwable t) {
            Log.e("Financial", "Could not parse malformed JSON: \"" + genericSearchHistoryEntity.getListKeyValues() + "\"");
        }


    }


    private class InsertHistory extends AsyncTask<GenericSearchHistoryEntity, Void, Void> {
        @Override
        protected Void doInBackground(GenericSearchHistoryEntity... voids) {
            roomDatabase.genericSearchHistoryDao().insertAll(voids);
            return null;
        }
    }

    private class UpdateHistory extends AsyncTask<GenericSearchHistoryEntity, Void, Void> {
        @Override
        protected Void doInBackground(GenericSearchHistoryEntity... voids) {
            roomDatabase.genericSearchHistoryDao().update(voids);
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_history:
                startActivityForResult(new Intent(this, SerachHistoryACtivity.class)
                        .putExtra("TYPE", Constants.PPF_CALCULATOR), SerachHistoryACtivity.REQUEST_CODE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SerachHistoryACtivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                genericSearchHistoryEntity = data.getExtras().getParcelable("DATA");
                if (genericSearchHistoryEntity != null)
                    bindValues(genericSearchHistoryEntity);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
