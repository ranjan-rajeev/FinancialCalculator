package com.financialcalculator.model;

public class InputTypeEntity {

    /**
     * calId : 100
     * data : {"inpType":1,"length":12,"isFocus":1,"regex":""}
     * firebaseId : -M60nZVPV-9JsM7zHlQk
     * inpId : 10
     * key : a
     * title : Enter First Number
     * type : 1
     */

    private int calId;
    private String data;
    private String firebaseId;
    private int inpId;
    private String key;
    private String title;
    private int type;

    public int getCalId() {
        return calId;
    }

    public void setCalId(int calId) {
        this.calId = calId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public int getInpId() {
        return inpId;
    }

    public void setInpId(int inpId) {
        this.inpId = inpId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
