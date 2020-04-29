package com.financialcalculator.model;

public class HomePageModel {

    /**
     * cmpData : [{"redUrl":"facebook.com","webUrl":"google.com"},{"redUrl":"google.com","webUrl":"facebook.com"},{"redUrl":"test","webUrl":"test"}]
     * cmpId : 3
     * cmpPos : 3
     * cmpType : 1
     * firebaseId : -M6-KPdn_71-MPV3nEBJ
     */

    private String cmpData;
    private String cmpId;
    private int cmpPos;
    private int cmpType;
    private String firebaseId;

    public String getCmpData() {
        return cmpData;
    }

    public void setCmpData(String cmpData) {
        this.cmpData = cmpData;
    }

    public String getCmpId() {
        return cmpId;
    }

    public void setCmpId(String cmpId) {
        this.cmpId = cmpId;
    }

    public int getCmpPos() {
        return cmpPos;
    }

    public void setCmpPos(int cmpPos) {
        this.cmpPos = cmpPos;
    }

    public int getCmpType() {
        return cmpType;
    }

    public void setCmpType(int cmpType) {
        this.cmpType = cmpType;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}