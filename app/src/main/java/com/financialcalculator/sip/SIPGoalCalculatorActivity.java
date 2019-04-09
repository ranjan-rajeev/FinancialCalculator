package com.financialcalculator.sip;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.banking.fd.FDYearAdapter;
import com.financialcalculator.banking.rd.RDCalculatorActivity;
import com.financialcalculator.model.FDDetailsEntity;
import com.financialcalculator.model.FDEntity;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.roomdb.tables.GenericSearchHistoryEntity;
import com.financialcalculator.searchhistory.SerachHistoryACtivity;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

public class SIPGoalCalculatorActivity extends BaseActivity implements View.OnClickListener {

    EditText etPrincipal, etInterest, etYear, etMonth, etDay;
    TextView tvCalculate, tvMonthly, tvPrincipal, tvTotalInterest, tvMaturity;

    NestedScrollView scrollView;
    CardView cvInput, cvDetails, cvResult;
    LinearLayout llEmiCAl;
    RecyclerView rvEMiDEtails;

    String[] monthName = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL",
            "AUG", "SEP", "OCT", "NOV",
            "DEC"};
    double amount = 0, principalAmount = 0, monthlyInvest = 0, rate = 0, year = 0, month = 0, day = 0, totalINterest = 0, maturityAmt = 0;


    List<FDDetailsEntity> fdDetailsEntityList;
    FDYearAdapter yearEmiAdapter;

    Spinner spFdTYpe;
    ArrayAdapter<String> fdType;

    RoomDatabase roomDatabase;
    GenericSearchHistoryEntity genericSearchHistoryEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sipgoal_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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


    private void setAdapter() {
        fdType = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sip_frequency));
        spFdTYpe.setAdapter(fdType);
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
        tvMonthly = findViewById(R.id.tvMonthly);
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
            amount = Double.parseDouble(etPrincipal.getText().toString());
        else
            amount = 0;

        if (!etInterest.getText().toString().equals(""))
            rate = Double.parseDouble(etInterest.getText().toString());
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

        //double r = rate / 100;
        // double n = 4;
        //double t = year + (month / 12) + (day / 365);
        // double temp = (Math.pow((1 + (r / n)), (n * t)));
        // maturityAmt = (amount * temp);
        //totalINterest = maturityAmt - amount;


        double r = rate / 1200;
        double x = 1 + r;
        double n = 12;
        double t = (year * 12) + month;
        double temp = ((Math.pow(x, t)) - 1) / r;
        // maturityAmt = (amount * (Math.pow((1 + (r / n)), (n * t))));
        monthlyInvest = amount / temp;


       /* double i = rate / 1200;
        double j = i + 1;
        double n = year * 12;
        double temp = Math.pow(j, (n - 1));
        temp = temp / i;
        temp = temp * j;
        monthlyInvest = (amount ) / (temp);*/


        switch (spFdTYpe.getSelectedItemPosition()) {
            case 0:
                getListDetailsYearly(monthlyInvest, rate, year, month, day);
                break;
            case 1:
                getListDetailsYearly(monthlyInvest, rate, year, month, day);
                break;
            case 2:
                getListDetailsYearly(monthlyInvest, rate, year, month, day);
                break;
            case 3:
                getListDetailsYearly(monthlyInvest, rate, year, month, day);
                break;
        }
    }


    public List<FDDetailsEntity> getListDetailsYearly(double amount, double rate, double year, double month, double day) {

        totalINterest = 0;
        principalAmount = 0;
        fdDetailsEntityList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currYear = calendar.get(Calendar.YEAR);
        int currMonth = calendar.get(Calendar.MONTH);

        double r = rate / 100;
        //double n = 4;
        //double t = year / 12 + (month / 12) + (day / 365);
        //maturityAmt = (amount * (Math.pow((1 + (r / n)), (n * t))));
        //totalINterest = maturityAmt - amount;


        List<FDEntity> fdEntities = new ArrayList<>();

        double balance = amount;
        double prevBalance = amount;
        double balanceToShow = amount;
        double interest = 0, yearlyInterest = 0, interestToShow = 0;

        double timeInMonth = year * 12;
        double frequency = 1;

        for (int i = 1; i <= timeInMonth; i++) {

            //balance = (prevBalance * (Math.pow((1 + (r / n)), (n * (1.0 / 12)))));

            double rat = rate / 1200;
            double x = 1 + rat;
            //double t = (year * 12) + month;
            double temp = ((Math.pow(x, i)) - 1) / rat;
            // maturityAmt = (amount * (Math.pow((1 + (r / n)), (n * t))));
            //monthlyInvest = amount / temp;
            balance = amount * temp;
            //balance = prevBalance + ((prevBalance * r * 1) / 12);
            interest = balance - prevBalance;
            prevBalance = balance;

            yearlyInterest = yearlyInterest + interest;
            interestToShow = interestToShow + interest;
            totalINterest = totalINterest + interest;
            if ((i % frequency) == 0 || i == timeInMonth) {

                balanceToShow = prevBalance;
                FDEntity fdEntity = new FDEntity(monthName[currMonth], "" + currYear, getFormattedDouble(balanceToShow), getFormattedDouble(interest));
                fdEntity.setInterestTotal(getFormattedDouble(interest));
                fdEntities.add(fdEntity);
                principalAmount = principalAmount + amount;
                prevBalance = prevBalance + amount;
                interestToShow = 0;

            } else {
                balanceToShow = prevBalance;
                FDEntity fdEntity = new FDEntity(monthName[currMonth], "" + currYear, getFormattedDouble(balanceToShow), getFormattedDouble(interest));
                fdEntity.setInterestTotal(getFormattedDouble(interest));
                fdEntities.add(fdEntity);
            }

            currMonth = currMonth + 1;


            if (currMonth == 12 || i == (int) timeInMonth) {
                FDDetailsEntity fdDetailsEntity = new FDDetailsEntity();
                fdDetailsEntity.setFdEntityLIst(fdEntities);
                fdDetailsEntity.setInterest(getFormattedDouble(yearlyInterest));
                fdDetailsEntity.setYear("" + currYear);
                fdDetailsEntity.setMonth("");
                fdDetailsEntity.setBalance(getFormattedDouble(balance));
                fdDetailsEntityList.add(fdDetailsEntity);

                currYear = currYear + 1;
                currMonth = 0;
                fdEntities = new ArrayList<>();
                yearlyInterest = 0;
            }


        }

        maturityAmt = balance;
        return fdDetailsEntityList;
    }

    private double getFrequency() {
        switch (spFdTYpe.getSelectedItemPosition()) {
            case 0:
                return 1;
            case 1:
                return 3;
            case 2:
                return 6;
            case 3:
                return 12;
            default:
                return 0;
        }
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

    private void bindDeposits() {
        tvPrincipal.setText("" + getFormattedDoubleUpToDecimal(principalAmount) + "\u20B9");
        tvTotalInterest.setText("" + getFormattedDoubleUpToDecimal(maturityAmt - principalAmount) + "\u20B9");
        tvMaturity.setText("" + getFormattedDoubleUpToDecimal(maturityAmt) + "\u20B9");
        tvMonthly.setText("" + getFormattedDoubleUpToDecimal(monthlyInvest) + "\u20B9");
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
        if (!etMonth.getText().toString().equals("")) {
            int month = Integer.parseInt(etMonth.getText().toString());
            if (month > 12) {
                etMonth.requestFocus();
                etMonth.setError("Month b/w 1 to 12");
                return false;
            }
        }
        if (!etDay.getText().toString().equals("")) {
            int month = Integer.parseInt(etDay.getText().toString());
            if (month > 31) {
                etDay.requestFocus();
                etDay.setError("Day b/w 1 to 31");
                return false;
            }
        }

        if (etYear.getText().toString().equals("") &&
                etMonth.getText().toString().equals("") &&
                etDay.getText().toString().equals("")) {
            etYear.requestFocus();
            etYear.setError("Enter Year");
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
            yearEmiAdapter = new FDYearAdapter(SIPGoalCalculatorActivity.this, fdDetailsEntityList);
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
            genericSearchHistoryEntity.setType(Constants.ADVANCE_SIP_CALCULATOR);
            genericSearchHistoryEntity.setListKeyValues(getListKeyVAlues());
            new InsertHistory().execute(genericSearchHistoryEntity);

        } else {
            genericSearchHistoryEntity.setUpdatedTime(Calendar.getInstance().getTimeInMillis());
            genericSearchHistoryEntity.setType(Constants.ADVANCE_SIP_CALCULATOR);
            genericSearchHistoryEntity.setListKeyValues(getListKeyVAlues());

            new UpdateHistory().execute(genericSearchHistoryEntity);
        }
    }

    private String getListKeyVAlues() {
        String keyValues = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", "" + etPrincipal.getText().toString());
            jsonObject.put("rate", "" + etInterest.getText().toString());
            jsonObject.put("tenure", "" + etYear.getText().toString());
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
            etYear.setText(obj.getString("tenure"));
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
                        .putExtra("TYPE", Constants.ADVANCE_SIP_CALCULATOR), SerachHistoryACtivity.REQUEST_CODE);

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
