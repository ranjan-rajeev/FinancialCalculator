package com.financialcalculator.banking;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.financialcalculator.R;
import com.financialcalculator.utility.BaseActivity;

public class FDCalculatorActivity extends BaseActivity implements View.OnClickListener {

    EditText etPrincipal, etInterest, etYear, etMonth, etDay;
    TextView tvCalculate, tvPrincipal, tvTotalInterest, tvMaturity;

    NestedScrollView scrollView;
    CardView cvInput, cvDetails, cvResult;
    LinearLayout llEmiCAl;
    RecyclerView rvEMiDEtails;

    double amount, rate, year, month, day, totalINterest, maturityAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdcalculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        setListeners();
    }

    private void init_views() {
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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCalculate:
                if (isValidInput()) {
                    calculateFixedDeposit();
                    bindDeposits();
                }
                break;
        }
    }

    private void calculateFixedDeposit() {
        amount = Double.parseDouble(etPrincipal.getText().toString());
        rate = Double.parseDouble(etInterest.getText().toString());
        year = Double.parseDouble(etYear.getText().toString());
        month = Double.parseDouble(etMonth.getText().toString());
        day = Double.parseDouble(etDay.getText().toString());
        double r = rate / 100;
        double n = 4;
        double t = year + (month / 12) + (day / n);
        maturityAmt = (amount * (Math.pow((1 + (r / n)), (n * t))));
        totalINterest = maturityAmt - amount;

    }

    private void bindDeposits() {
        tvPrincipal.setText("" + getFormattedDoubleUpToDecimal(amount) + "\u20B9");
        tvTotalInterest.setText("" + getFormattedDoubleUpToDecimal(totalINterest) + "\u20B9");
        tvMaturity.setText("" + getFormattedDoubleUpToDecimal(maturityAmt) + "\u20B9");
    }

    private boolean isValidInput() {
        return true;
    }
}
