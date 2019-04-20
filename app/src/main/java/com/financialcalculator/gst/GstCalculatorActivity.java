package com.financialcalculator.gst;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.financialcalculator.BuildConfig;
import com.financialcalculator.R;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GstCalculatorActivity extends BaseActivity implements View.OnClickListener {

    NestedScrollView scrollView;
    LinearLayout llEmiCAl;
    CardView cvInput, cvResult;
    EditText etPrincipal, etCustomRAte;
    TextInputLayout tilCustomRate;
    RadioGroup rgFirstRAte, rgSecondRate;
    RadioButton rb1, rb2, rb3, rb4, rb5;
    TextView tvOriginalCost, tvAppliedGst, tvNetPrice;
    TextView tvCalculate;
    boolean isfirst = true;
    Spinner spGstTYpe;
    ArrayAdapter<String> gstType;

    double originalCost = 0, gstApplied = 0, netPrice = 0;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gst_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUPAdd();
        //region floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //endregion
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inti_widgets();
        setListeners();
        setAdapter();
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
        gstType = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gst_type));
        spGstTYpe.setAdapter(gstType);
    }

    private void setListeners() {
        tvCalculate.setOnClickListener(this);
        rgFirstRAte.setOnCheckedChangeListener(listener1);
        rgSecondRate.setOnCheckedChangeListener(listener2);
    }

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rgSecondRate.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                rgSecondRate.clearCheck(); // clear the second RadioGroup!
                rgSecondRate.setOnCheckedChangeListener(listener2); //reset the listener
                if (checkedId == R.id.rb5) {
                    tilCustomRate.setVisibility(View.VISIBLE);
                } else {
                    tilCustomRate.setVisibility(View.GONE);
                }
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rgFirstRAte.setOnCheckedChangeListener(null);
                rgFirstRAte.clearCheck();
                rgFirstRAte.setOnCheckedChangeListener(listener1);

                if (checkedId == R.id.rb5) {
                    tilCustomRate.setVisibility(View.VISIBLE);
                } else {
                    tilCustomRate.setVisibility(View.GONE);
                }
            }
        }
    };


    private void inti_widgets() {

        scrollView = findViewById(R.id.scrollView);
        llEmiCAl = findViewById(R.id.llEmiCAl);
        cvInput = findViewById(R.id.cvInput);
        cvResult = findViewById(R.id.cvResult);


        etPrincipal = findViewById(R.id.etPrincipal);
        etCustomRAte = findViewById(R.id.etCustomRAte);
        tilCustomRate = findViewById(R.id.tilCustomRate);
        spGstTYpe = findViewById(R.id.spGstTYpe);
        rgFirstRAte = findViewById(R.id.rgFirstRAte);
        rgSecondRate = findViewById(R.id.rgSecondRate);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        rb5 = findViewById(R.id.rb5);


        tvOriginalCost = findViewById(R.id.tvOriginalCost);
        tvAppliedGst = findViewById(R.id.tvAppliedGst);
        tvNetPrice = findViewById(R.id.tvNetPrice);
        tvCalculate = findViewById(R.id.tvCalculate);


    }

    public double getCheckedPercent() {
        int checkedBUttonId = 0;
        if (rgFirstRAte.getCheckedRadioButtonId() != -1) {
            checkedBUttonId = rgFirstRAte.getCheckedRadioButtonId();
        } else {
            checkedBUttonId = rgSecondRate.getCheckedRadioButtonId();
        }

        double rate = 0;
        switch (checkedBUttonId) {
            case R.id.rb1:
                return 5;
            case R.id.rb2:
                return 12;
            case R.id.rb3:
                return 18;
            case R.id.rb4:
                return 28;
            case R.id.rb5:
                if (!etCustomRAte.getText().toString().equals(""))
                    rate = Double.parseDouble(etCustomRAte.getText().toString());
                return rate;

            default:
                return 0;
        }
    }


    @Override
    public void onClick(View view) {
        hideKeyBoard(view, this);
        switch (view.getId()) {
            case R.id.tvCalculate:
                if (isValid()) {
                    scrollToRow(scrollView, llEmiCAl, cvResult);
                    calculateGst();
                    bindData();
                }
                break;
        }
    }

    private void bindData() {
        tvOriginalCost.setText("" + getFormattedDoubleUpToDecimal(originalCost) + "\u20B9");
        tvAppliedGst.setText("" + getFormattedDoubleUpToDecimal(gstApplied) + "\u20B9");
        tvNetPrice.setText("" + getFormattedDoubleUpToDecimal(netPrice) + "\u20B9");
    }

    private void calculateGst() {
        double principal = 0, rate = 0;
        if (!etPrincipal.getText().toString().equals(""))
            principal = Double.parseDouble(etPrincipal.getText().toString());

        rate = getCheckedPercent();


        if (spGstTYpe.getSelectedItemPosition() == 0) {  // add GST
            originalCost = principal;
            gstApplied = ((rate * originalCost) / 100);
            netPrice = originalCost + gstApplied;

        } else {   //remove GST
            originalCost = principal;
            double temp = 100 + rate;
            netPrice = ((100 * originalCost) / temp);
            gstApplied = originalCost - netPrice;

        }

    }

    private boolean isValid() {
        if (etPrincipal.getText().toString().equals("")) {
            etPrincipal.setError("Enter amount");
            etPrincipal.requestFocus();
            return false;
        }
        if (rb5.isChecked() && etCustomRAte.getText().toString().equals("")) {
            etCustomRAte.setError("Enter Custom rate");
            etCustomRAte.requestFocus();
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
