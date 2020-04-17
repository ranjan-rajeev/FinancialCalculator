package com.financialcalculator.emi.emicompare;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.widget.NestedScrollView;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.R;

import java.text.DecimalFormat;

public class EmiCompareActivity extends BaseActivity implements View.OnClickListener {

    public static final int ANIMATION_TIME = 3000;
    NestedScrollView scrollView;
    LinearLayout llEmiCAl;
    CardView cvInput, cvResult;

    TextView tvDetails;
    EditText etTenure, etInterest, etPrincipal;
    EditText etPrincipalLoan2, etInterestLoan2, etTenureLoan2;

    TextView tvEmi, tvTotalPayable, tvProgressInterestPercent, tvProgressInterest;
    TextView tvEmiLoan2, tvTotalPayableLoan2, tvProgressInterestPercentLoan2, tvProgressInterestLoan2;

    ProgressBar progressInterest, progressInterestFull, progressInterestLoan2, progressInterestFullLoan2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_compare);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        init_views();
        setListener();
        animateProgressBar(progressInterestFull);
        animateProgressBar(progressInterestFullLoan2);

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
                    calculateEmi(etPrincipal.getText().toString(), etInterest.getText().toString(), etTenure.getText().toString());
                    calculateEmiLoan2(etPrincipalLoan2.getText().toString(), etInterestLoan2.getText().toString(), etTenureLoan2.getText().toString());

                }
                break;
        }
    }

    public void calculateEmi(String Principal, String Interest, String tenure) {

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

        animateProgressBar(progressInterest, (int) InterestPayablePercentage);

        tvEmi.setText("" + new DecimalFormat("#").format(emi) + "\u20B9");
        tvTotalPayable.setText("" + new DecimalFormat("#").format(totalAmountPayable) + "\u20B9");
        tvProgressInterestPercent.setText("" + new DecimalFormat("#").format(InterestPayablePercentage) + "%");
        tvProgressInterest.setText("" + new DecimalFormat("#").format(totalInterestPayable) + "\u20B9");

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

        tvEmiLoan2.setText("" + new DecimalFormat("#").format(emi) + "\u20B9");
        tvTotalPayableLoan2.setText("" + new DecimalFormat("#").format(totalAmountPayable) + "\u20B9");
        tvProgressInterestPercentLoan2.setText("" + new DecimalFormat("#").format(InterestPayablePercentage) + "%");
        tvProgressInterestLoan2.setText("" + new DecimalFormat("#").format(totalInterestPayable) + "\u20B9");

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
}
