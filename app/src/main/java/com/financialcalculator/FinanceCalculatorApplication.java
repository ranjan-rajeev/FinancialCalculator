package com.financialcalculator;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.financialcalculator.roomdb.RoomDatabase;

import java.util.Arrays;
import java.util.List;


public class FinanceCalculatorApplication extends MultiDexApplication {
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

