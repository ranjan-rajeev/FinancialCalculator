package com.financialcalculator.generic;

import android.content.Context;
import android.os.AsyncTask;

import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.model.MoreInfoEntity;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.services.HttpService;
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GenericCalculatorRepository {
    SharedPrefManager sharedPrefManager;
    String localCalDetails;
    CalculatorListener calculatorListener;
    private Context context;
    private CalculatorEntity calculatorEntity;
    private RoomDatabase roomDatabase;
    private List<MoreInfoEntity> moreInfoEntities;

    public GenericCalculatorRepository(Context context, CalculatorListener calculatorListener, CalculatorEntity calculatorEntity) {
        this.context = context;
        this.calculatorListener = calculatorListener;
        this.calculatorEntity = calculatorEntity;
        sharedPrefManager = SharedPrefManager.getInstance(context);
        localCalDetails = sharedPrefManager.getStringValueForKey(calculatorEntity.getFirebaseId(), "");
        roomDatabase = RoomDatabase.getAppDatabase(context);
        getCalculatorDetails();
        getCalculatorMoreDetailsFromServer();
    }

    public void getCalculatorDetails() {
        int localCalVersion = sharedPrefManager.getIntegerValueForKey(getFirebaseVersionKey(), 0);
        int serverCalVersion = calculatorEntity.getVersion();
        if (localCalVersion < serverCalVersion) {
            Logger.d("Fetching calculator details from server");
            getCalculatorDetailsFromServer();
        } else {
            Logger.d("Fetching calculator details from local");
            new ConvertCalculatorAsync().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    private void getCalculatorDetailsFromServer() {
        Observable<CalculatorEntity> stateResponseObservable = getCalculatorObserVableType(calculatorEntity.getCalId());
        stateResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CalculatorEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CalculatorEntity response) {
                        if (response != null) {
                            calculatorListener.onDataFetched(response);
                            new StoreCalculatorDetails(response).execute();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getCalculatorMoreDetailsFromServer() {
        Observable<List<MoreInfoEntity>> stateResponseObservable = getCalculatorMoreDetailsObserVableType(calculatorEntity.getCalId());
        stateResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MoreInfoEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MoreInfoEntity> response) {
                        if (response != null) {
                            moreInfoEntities = response;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    Observable<CalculatorEntity> getCalculatorObserVableType(int calculatorId) {
        switch (calculatorId) {
            case Constants.CAGR_CALCULATOR:
                return HttpService.getInstance().getCAGRCalculator();
            case Constants.NPS_CALCULATOR:
                return HttpService.getInstance().getNpsCalculator();
            case Constants.ATAL_CALCULATOR:
                return HttpService.getInstance().getAtalCalculator();
            default:
                return HttpService.getInstance().getNpsCalculator();
        }
    }

    Observable<List<MoreInfoEntity>> getCalculatorMoreDetailsObserVableType(int calculatorId) {
        switch (calculatorId) {
            case Constants.CAGR_CALCULATOR:
                return HttpService.getInstance().getCAGRMoreInfo();
            case Constants.NPS_CALCULATOR:
                return HttpService.getInstance().getNpsMoreInfo();
            case Constants.ATAL_CALCULATOR:
                return HttpService.getInstance().getAtalMoreInfo();
            default:
                return HttpService.getInstance().getNpsMoreInfo();
        }
    }

    private String getFirebaseVersionKey() {
        return calculatorEntity.getFirebaseId() + "version";
    }

    public List<MoreInfoEntity> getMoreInfoEntities() {
        return moreInfoEntities;
    }

    private class ConvertCalculatorAsync extends AsyncTask<Void, Void, CalculatorEntity> {

        @Override
        protected CalculatorEntity doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type calculator = new TypeToken<CalculatorEntity>() {
                }.getType();
                return gson.fromJson(localCalDetails, calculator);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(CalculatorEntity calculatorEntity1) {
            super.onPostExecute(calculatorEntity1);
            if (calculatorEntity1 != null) {
                calculatorEntity = calculatorEntity1;
                calculatorListener.onDataFetched(calculatorEntity1);
            }

        }
    }

    private class StoreCalculatorDetails extends AsyncTask<Void, Void, String> {
        private CalculatorEntity calculatorEntity;

        public StoreCalculatorDetails(CalculatorEntity calculatorEntity) {
            this.calculatorEntity = calculatorEntity;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return new Gson().toJson(calculatorEntity);
        }

        @Override
        protected void onPostExecute(String list) {
            super.onPostExecute(list);
            if (list != null) {
                sharedPrefManager.putStringValueForKey(calculatorEntity.getFirebaseId(), list);
                sharedPrefManager.putIntegerValueForKey(getFirebaseVersionKey(), calculatorEntity.getVersion());
            }

        }
    }

    private class FetchMoreDetails extends AsyncTask<Void, Void, List<MoreInfoEntity>> {
        private String firebaseId;

        public FetchMoreDetails(String firebaseId) {
            this.firebaseId = firebaseId;
        }

        @Override
        protected List<MoreInfoEntity> doInBackground(Void... voids) {
            return roomDatabase.moreInfoDao().getAllById(firebaseId);
        }

        @Override
        protected void onPostExecute(List<MoreInfoEntity> list) {
            super.onPostExecute(list);
            if (list != null && list.size() > 0) {
                moreInfoEntities = list;
            }
        }
    }
/* private class ParseInput extends AsyncTask<Void, Void, List<GenericViewTypeModel>> {

        @Override
        protected List<GenericViewTypeModel> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<GenericViewTypeModel>>() {
                }.getType();
                return gson.fromJson(calculatorEntity.getInput(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<GenericViewTypeModel> list) {
            super.onPostExecute(list);
            inputArray = list;
        }
    }

    private class ParseOutput extends AsyncTask<Void, Void, List<GenericOutputEntity>> {

        @Override
        protected List<GenericOutputEntity> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<GenericOutputEntity>>() {
                }.getType();
                return gson.fromJson(calculatorEntity.getOutput(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<GenericOutputEntity> list) {
            super.onPostExecute(list);
            outputArray = list;
        }
    }*/
}
