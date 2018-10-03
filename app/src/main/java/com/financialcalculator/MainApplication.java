package com.financialcalculator;

import android.app.Application;

import com.financialcalculator.roomdb.RoomDatabase;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RoomDatabase.getAppDatabase(this); //This will provide RoomDatabase Instance
    }
}

