package com.financialcalculator.model;

import java.io.Serializable;

public class CarouselEntity  extends  BaseCompModel implements Serializable {

    /**
     * cmpId : 1
     * firebaseId : -M5wkvzzG2BD5179J9US
     * redUrl : facebook.com
     * webUrl : google.com
     */

    private int cmpId;
    private String firebaseId;
    private String redUrl;
    private String webUrl;
    private long interval;

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int getCmpId() {
        return cmpId;
    }

    public void setCmpId(int cmpId) {
        this.cmpId = cmpId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getRedUrl() {
        return redUrl;
    }

    public void setRedUrl(String redUrl) {
        this.redUrl = redUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
