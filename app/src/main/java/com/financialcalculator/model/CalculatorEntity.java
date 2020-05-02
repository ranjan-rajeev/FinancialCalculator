package com.financialcalculator.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

public class CalculatorEntity implements Serializable {


    /**
     * calId : 10
     * calName : First Calculator
     * firebaseId : -M6-upbEdM9PQdUW4OqL
     * iconUrl :
     * input :
     * output :
     * redUrl : MainACtivity
     */

    private int calId;
    private String calName;
    private String firebaseId;
    private String iconUrl;
    private String input;
    private String output;
    private String redUrl;
    public HashMap<Character, BigDecimal> inputHashmap = new HashMap<>();

    public HashMap<Character, BigDecimal> getInputHashmap() {
        return inputHashmap;
    }

    public void setInputHashmap(HashMap<Character, BigDecimal> inputHashmap) {
        this.inputHashmap = inputHashmap;
    }

    public int getCalId() {
        return calId;
    }

    public void setCalId(int calId) {
        this.calId = calId;
    }

    public String getCalName() {
        return calName;
    }

    public void setCalName(String calName) {
        this.calName = calName;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getRedUrl() {
        return redUrl;
    }

    public void setRedUrl(String redUrl) {
        this.redUrl = redUrl;
    }
}
