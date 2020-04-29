package com.financialcalculator.services;


import com.financialcalculator.model.HomePageModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface LoanAssistServices {

    @GET
    Observable<HomePageModel> getDashBoardData(@Url String url);
}
