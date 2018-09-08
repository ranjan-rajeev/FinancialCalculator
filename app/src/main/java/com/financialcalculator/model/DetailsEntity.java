package com.financialcalculator.model;

import java.text.DecimalFormat;

public class DetailsEntity {
    DecimalFormat format = new DecimalFormat("##.00");
    int year;
    int month;
    double principal;
    double interest;
    double totalPayment;
    double balance;
    double loanPaidPercent;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
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


    public DetailsEntity(int year, int month, double principal, double interest, double totalPayment, double balance, double loanPaidPercent) {
        this.year = year;
        this.month = month;
        this.principal = Math.round(principal);
        this.interest = Math.round(interest);
        this.totalPayment = Math.round(totalPayment);
        this.balance = Math.round(balance);
        this.loanPaidPercent = Math.round(loanPaidPercent);
    }
}
