package com.financialcalculator;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.financialcalculator.home.MainActivity;

public class EmiCalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvCalculate, tvDetails;
    NestedScrollView scrollView;
    CardView cvInput, cvDetails, cvResult;
    LinearLayout llEmiCAl;


    ProgressBar progressPrincipal, progressInterest, progressInterestFull, progressPrincipalFull;
    public static final int ANIMATION_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        init();
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
        animateProgressBar(progressInterest, 25);

        animateProgressBar(progressPrincipalFull);
        animateProgressBar(progressPrincipal, 75);


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
    }


    private void init() {
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
        switch (view.getId()) {
            case R.id.tvCalculate:
                scrollToRow(scrollView, llEmiCAl, cvResult);
                break;
            case R.id.tvDetails:
                scrollToRow(scrollView, llEmiCAl, cvDetails);
                break;
        }

    }
}
