package com.financialcalculator.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.financialcalculator.BuildConfig;
import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Logger;
import com.financialcalculator.utility.Util;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;


public class FirebaseHelper {

    //firebase realtime DB
    public static final String DB_CALCULATOR = "calculator";
    public static final String DB_DASHBOARD = "dashboard";
    public static final String DB_MOREINFO = "moreinfo";
    public static final String GALLERY_LIST = "gallery_list";
    public static final String FACULTY_LIST = "faculty_list";
    public static final String DASHBOARD_LIST = "dashboard_list";
    public static final String BOOK_LIST = "book_list";
    public static final String BANNER_LIST = "banner_list";
    public static final String SCROLL_MESSAGE = "scroll_msg";
    public static final String UPLOAD_ENABLE = "upload_enabled";
    public static final String REALTIME_ENABLED = "realtime_enabled";
    public static final String VERSION_CODE = "app_version_code";
    public static final String FORCE_UPDATE = "force_update";
    public static final String CALCULATOR_VERSION = "calculator_version";
    public static final String DASHBOARD_VERSION = "dashboard_version";
    public static final String MORE_INFO_VERSION = "more_info_version";
    public static final String FA_EVENTS_APP_OPENED = "app_opened";
    public static final String FA_EVENTS_APP_USER = "app_user";
    public static final String FA_EVENTS_APP_UNIQUE_ID = "android_id";
    public static Boolean isFIrebaseFetchSuccessfull = false;

    public static final String AD_BANNER_ID = "ad_banner_id";

    public static void getValuesFromFirebaseRemoteConfig() {

        long cacheExpiration = 0; // 6 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.

//      if (FirebaseRemoteConfig.getInstance().getInfo().getConfigSettings().isDeveloperModeEnabled()) {
        if (BuildConfig.DEBUG) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.

        FirebaseRemoteConfig.getInstance().fetch(1)
                .addOnCompleteListener(task -> {
                    Logger.v("Task", ">>>>>>>>>>>>>>>>>>>>>>>>> FirebaseConfig OnComplete: " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        FirebaseRemoteConfig.getInstance().activate();
                        Logger.d(getBannerList());
                        isFIrebaseFetchSuccessfull = true;
                    } else {
                        isFIrebaseFetchSuccessfull = false;
                    }
                }).addOnFailureListener(task -> {
            Logger.v("Task", ">>>>>>>>>>>>>>>>>>>>>>>>> FirebaseConfig OnComplete: Failed");
        });
        // [END fetch_config_with_callback]
    }

    public static long getDashBoardVersion() {
        return FirebaseRemoteConfig.getInstance().getLong(DASHBOARD_VERSION);
    }

    public static Boolean getIsFIrebaseFetchSuccessfull() {
        return isFIrebaseFetchSuccessfull;
    }

    public static String getGalleryList() {
        return FirebaseRemoteConfig.getInstance().getString(GALLERY_LIST);
    }

    public static String getFacultyList() {
        return FirebaseRemoteConfig.getInstance().getString(FACULTY_LIST);
    }

    public static String getDashboardList() {
        return FirebaseRemoteConfig.getInstance().getString(DASHBOARD_LIST);
    }

    public static String getBookList() {
        return FirebaseRemoteConfig.getInstance().getString(BOOK_LIST);
    }

    public static String getBannerList() {
        return FirebaseRemoteConfig.getInstance().getString(BANNER_LIST);
    }

    public static String getScrollMessage() {
        return FirebaseRemoteConfig.getInstance().getString(SCROLL_MESSAGE);
    }

    public static Boolean getUploadEnable() {
        return FirebaseRemoteConfig.getInstance().getBoolean(UPLOAD_ENABLE);
    }

    public static Boolean getRealtimeEnabled() {
        return FirebaseRemoteConfig.getInstance().getBoolean(REALTIME_ENABLED);
    }

    public static String getAdBannerId() {
        return FirebaseRemoteConfig.getInstance().getString(AD_BANNER_ID);
    }

    public static Boolean getForceUpdate() {
        return FirebaseRemoteConfig.getInstance().getBoolean(FORCE_UPDATE);
    }

    public static Long getServerVersionCode() {
        return FirebaseRemoteConfig.getInstance().getLong(VERSION_CODE);
    }

    public static Long getCalculatorVersion() {
        return FirebaseRemoteConfig.getInstance().getLong(CALCULATOR_VERSION);
    }

    public static Long getMoreInfoVersion() {
        return FirebaseRemoteConfig.getInstance().getLong(MORE_INFO_VERSION);
    }

    @SuppressLint("InvalidAnalyticsName")
    public static void logAppOpenedEvent(Context context) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(FA_EVENTS_APP_UNIQUE_ID, Util.getUniqueDeviceId(context));
            String user = SharedPrefManager.getInstance(context).getStringValueForKey(Constants.SHD_PRF_USER_DETAILS, "");
            bundle.putString(FA_EVENTS_APP_USER, user);
            FirebaseAnalytics.getInstance(context).logEvent(FA_EVENTS_APP_OPENED, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
