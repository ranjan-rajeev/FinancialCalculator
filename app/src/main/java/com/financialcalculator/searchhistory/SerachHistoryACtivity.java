package com.financialcalculator.searchhistory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.banking.fd.FDCalculatorActivity;
import com.financialcalculator.emi.emicalculator.EmiCalculatorActivity;
import com.financialcalculator.emi.emicalculator.EmiSearchHistoryAdapter;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.roomdb.tables.EMISearchHistoryEntity;
import com.financialcalculator.roomdb.tables.GenericSearchHistoryEntity;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SerachHistoryACtivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 18;
    FloatingActionButton fab;
    RecyclerView rvSearchList;
    int type = 0;


    List<EMISearchHistoryEntity> emiSearchHistoryEntityList;
    EmiSearchHistoryAdapter emiSearchHistoryAdapter;

    RoomDatabase appDatabase;
    List<GenericSearchHistoryEntity> genericSearchHistoryEntities;
    GenericSearchHistoryAdapter genericSearchHistoryAdapter;
    //private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach_history_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showBannerAd();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appDatabase = RoomDatabase.getAppDatabase(this);
        if (getIntent().hasExtra("TYPE")) {
            type = getIntent().getIntExtra("TYPE", 0);
        }
        init_widgets();
        setListeners();
        new FilterGenericList().execute();

        //init_Adapters();
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        switch (type) {
            case Constants.EMI_CALCULATOR:
                new GetAllEmi().execute();
                break;
        }
    }*/

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
            emiSearchHistoryAdapter = new EmiSearchHistoryAdapter(SerachHistoryACtivity.this, emiSearchHistoryEntityList);
            rvSearchList.setAdapter(emiSearchHistoryAdapter);
        }
    }

    public void redirectToResACtivity() {

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();


        /*switch (type) {
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

        }*/
    }

    public void redirectToResACtivity(GenericSearchHistoryEntity genericSearchHistoryEntity) {
        Intent returnIntent = new Intent(this, FDCalculatorActivity.class);
        returnIntent.putExtra("DATA", genericSearchHistoryEntity);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();


        /*switch (type) {
            case Constants.EMI_CALCULATOR:
                startActivity(new Intent(this, EmiCalculatorActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.COMPARE_LOAN:
                startActivity(new Intent(this, EmiCompareActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.FLAT_VS_REDUCING:
                startActivity(new Intent(this, FixedVsReducingActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.GST_CALCULATOR:
                startActivity(new Intent(this, GstCalculatorActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.VAT_CALCULATOR:
                startActivity(new Intent(this, VatCalculatorActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.FD_CALCULATOR:
                startActivity(new Intent(this, FDCalculatorActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.RD_CALCULATOR:
                startActivity(new Intent(this, RDCalculatorActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.PPF_CALCULATOR:
                startActivity(new Intent(this, PPFCalculatotActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.SIP_CALCULATOR:
                startActivity(new Intent(this, SIPCalculatorActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.ADVANCE_SIP_CALCULATOR:
                startActivity(new Intent(this, SIPGoalCalculatorActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;
            case Constants.LUMPSUMP_CALCULATOR:
                startActivity(new Intent(this, LumpSumpSipActivity.class)
                        .putExtra("DATA", genericSearchHistoryEntity));
                break;

        }*/
    }

    private class FilterGenericList extends AsyncTask<Void, Void, List<GenericSearchHistoryEntity>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog("Fetching Search history...");
        }

        @Override
        protected List<GenericSearchHistoryEntity> doInBackground(Void... voids) {
            return appDatabase.genericSearchHistoryDao().getListByType(type);
        }

        @Override
        protected void onPostExecute(List<GenericSearchHistoryEntity> list) {
            super.onPostExecute(list);
            genericSearchHistoryEntities = list;
            genericSearchHistoryAdapter = new GenericSearchHistoryAdapter(SerachHistoryACtivity.this, list);
            rvSearchList.setAdapter(genericSearchHistoryAdapter);
            cancelDialog();
        }
    }

    private class DeleteGenericList extends AsyncTask<Void, Void, Void> {
        GenericSearchHistoryEntity genericSearchHistoryEntity;

        DeleteGenericList(GenericSearchHistoryEntity genericSearchHistoryEntity) {
            this.genericSearchHistoryEntity = genericSearchHistoryEntity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog("Deleting history...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            genericSearchHistoryEntities.remove(genericSearchHistoryEntity);
            appDatabase.genericSearchHistoryDao().delete(genericSearchHistoryEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            genericSearchHistoryAdapter.notifyDataSetChanged();
            cancelDialog();
        }
    }

    public void deleteSearchItem(GenericSearchHistoryEntity genericSearchHistoryEntity) {
        new DeleteGenericList(genericSearchHistoryEntity).execute();
    }
}
