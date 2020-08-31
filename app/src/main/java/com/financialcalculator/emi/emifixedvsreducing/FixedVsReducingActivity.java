package com.financialcalculator.emi.emifixedvsreducing;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;

import com.financialcalculator.utility.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.widget.NestedScrollView;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.financialcalculator.BuildConfig;
import com.financialcalculator.R;
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

import java.text.DecimalFormat;
import java.util.Calendar;

public class FixedVsReducingActivity extends BaseActivity implements View.OnClickListener {

    public static final int ANIMATION_TIME = 3000;
    NestedScrollView scrollView;
    TextView tvDetails;
    EditText etTenure, etInterest, etPrincipal;
    EditText etPrincipalLoan2, etInterestLoan2, etTenureLoan2;

    TextView tvEmi, tvTotalPayable, tvProgressInterestPercent, tvProgressInterest;
    TextView tvEmiLoan2, tvTotalPayableLoan2, tvProgressInterestPercentLoan2, tvProgressInterestLoan2;

    ProgressBar progressInterest, progressInterestFull, progressInterestLoan2, progressInterestFullLoan2;
    LinearLayout llEmiCAl;
    CardView cvInput, cvResult;

    RoomDatabase roomDatabase;
    GenericSearchHistoryEntity genericSearchHistoryEntity;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_vs_reducing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUPAdd();
        roomDatabase = RoomDatabase.getAppDatabase(this);

        //region floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //endregion

        init();
        init_views();
        setListener();
        animateProgressBar(progressInterestFull);
        animateProgressBar(progressInterestFullLoan2);

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

    private void init() {

        cvInput = findViewById(R.id.cvInput);
        cvResult = findViewById(R.id.cvResult);
        llEmiCAl = findViewById(R.id.llEmiCAl);
        scrollView = findViewById(R.id.scrollView);

        etPrincipal = findViewById(R.id.etPrincipal);
        etInterest = findViewById(R.id.etInterest);
        etTenure = findViewById(R.id.etTenure);

        etPrincipalLoan2 = findViewById(R.id.etPrincipalLoan2);
        etInterestLoan2 = findViewById(R.id.etInterestLoan2);
        etTenureLoan2 = findViewById(R.id.etTenureLoan2);

        tvEmi = findViewById(R.id.tvEmi);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);
        tvProgressInterestPercent = findViewById(R.id.tvProgressInterestPercent);
        tvProgressInterest = findViewById(R.id.tvProgressInterest);

        tvEmiLoan2 = findViewById(R.id.tvEmiLoan2);
        tvTotalPayableLoan2 = findViewById(R.id.tvTotalPayableLoan2);
        tvProgressInterestPercentLoan2 = findViewById(R.id.tvProgressInterestPercentLoan2);
        tvProgressInterestLoan2 = findViewById(R.id.tvProgressInterestLoan2);


        progressInterest = findViewById(R.id.progressInterest);
        progressInterestFull = findViewById(R.id.progressInterestFull);
        progressInterestLoan2 = findViewById(R.id.progressInterestLoan2);
        progressInterestFullLoan2 = findViewById(R.id.progressInterestFullLoan2);

