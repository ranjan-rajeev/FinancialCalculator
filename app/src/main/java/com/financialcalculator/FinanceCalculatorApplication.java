package com.financialcalculator;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.financialcalculator.roomdb.RoomDatabase;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.List;


public class FinanceCalculatorApplication extends MultiDexApplication {
    private static FinanceCalculatorApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        RoomDatabase.getAppDatabase(this); //This will provide RoomDatabase Instance
        MobileAds.initialize(this);//initialize ads
        addTestDevice();
    }

    private void addTestDevice() {
        List<String> testDeviceIds = Arrays.asList("95646C9BB72CC16CA903FF766625E09E", "E4C1A1E239A779858B55CE1641C2BC02");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static FinanceCalculatorApplication getInstance() {
        return mInstance;
    }
}

