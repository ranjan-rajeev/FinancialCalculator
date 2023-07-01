package com.financialcalculator.loanprofile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.roomdb.tables.GenericSearchHistoryEntity;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Constants;

import java.util.List;


public class ViewLoanProfile extends BaseActivity {

    //private AdView mAdView;
    RecyclerView rvDashboard;
    List<GenericSearchHistoryEntity> genericSearchHistoryEntities;
    LoanProfileAdapter loanProfileAdapter;
    RoomDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_loan_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appDatabase = RoomDatabase.getAppDatabase(this);

        init_widgets();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new FilterGenericList().execute();
    }



    private void init_widgets() {
        rvDashboard = findViewById(R.id.rvDashboard);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvDashboard.setLayoutManager(layoutManager);
    }

    public void redirectToCreateLoan(GenericSearchHistoryEntity detailsEntity) {

        Intent returnIntent = new Intent(this, CreateLoanProfileActivity.class);
        returnIntent.putExtra("DATA", detailsEntity);
        startActivity(returnIntent);
        //finish();
    }

    private class FilterGenericList extends AsyncTask<Void, Void, List<GenericSearchHistoryEntity>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog("Fetching Search history...");
        }

        @Override
        protected List<GenericSearchHistoryEntity> doInBackground(Void... voids) {
            return appDatabase.genericSearchHistoryDao().getListByType(Constants.LOAN_PROFILE);
        }

        @Override
        protected void onPostExecute(List<GenericSearchHistoryEntity> list) {
            super.onPostExecute(list);
            genericSearchHistoryEntities = list;
            loanProfileAdapter = new LoanProfileAdapter(ViewLoanProfile.this, list);
            rvDashboard.setAdapter(loanProfileAdapter);
            cancelDialog();
        }
    }
}
