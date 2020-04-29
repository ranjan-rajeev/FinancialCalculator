package com.financialcalculator.services;

import android.content.Context;

import com.financialcalculator.BuildConfig;
import com.financialcalculator.FinanceCalculatorApplication;
import com.financialcalculator.PrefManager.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpService {
    private static HttpService instance = new HttpService();
    public Retrofit retrofit;
    private LoanAssistServices loanAssistServices;

    private HttpService() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        long TIMEOUT = 60L;
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS);

        httpClientBuilder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();
            //requestBuilder.addHeader("appversion", GeneralUtils.getPackageInfo(getContext()).versionName);
            requestBuilder.addHeader("appplatform", "android");
            requestBuilder.method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(BuildConfig.IS_IN_DEBUG_MODE ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);// todo : change it to none

        OkHttpClient client = httpClientBuilder.addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder().baseUrl(BuildConfig.HOST + "/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).client(client).build();

        loanAssistServices = retrofit.create(LoanAssistServices.class);

//        Retrofit middleLayerRetrofit = new Retrofit.Builder()
//                .baseUrl(CliqApplication.getInstance().environmentManager.getMiddleLayerHost() + "/")
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        middleLayerService = middleLayerRetrofit.create(MiddleLayerService.class);
//
//        Retrofit juspayRetrofit = new Retrofit.Builder()
//                .baseUrl(CliqApplication.getInstance().environmentManager.getJustPayHost() + "/")
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        justPayServices = juspayRetrofit.create(JustPayServices.class);
//
//        OkHttpClient.Builder stripeHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
//                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
//                .writeTimeout(TIMEOUT, TimeUnit.SECONDS);
//
//        stripeHttpClient.addInterceptor(chain -> {
//            Request original = chain.request();
//            Request.Builder requestBuilder = original.newBuilder();
//            requestBuilder.addHeader("Authorization", "Bearer " + BuildConfig.STRIPE_API_KEY);
//            requestBuilder.method(original.method(), original.body());
//            Request request = requestBuilder.build();
//            return chain.proceed(request);
//        });
//
//        OkHttpClient stripeClient = stripeHttpClient.addInterceptor(interceptor).build();
//
//        Retrofit stripeRetrofit = new Retrofit.Builder()
//                .baseUrl(BuildConfig.STRIPE_BASE_URL + "/")
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(stripeClient)
//                .build();
//
//        stripeServices = stripeRetrofit.create(StripeServices.class);
//
//        Retrofit msdRetrofit = new Retrofit.Builder()
//                .baseUrl(BuildConfig.MSD_RECOMMENDATION_HOST)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        msdServices = msdRetrofit.create(MsdServices.class);
//
//        Retrofit flixMediaRetrofit = new Retrofit.Builder()
//                .baseUrl(BuildConfig.FLIX_MEDIA_HOST)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//        flixMediaServices = flixMediaRetrofit.create(FlixMediaServices.class);
    }

    public static HttpService getInstance() {
        if (instance == null) {
            instance = getSyncedInstance();
        }
        return instance;
    }

    private static synchronized HttpService getSyncedInstance() {
        if (instance == null) {
            instance = new HttpService();
        }
        return instance;
    }

    public Context getContext() {
        return FinanceCalculatorApplication.getContext();
    }

    private SharedPrefManager getSharedPreference() {
        return SharedPrefManager.getInstance(getContext());
    }


   /* public Observable<OnlineDashBoardEntity> getDashboardData(String url) {
        return loanAssistServices.getDashBoardData(url)
                .flatMap((Function<OnlineDashBoardEntity, ObservableSource<OnlineDashBoardEntity>>) users -> Observable.just(users));
    }*/
}
