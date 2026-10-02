package com.financialcalculator.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.model.ConfigModel;
import com.financialcalculator.utility.Constants;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class InAppReviewManager {

    private static final String PREFS_NAME = "in_app_review_prefs";
    private static final String KEY_LAST_REVIEW_TIME = "last_review_time";
    private static final String KEY_REVIEW_COUNT = "review_count";
    private static final long DEFAULT_REVIEW_COOLDOWN_MS = 3 * 24 * 60 * 60 * 1000L; // 3 days

    private final Context context;
    private final ReviewManager reviewManager;
    private ReviewInfo reviewInfo;

    public InAppReviewManager(Context context) {
        this.context = context.getApplicationContext();
        this.reviewManager = ReviewManagerFactory.create(this.context);
    }

    public void requestReviewIfNeeded(Activity activity, OnReviewCallback callback) {
        if (!isInAppReviewEnabled()) {
            if (callback != null) {
                callback.onReviewSkipped("In-app review disabled in config");
            }
            return;
        }

        long cooldownMs = getReviewCooldownMs();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastReviewTime = prefs.getLong(KEY_LAST_REVIEW_TIME, 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastReviewTime < cooldownMs) {
            if (callback != null) {
                callback.onReviewSkipped("Cooldown period active");
            }
            return;
        }

        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
                launchReviewFlow(activity, callback);
            } else {
                if (callback != null) {
                    callback.onReviewFailed(task.getException());
                }
            }
        });
    }

    private boolean isInAppReviewEnabled() {
        ConfigModel config = getConfig();
        return config != null && config.isInAppReviewEnabled();
    }

    private long getReviewCooldownMs() {
        ConfigModel config = getConfig();
        if (config != null && config.getInAppReviewCooldownDays() > 0) {
            return config.getInAppReviewCooldownDays() * 24L * 60 * 60 * 1000L;
        }
        return DEFAULT_REVIEW_COOLDOWN_MS;
    }

    private ConfigModel getConfig() {
        try {
            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
            String configJson = sharedPrefManager.getStringValueForKey(Constants.SHD_PRF_CONFIG, "");
            if (!configJson.isEmpty()) {
                Gson gson = new Gson();
                Type type = new TypeToken<ConfigModel>() {}.getType();
                return gson.fromJson(configJson, type);
            }
        } catch (Exception e) {
            // Ignore and use defaults
        }
        return null;
    }

    private void launchReviewFlow(Activity activity, OnReviewCallback callback) {
        if (reviewInfo == null) {
            if (callback != null) {
                callback.onReviewFailed(new IllegalStateException("ReviewInfo is null"));
            }
            return;
        }

        Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
        flow.addOnCompleteListener(task -> {
            updateReviewTimestamp();
            if (callback != null) {
                if (task.isSuccessful()) {
                    callback.onReviewShown();
                } else {
                    callback.onReviewFailed(task.getException());
                }
            }
        });
    }

    private void updateReviewTimestamp() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_LAST_REVIEW_TIME, System.currentTimeMillis());
        editor.putInt(KEY_REVIEW_COUNT, prefs.getInt(KEY_REVIEW_COUNT, 0) + 1);
        editor.apply();
    }

    public static boolean isReviewEligible(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long lastReviewTime = prefs.getLong(KEY_LAST_REVIEW_TIME, 0);
        return System.currentTimeMillis() - lastReviewTime >= DEFAULT_REVIEW_COOLDOWN_MS;
    }

    public interface OnReviewCallback {
        void onReviewShown();
        void onReviewSkipped(String reason);
        void onReviewFailed(Exception e);
    }
}