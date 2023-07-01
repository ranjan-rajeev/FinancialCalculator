package com.financialcalculator;

import android.app.Application;
import android.content.Context;


import com.financialcalculator.roomdb.RoomDatabase;



public class FinanceCalculatorApplication extends Application {
    private static FinanceCalculatorApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        RoomDatabase.getAppDatabase(this); //This will provide RoomDatabase Instance
        addTestDevice();
    }

    private void addTestDevice() {

    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static FinanceCalculatorApplication getInstance() {
        return mInstance;
    }
}

