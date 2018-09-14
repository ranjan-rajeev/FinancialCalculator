package com.financialcalculator.model;

import java.text.DecimalFormat;
import java.util.List;

public class YearsDetailsEntity {

    List<DetailsEntity> detailsEntityList;

    boolean childVisible;
    int year;

    public boolean isChildVisible() {
        return childVisible;
    }

    public void setChildVisible(boolean childVisible) {
        this.childVisible = childVisible;
    }

    String month;
    double principal;
    double interest;
    double totalPayment;
    double balance;
    double loanPaidPercent;

    DecimalFormat format = new DecimalFormat("##.00");

    public List<DetailsEntity> getDetailsEntityList() {
        return detailsEntityList;
    }

    public void setDetailsEntityList(List<DetailsEntity> detailsEntityList) {
        this.detailsEntityList = detailsEntityList;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getLoanPaidPercent() {
        return loanPaidPercent;
    }

    public void setLoanPaidPercent(double loanPaidPercent) {
        this.loanPaidPercent = loanPaidPercent;
    }


    public YearsDetailsEntity(int year, String month, double principal, double interest, double totalPayment, double balance, double loanPaidPercent) {
        this.year = year;
        this.month = month;
        this.principal = Math.round(principal);
        this.interest = Math.round(interest);
        this.totalPayment = Math.round(totalPayment);
        this.balance = Math.round(balance);
        this.loanPaidPercent = Math.round(loanPaidPercent);
        this.childVisible = false;
    }
}
