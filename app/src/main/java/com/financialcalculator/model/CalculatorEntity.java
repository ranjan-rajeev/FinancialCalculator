package com.financialcalculator.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

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
    private List<GenericViewTypeModel> inputList;
    private List<GenericOutputEntity> outputList;
    private String redUrl;
    private int version;

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

    public List<GenericViewTypeModel> getInputList() {
        return inputList;
    }

    public void setInputList(List<GenericViewTypeModel> inputList) {
        this.inputList = inputList;
    }

    public List<GenericOutputEntity> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<GenericOutputEntity> outputList) {
        this.outputList = outputList;
    }

    public String getRedUrl() {
        return redUrl;
    }

    public void setRedUrl(String redUrl) {
        this.redUrl = redUrl;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
