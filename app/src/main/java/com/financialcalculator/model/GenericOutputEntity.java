package com.financialcalculator.model;

public class GenericOutputEntity {

    /**
     * calId : 150
     * firebaseId : -M6F_bNHOhbKJPcS1D-c
     * formulae : a+b
     * outId : 1
     * outKey : c
     * outMsg : Sum of two numbers
     * type : 1
     */

    private int calId;
    private String firebaseId;
    private String formulae;
    private int outId;
    private String outKey;
    private String outMsg;
    private int type;
    private String data;
    private String curr;

    public String getCurr() {
        return curr;
    }

    public void setCurr(String curr) {
        this.curr = curr;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCalId() {
        return calId;
    }

    public void setCalId(int calId) {
        this.calId = calId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getFormulae() {
        return formulae;
    }

    public void setFormulae(String formulae) {
        this.formulae = formulae;
    }

    public int getOutId() {
        return outId;
    }

    public void setOutId(int outId) {
        this.outId = outId;
    }

    public String getOutKey() {
        return outKey;
    }

    public void setOutKey(String outKey) {
        this.outKey = outKey;
    }

    public String getOutMsg() {
        return outMsg;
    }

    public void setOutMsg(String outMsg) {
        this.outMsg = outMsg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
