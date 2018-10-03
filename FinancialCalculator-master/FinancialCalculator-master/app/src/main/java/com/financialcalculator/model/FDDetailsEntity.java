package com.financialcalculator.model;

import java.util.List;

public class FDDetailsEntity {
    List<FDEntity> fdEntityLIst;
    boolean childVisible;
    String month, year, balance, interest;

    public FDDetailsEntity(List<FDEntity> fdEntityLIst, String year) {
        this.fdEntityLIst = fdEntityLIst;
        this.childVisible = false;
        this.year = year;
    }

    public FDDetailsEntity() {
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public List<FDEntity> getFdEntityLIst() {
        return fdEntityLIst;
    }

    public void setFdEntityLIst(List<FDEntity> fdEntityLIst) {
        this.fdEntityLIst = fdEntityLIst;
    }

    public boolean isChildVisible() {
        return childVisible;
    }

    public void setChildVisible(boolean childVisible) {
        this.childVisible = childVisible;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
