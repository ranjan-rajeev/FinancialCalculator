package com.financialcalculator.generic;

import android.content.Context;
import android.os.AsyncTask;

import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.MoreInfoEntity;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.utility.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

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
    }

    public void getCalculatorDetails() {
        int localCalVersion = sharedPrefManager.getIntegerValueForKey(getFirebaseVersionKey(), 0);
        int serverCalVersion = calculatorEntity.getVersion();
        if (localCalVersion < serverCalVersion) {
            Logger.d("Fetching calculator details from server");
            getInputDetailsFirebase();
            getOutputDetailsFirebase();
        } else {
            Logger.d("Fetching calculator details from local");
            new ConvertCalculatorAsync().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
        //fetch more info list
        new FetchMoreDetails(calculatorEntity.getFirebaseId()).execute();
    }

    public void getInputDetailsFirebase() {
    }

    public void getOutputDetailsFirebase() {
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
