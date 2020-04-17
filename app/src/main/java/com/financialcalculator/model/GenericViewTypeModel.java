package com.financialcalculator.model;

import com.financialcalculator.generic.GenericViewTypeAdapter;

import java.util.ArrayList;
import java.util.List;

public class GenericViewTypeModel {

    int viewId;
    int viewType;
    String viewTitle;
    String inputType;
    String regex;
    int maxLength;
    boolean isFocusable = true;
    List<String> data;
    int action;

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getViewTitle() {
        return viewTitle;
    }

    public void setViewTitle(String viewTitle) {
        this.viewTitle = viewTitle;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isFocusable() {
        return isFocusable;
    }

    public void setFocusable(boolean focusable) {
        isFocusable = focusable;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public List<GenericViewTypeModel> getDummyList() {
        List<GenericViewTypeModel> genericViewTypeModels = new ArrayList<>();

        GenericViewTypeModel etPrincipal = new GenericViewTypeModel();
        etPrincipal.setViewType(GenericViewTypeAdapter.TYPE_EDITTEXT);
        etPrincipal.setViewId(1);
        etPrincipal.setViewTitle("Your Age : ");
        etPrincipal.setFocusable(true);
        etPrincipal.setInputType("number");
        etPrincipal.setMaxLength(2);

        GenericViewTypeModel spTitle = new GenericViewTypeModel();
        spTitle.setViewType(GenericViewTypeAdapter.TYPE_SPINNER_TITLE);
        spTitle.setViewTitle("You want to receive Monthly Pention :");

        GenericViewTypeModel monthlySpinner = new GenericViewTypeModel();
        monthlySpinner.setViewType(GenericViewTypeAdapter.TYPE_SPINNER);
        monthlySpinner.setViewId(2);
        monthlySpinner.setData(new ArrayList<String>() {
            {
                add("Rs 1,000/-");
                add("Rs 2,000/-");
                add("Rs 3,000/-");
                add("Rs 4,000/-");
                add("Rs 5,000/-");
            }
        });

        GenericViewTypeModel spTitle2 = new GenericViewTypeModel();
        spTitle2.setViewType(GenericViewTypeAdapter.TYPE_SPINNER_TITLE);
        spTitle2.setViewTitle("You will pay your contribution :");

        GenericViewTypeModel monthlySpinner2 = new GenericViewTypeModel();
        monthlySpinner2.setViewType(GenericViewTypeAdapter.TYPE_SPINNER);
        monthlySpinner2.setViewId(3);
        monthlySpinner2.setData(new ArrayList<String>() {
            {
                add("Monthly");
                add("Quarterly");
                add("Half Yearly");
            }
        });


        GenericViewTypeModel twoView  = new GenericViewTypeModel();
        twoView.setViewType(GenericViewTypeAdapter.TYPE_EDITTEXT_SPINNER);
        twoView.setViewTitle("Principal Amount");
        twoView.setViewId(3);
        twoView.setData(new ArrayList<String>() {
            {
                add("Monthly");
                add("Quarterly");
                add("Half Yearly");
            }
        });

        GenericViewTypeModel spTitle3 = new GenericViewTypeModel();
        spTitle3.setViewType(GenericViewTypeAdapter.TYPE_SPINNER_TITLE);
        spTitle3.setViewTitle("Retirement Age     :       60 Years");


        GenericViewTypeModel buttonCalculate  = new GenericViewTypeModel();
        buttonCalculate.setViewType(GenericViewTypeAdapter.TYPE_BUTTON);
        buttonCalculate.setViewTitle("Calculate");


        genericViewTypeModels.add(twoView);
        genericViewTypeModels.add(etPrincipal);
        genericViewTypeModels.add(spTitle);

        genericViewTypeModels.add(monthlySpinner);
        genericViewTypeModels.add(spTitle2);

        genericViewTypeModels.add(monthlySpinner2);
        genericViewTypeModels.add(spTitle3);
        genericViewTypeModels.add(buttonCalculate);

       /* genericViewTypeModels.add(etPrincipal);
        genericViewTypeModels.add(spTitle);

        genericViewTypeModels.add(monthlySpinner);
        genericViewTypeModels.add(spTitle2);

        genericViewTypeModels.add(monthlySpinner2);
        genericViewTypeModels.add(spTitle3);
        genericViewTypeModels.add(buttonCalculate);*/


        return genericViewTypeModels;
    }
}