        tvDetails = findViewById(R.id.tvDetails);
    }

    private void init_views() {
    }

    private void setListener() {
        tvDetails.setOnClickListener(this);
        applyCommaTextChange(etTenure, etPrincipal, etPrincipalLoan2, etTenureLoan2);
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

    @Override
    public void onClick(View view) {
        hideKeyBoard(view, this);
        switch (view.getId()) {
            case R.id.tvDetails:
                if (isValidInput()) {
                    scrollToRow(scrollView, llEmiCAl, cvResult);
                    calculateEmi(getCommaRemovedText(etPrincipal), getCommaRemovedText(etInterest), getCommaRemovedText(etTenure));
                    calculateEmiLoan2(getCommaRemovedText(etPrincipalLoan2), getCommaRemovedText(etInterestLoan2), getCommaRemovedText(etTenureLoan2));
                    updateGenericHistory();
                }
                break;
        }
    }

    public void calculateEmi(String Principal, String Interest, String tenure) {

        double principal = Double.parseDouble(Principal);
        double interestPercent = Double.parseDouble(Interest);
        double timeInMonth = Double.parseDouble(tenure);


        double totalInterestPayable = (principal * interestPercent * timeInMonth) / 100;
        double totalAmountPayable = (principal + totalInterestPayable);
        double emi = (totalAmountPayable / (timeInMonth * 12));


       /* double effectiveROI = interestPercent / 1200;

        double top = Math.pow((1 + effectiveROI), timeInMonth);

        //double emi = (principal * effectiveROI * (top / (top - 1)));

        double emi = (principal * effectiveROI);

        double totalAmountPayable = (emi * timeInMonth);
        double totalInterestPayable = ((emi * timeInMonth) - principal);*/
        double InterestPayablePercentage = ((totalInterestPayable / totalAmountPayable) * 100);
        double principalPercent = 100 - InterestPayablePercentage;

        animateProgressBar(progressInterest, (int) InterestPayablePercentage);

        tvEmi.setText("" + Util.getCommaSeparated("" + emi) + "\u20B9");
        tvTotalPayable.setText("" + Util.getCommaSeparated("" + totalAmountPayable) + "\u20B9");
        tvProgressInterestPercent.setText("" + Util.getCommaSeparated("" + InterestPayablePercentage) + "%");
        tvProgressInterest.setText("" + Util.getCommaSeparated("" + totalInterestPayable) + "\u20B9");

    }

    public void calculateEmiLoan2(String Principal, String Interest, String tenure) {

        double principal = Double.parseDouble(Principal);
        double interestPercent = Double.parseDouble(Interest);
        double timeInMonth = Double.parseDouble(tenure);

        timeInMonth = timeInMonth * 12;

        double effectiveROI = interestPercent / 1200;

        double top = Math.pow((1 + effectiveROI), timeInMonth);

        double emi = (principal * effectiveROI * (top / (top - 1)));
        double totalAmountPayable = (emi * timeInMonth);
        double totalInterestPayable = ((emi * timeInMonth) - principal);
        double InterestPayablePercentage = ((totalInterestPayable / totalAmountPayable) * 100);
        double principalPercent = 100 - InterestPayablePercentage;

        animateProgressBar(progressInterestLoan2, (int) InterestPayablePercentage);

        tvEmiLoan2.setText("" + Util.getCommaSeparated("" + emi) + "\u20B9");
        tvTotalPayableLoan2.setText("" + Util.getCommaSeparated("" + totalAmountPayable) + "\u20B9");
        tvProgressInterestPercentLoan2.setText("" + Util.getCommaSeparated("" + InterestPayablePercentage) + "%");
        tvProgressInterestLoan2.setText("" + Util.getCommaSeparated("" + totalInterestPayable) + "\u20B9");

    }

    public boolean isValidInput() {
        if (etPrincipal.getText().toString().equals("")) {
            etPrincipal.requestFocus();
            etPrincipal.setError("Enter Principal Loan1");
            return false;
        }
        if (etInterest.getText().toString().equals("")) {
            etInterest.requestFocus();
            etInterest.setError("Enter Interest Loan1");
            return false;
        }
        if (etTenure.getText().toString().equals("")) {
            etTenure.requestFocus();
            etTenure.setError("Enter Tenure Loan1");
            return false;
        }

        if (etPrincipalLoan2.getText().toString().equals("")) {
            etPrincipalLoan2.requestFocus();
            etPrincipalLoan2.setError("Enter Principal Loan2");
            return false;
        }
        if (etInterestLoan2.getText().toString().equals("")) {
            etInterestLoan2.requestFocus();
            etInterestLoan2.setError("Enter Interest Loan2");
            return false;
        }
        if (etTenureLoan2.getText().toString().equals("")) {
            etTenureLoan2.requestFocus();
            etTenureLoan2.setError("Enter Tenure Loan2");
            return false;
        }
        return true;
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
            genericSearchHistoryEntity.setType(Constants.FLAT_VS_REDUCING);
            genericSearchHistoryEntity.setListKeyValues(getListKeyVAlues());
            new InsertHistory().execute(genericSearchHistoryEntity);

        } else {
            genericSearchHistoryEntity.setUpdatedTime(Calendar.getInstance().getTimeInMillis());
            genericSearchHistoryEntity.setType(Constants.FLAT_VS_REDUCING);
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
            jsonObject.put("tenure", "" + getCommaRemovedText(etTenure));

            jsonObject.put("amount2", "" + getCommaRemovedText(etPrincipalLoan2));
            jsonObject.put("rate2", "" + getCommaRemovedText(etInterestLoan2));
            jsonObject.put("tenure2", "" + getCommaRemovedText(etTenureLoan2));


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
            etTenure.setText(obj.getString("tenure"));

            etPrincipalLoan2.setText(obj.getString("amount"));
            etInterestLoan2.setText(obj.getString("rate"));
            etTenureLoan2.setText(obj.getString("tenure"));


            tvDetails.performClick();
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
                        .putExtra("TYPE", Constants.FLAT_VS_REDUCING), SerachHistoryACtivity.REQUEST_CODE);

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
