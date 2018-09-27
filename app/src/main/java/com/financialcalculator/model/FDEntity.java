package com.financialcalculator.model;

public class FDEntity {
    String month, year, balance, interest, interestTotal;

    public FDEntity(String month, String year, String balance, String interest) {
        this.month = month;
        this.year = year;
        this.balance = balance;
        this.interest = interest;
        this.interestTotal = "0";
    }

    public String getInterestTotal() {
        return interestTotal;
    }

    public void setInterestTotal(String interestTotal) {
        this.interestTotal = interestTotal;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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
}
