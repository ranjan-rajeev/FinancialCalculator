package com.financialcalculator.searchhistory;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.financialcalculator.R;
import com.financialcalculator.banking.fd.FDCalculatorActivity;
import com.financialcalculator.banking.ppf.PPFCalculatotActivity;
import com.financialcalculator.banking.rd.RDCalculatorActivity;
import com.financialcalculator.emi.emicalculator.EmiCalculatorActivity;
import com.financialcalculator.emi.emicalculator.EmiSearchHistoryAdapter;
import com.financialcalculator.emi.emicompare.EmiCompareActivity;
import com.financialcalculator.emi.emifixedvsreducing.FixedVsReducingActivity;
import com.financialcalculator.gst.GstCalculatorActivity;
import com.financialcalculator.gst.VatCalculatorActivity;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.roomdb.tables.EMISearchHistoryEntity;
import com.financialcalculator.sip.LumpSumpSipActivity;
import com.financialcalculator.sip.SIPCalculatorActivity;
import com.financialcalculator.sip.SIPGoalCalculatorActivity;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Constants;

import java.util.List;

public class SerachHistoryACtivity extends BaseActivity implements View.OnClickListener {

    FloatingActionButton fab;
    RecyclerView rvSearchList;
    int type = 0;
    RoomDatabase appDatabase;

    List<EMISearchHistoryEntity> emiSearchHistoryEntityList;
    EmiSearchHistoryAdapter emiSearchHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach_history_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appDatabase = RoomDatabase.getAppDatabase(this);
        if (getIntent().hasExtra("TYPE")) {
            type = getIntent().getIntExtra("TYPE", 0);
        }
        init_widgets();
        setListeners();
        init_Adapters();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        switch (type) {
            case Constants.EMI_CALCULATOR:
                new GetAllEmi().execute();
                break;
        }
    }

    private void init_widgets() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rvSearchList = findViewById(R.id.rvSearchList);
        rvSearchList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setListeners() {
        fab.setOnClickListener(this);
    }

    private void init_Adapters() {
        switch (type) {
            case Constants.EMI_CALCULATOR:
                if (getIntent().hasExtra("LIST")) {
                    emiSearchHistoryEntityList = getIntent().getParcelableArrayListExtra("LIST");
                    emiSearchHistoryAdapter = new EmiSearchHistoryAdapter(this, emiSearchHistoryEntityList);
                    rvSearchList.setAdapter(emiSearchHistoryAdapter);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                redirectToResACtivity();
                break;
        }
    }

    public void redirectToEmiCAlculator(EMISearchHistoryEntity emiSearchHistoryEntity) {
        startActivity(new Intent(this, EmiCalculatorActivity.class)
                .putExtra("LIST", (Parcelable) emiSearchHistoryEntity));
    }

    public void deleteEmiCAlculatot(EMISearchHistoryEntity emiSearchHistoryEntity) {
        new DeleteEMi(emiSearchHistoryEntity).execute();
    }

    public class DeleteEMi extends AsyncTask<Void, Void, Void> {
        EMISearchHistoryEntity emiSearchHistoryEntity;

        public DeleteEMi(EMISearchHistoryEntity emiSearchHistoryEntity) {
            this.emiSearchHistoryEntity = emiSearchHistoryEntity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            emiSearchHistoryEntityList.remove(emiSearchHistoryEntity);
            appDatabase.emiSearchHistoryDao().delete(emiSearchHistoryEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cancelDialog();
            super.onPostExecute(aVoid);
            emiSearchHistoryAdapter.notifyDataSetChanged();
        }
    }

    public class GetAllEmi extends AsyncTask<Void, Void, Void> {
        EMISearchHistoryEntity emiSearchHistoryEntity;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            emiSearchHistoryEntityList = appDatabase.emiSearchHistoryDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cancelDialog();
            super.onPostExecute(aVoid);
            emiSearchHistoryAdapter = new EmiSearchHistoryAdapter(SerachHistoryACtivity.this,emiSearchHistoryEntityList);
            rvSearchList.setAdapter(emiSearchHistoryAdapter);
        }
    }

    public void redirectToResACtivity() {
        switch (type) {
            case Constants.EMI_CALCULATOR:
                startActivity(new Intent(this, EmiCalculatorActivity.class));
                break;
            case Constants.COMPARE_LOAN:
                startActivity(new Intent(this, EmiCompareActivity.class));
                break;
            case Constants.FLAT_VS_REDUCING:
                startActivity(new Intent(this, FixedVsReducingActivity.class));
                break;
            case Constants.GST_CALCULATOR:
                startActivity(new Intent(this, GstCalculatorActivity.class));
                break;
            case Constants.VAT_CALCULATOR:
                startActivity(new Intent(this, VatCalculatorActivity.class));
                break;
            case Constants.FD_CALCULATOR:
                startActivity(new Intent(this, FDCalculatorActivity.class));
                break;
            case Constants.RD_CALCULATOR:
                startActivity(new Intent(this, RDCalculatorActivity.class));
                break;
            case Constants.PPF_CALCULATOR:
                startActivity(new Intent(this, PPFCalculatotActivity.class));
                break;
            case Constants.SIP_CALCULATOR:
                startActivity(new Intent(this, SIPCalculatorActivity.class));
                break;
            case Constants.ADVANCE_SIP_CALCULATOR:
                startActivity(new Intent(this, SIPGoalCalculatorActivity.class));
                break;
            case Constants.LUMPSUMP_CALCULATOR:
                startActivity(new Intent(this, LumpSumpSipActivity.class));
                break;

        }
    }
}
