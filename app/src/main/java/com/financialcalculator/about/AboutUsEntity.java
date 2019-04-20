package com.financialcalculator.about;

import java.util.ArrayList;
import java.util.List;

public class AboutUsEntity {
    String title;
    String desc1, desc2, desc3;

    public AboutUsEntity() {

    }

    public AboutUsEntity(String title, String desc1, String desc2, String desc3) {
        this.title = title;
        this.desc1 = desc1;
        this.desc2 = desc2;
        this.desc3 = desc3;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc1() {
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getDesc3() {
        return desc3;
    }

    public void setDesc3(String desc3) {
        this.desc3 = desc3;
    }

    public List<AboutUsEntity> getListAboutUsEntity() {
        List<AboutUsEntity> list = new ArrayList<>();
        list.add(new AboutUsEntity("EMI CALCULATORS", "EMI Calculator (Loan Calculator)", "Compare Loan", "Flat vs Reducing rate"));
        list.add(new AboutUsEntity("Loan Profile", "Create Loan Profile", "View Loan Profile", "Home Loan Eligibility"));
        list.add(new AboutUsEntity("MUTUAL FUNDS & SIP  CALCULATORS", "Systematic Investment Plan Calculator (SIP)", "Goal SIP Calculator", "Lumpsum SIP Calculator"));
        list.add(new AboutUsEntity("BANK CALCULATORS", "Fixed Deposit Calculator (TDR - Interest Payout)", "Recurring Deposit Calculator (RD)", "PPF Calculator (Public Provident Fund)"));
        list.add(new AboutUsEntity("GST CALCULATOR", "GST Calculator (Add)", "GST Calculator (Remove)", "VAT Calculator"));
        return list;
    }
}
