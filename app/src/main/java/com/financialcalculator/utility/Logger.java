//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.financialcalculator.utility;

import android.util.Log;

import com.financialcalculator.BuildConfig;


public class Logger {
    public static final String TAG = "Free";
    private static boolean DEBUG_ENABLED = BuildConfig.DEBUG;

    private Logger() {
    }

    public static void v(String msg) {
        if (isDebugEnabled()) {
            Log.v(TAG, msg);
        }

    }


    public static void v(String tag, String msg) {
        if (isDebugEnabled()) {
            Log.v(tag, msg);
        }

    }

    public static void d(String msg) {
        if (isDebugEnabled()) {
            Log.d(TAG, msg);
        }

    }


    public static void d(String tag, String msg) {
        if (isDebugEnabled()) {
            Log.d(tag, msg);
        }

    }

    public static void i(String msg) {
        if (isDebugEnabled()) {
            Log.i(TAG, msg);
        }

    }


    public static void i(String tag, String msg) {
        if (isDebugEnabled()) {
            Log.i(tag, msg);
        }

    }

    public static void w(String msg) {
        if (isDebugEnabled()) {
            Log.w(TAG, msg);
        }

    }


    public static void w(String tag, String msg) {
        if (isDebugEnabled()) {
            Log.w(tag, msg);
        }

    }

    public static void e(String msg) {

        if (isDebugEnabled()) {
            Log.e(TAG, msg);
        }

    }


    public static void e(String tag, String msg) {
        if (isDebugEnabled()) {
            Log.e(tag, msg);
        }

    }

    public static void f(String msg) {
    }


    public static void f(String tag, String msg) {
    }

    public static boolean isDebugEnabled() {
        return DEBUG_ENABLED;
    }

    public static void setLogStatus(boolean value) {
        DEBUG_ENABLED = value;
    }
}
