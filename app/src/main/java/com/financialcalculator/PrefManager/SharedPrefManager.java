package com.financialcalculator.PrefManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.financialcalculator.R;


/**
 * Class contains SharedPreference, all the fields accessing in this class
 * and saved in SharedPreference.
 * <p>
 */

public class SharedPrefManager {
    protected static SharedPrefManager instance;
    private SharedPreferences prefs;
    private Context mContext;
    private SharedPreferences.Editor editor;

    private SharedPrefManager(Context context) {
        mContext = context;
        if (mContext == null)
            return;
        prefs = context.getSharedPreferences(context.getResources().getString(R.string.preferences),
                Context.MODE_PRIVATE);
    }

    public static SharedPrefManager getInstance(Context context) {

        if (instance == null) {
            synchronized (SharedPrefManager.class) {
                if (instance == null) {
                    instance = new SharedPrefManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * Clear all data saved in shared preference
     */
//    public void clearAllData() {
//        String appAccessToken = getStringValueForKey(Constants.PREF_APP_ACCESS_TOKEN_OBJECT, "");
//        boolean isLaunchedFirstTime = getBooleanValueForKey(Constants.IS_LAUNCHED, false);
//        editor = prefs.edit();
//        editor.clear().commit();
//        putStringValueForKey(Constants.PREF_APP_ACCESS_TOKEN_OBJECT, appAccessToken);
//        putBooleanValueForKey(Constants.IS_LAUNCHED, isLaunchedFirstTime);
//    }
//
//    public void clearAllDataOnUpdate() {
//        editor = prefs.edit();
//        editor.clear().commit();
//    }
//
//    public void removePreferencesForLogout() {
//        editor = prefs.edit();
//        editor.remove(Constants.PREF_CUSTOMER_OBJECT);
//        editor.remove(Constants.PREF_CUSTOMER_ACCESS_TOKEN_OBJECT);
//        editor.remove(Constants.PREF_APP_ACCESS_TOKEN_OBJECT);
//        editor.remove(Constants.PREF_GCM_TOKEN);
//        editor.remove(Constants.IS_TOKEN_SENT_TO_SERVER);
//        editor.remove(Constants.PREF_COUPONS);
//        editor.remove(Constants.PREF_ALERTS_ORDER_NOTIFICATIONS);
//        editor.remove(Constants.IS_SEND_NOTIFICATIONS);
//        editor.remove(Constants.PREFERENCE_CART_ID);
//        editor.remove(Constants.PROFILE_IMAGE_PATH);
//        editor.remove(Constants.USER_NAME);
//        editor.remove(Constants.IS_SOCIAL_LOGIN);
//        editor.remove(Constants.PREF_APP_ACCESS_TOKEN_OBJECT);
//        editor.remove(Constants.PREF_CUSTOMER_CART_OBJECT_WITH_GUID);
//        editor.remove(Constants.PREF_USER_WISHLIST);
//        editor.remove(Constants.PREFERENCE_USER_CART);
//        editor.remove(Constants.PREF_BUY_NOW_CART_OBJECT_WITH_GUID);
//        editor.remove(Constants.PREFERENCE_COUPONS_SHOW_BADGE);
//        editor.remove(Constants.PREFERENCE_CLIQCASH_SHOW_BADGE);
//        editor.commit();
//    }
    public boolean getBooleanValueForKey(String key, boolean defValue) {
        boolean result = false;
        if (prefs != null)
            result = prefs.getBoolean(key, defValue);
        return result;
    }

    public void putBooleanValueForKey(String key, boolean value) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.preferences),
                        Context.MODE_PRIVATE);
            }
            editor = prefs.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public String getStringValueForKey(String key, String defValue) {
        String result = "";
        if (prefs != null)
            result = prefs.getString(key, defValue);
        return result;
    }

    public void putStringValueForKey(String key, String value) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.preferences),
                        Context.MODE_PRIVATE);
            }
            editor = prefs.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public Float getFloatValueForKey(String key, Float defValue) {
        float result = 1f;
        if (prefs != null)
            result = prefs.getFloat(key, defValue);
        return result;
    }

    public void putFloatValueForKey(String key, Float value) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.preferences),
                        Context.MODE_PRIVATE);
            }
            editor = prefs.edit();
            editor.putFloat(key, value);
            editor.apply();
        }
    }

    public Integer getIntegerValueForKey(String key, int defValue) {
        int result = 0;
        if (prefs != null)
            result = prefs.getInt(key, defValue);
        return result;
    }

    public void putIntegerValueForKey(String key, int value) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.preferences),
                        Context.MODE_PRIVATE);
            }
            editor = prefs.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }
}
