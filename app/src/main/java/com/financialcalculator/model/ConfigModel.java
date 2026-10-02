package com.financialcalculator.model;

public class ConfigModel {
    String BANNER_PLACEMENT_ID;
    int playStoreVersion;
    boolean showAds;
    boolean inAppReviewEnabled = true;
    int inAppReviewCooldownDays = 2;

    public String getBANNER_PLACEMENT_ID() {
        return BANNER_PLACEMENT_ID;
    }

    public void setBANNER_PLACEMENT_ID(String BANNER_PLACEMENT_ID) {
        this.BANNER_PLACEMENT_ID = BANNER_PLACEMENT_ID;
    }

    public int getPlayStoreVersion() {
        return playStoreVersion;
    }

    public void setPlayStoreVersion(int playStoreVersion) {
        this.playStoreVersion = playStoreVersion;
    }

    public boolean isShowAds() {
        return showAds;
    }

    public void setShowAds(boolean showAds) {
        this.showAds = showAds;
    }

    public boolean isInAppReviewEnabled() {
        return inAppReviewEnabled;
    }

    public void setInAppReviewEnabled(boolean inAppReviewEnabled) {
        this.inAppReviewEnabled = inAppReviewEnabled;
    }

    public int getInAppReviewCooldownDays() {
        return inAppReviewCooldownDays;
    }

    public void setInAppReviewCooldownDays(int inAppReviewCooldownDays) {
        this.inAppReviewCooldownDays = inAppReviewCooldownDays;
    }
}
