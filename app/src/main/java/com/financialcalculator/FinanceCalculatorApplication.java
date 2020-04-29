package com.financialcalculator;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.utility.Constants;
import com.google.android.gms.ads.MobileAds;

public class FinanceCalculatorApplication extends MultiDexApplication {
    private static FinanceCalculatorApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        RoomDatabase.getAppDatabase(this); //This will provide RoomDatabase Instance
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static FinanceCalculatorApplication getInstance() {
        return mInstance;
    }
}

