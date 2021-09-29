package com.financialcalculator.services;


import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.ConfigModel;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.model.MoreInfoEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface FinanceCalculatorServices {

    @GET("/Finanace-Calculator-Web/json/dashboard.json")
    Observable<List<HomePageModel>> getDashboardData();

    @GET("/Finanace-Calculator-Web/json/nps/npscalculator.json")
    Observable<CalculatorEntity> getNpsCalculator();

    @GET("/Finanace-Calculator-Web/json/nps/npsmoreinfo.json")
    Observable<List<MoreInfoEntity>> getNpsMoreInfo();

    @GET("/Finanace-Calculator-Web/json/atal/atalcalculator.json")
    Observable<CalculatorEntity> getAtalCalculator();

    @GET("/Finanace-Calculator-Web/json/atal/atalmoreinfo.json")
    Observable<List<MoreInfoEntity>> getAtalMoreInfo();

    @GET("/Finanace-Calculator-Web/json/cagr/cagrcalculator.json")
    Observable<CalculatorEntity> getCAGRCalculator();

    @GET("/Finanace-Calculator-Web/json/cagr/cagrmoreinfo.json")
    Observable<List<MoreInfoEntity>> getCAGRMoreInfo();

    @GET("/Finanace-Calculator-Web/json/config.json")
    Observable<ConfigModel> getConfig();

}
