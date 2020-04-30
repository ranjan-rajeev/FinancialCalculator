package com.financialcalculator.model;

public class EditTextEntity {
    int inpType;
    int length;
    int isFocus;
    String regex;
    String data;

    public int getInpType() {
        return inpType;
    }

    public void setInpType(int inpType) {
        this.inpType = inpType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getIsFocus() {
        return isFocus;
    }

    public void setIsFocus(int isFocus) {
        this.isFocus = isFocus;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}