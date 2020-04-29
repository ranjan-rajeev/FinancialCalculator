package com.financialcalculator.model;

import com.financialcalculator.generic.GenericViewTypeAdapter;

import java.util.ArrayList;
import java.util.List;

public class GenericViewTypeModel {


    /**
     * calId : 100
     * data : {"inpType":1,"length":12,"isFocus":1,"regex":""}
     * firebaseId : -M60nZVPV-9JsM7zHlQk
     * inpId : 10
     * key : a
     * title : Enter First Number
     * type : 1
     */

    private int calId;
    private String data;
    private String firebaseId;
    private int inpId;
    private String key;
    private String title;
    private int type;

    public int getCalId() {
        return calId;
    }

    public void setCalId(int calId) {
        this.calId = calId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public int getInpId() {
        return inpId;
    }

    public void setInpId(int inpId) {
        this.inpId = inpId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

   /* public List<GenericViewTypeModel> getDummyList() {
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

       *//* genericViewTypeModels.add(etPrincipal);
        genericViewTypeModels.add(spTitle);

        genericViewTypeModels.add(monthlySpinner);
        genericViewTypeModels.add(spTitle2);

        genericViewTypeModels.add(monthlySpinner2);
        genericViewTypeModels.add(spTitle3);
        genericViewTypeModels.add(buttonCalculate);*//*


        return genericViewTypeModels;
    }*/
}
