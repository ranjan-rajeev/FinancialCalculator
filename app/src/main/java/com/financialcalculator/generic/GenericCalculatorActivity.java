package com.financialcalculator.generic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.financialcalculator.R;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.CarouselEntity;
import com.financialcalculator.model.GenericViewTypeModel;
import com.financialcalculator.utility.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GenericCalculatorActivity extends AppCompatActivity {

    RecyclerView rvInputs;
    GenericViewTypeAdapter genericViewTypeAdapter;
    CalculatorEntity calculatorEntity;
    List<GenericViewTypeModel> genericViewTypeModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (null != getIntent()) {
            calculatorEntity = (CalculatorEntity) this.getIntent().getSerializableExtra("CALCULATOR");
        }
        init_widgets();
        //genericViewTypeModelList = new GenericViewTypeModel().getDummyList();
        /*genericViewTypeAdapter = new GenericViewTypeAdapter(genericViewTypeModelList, this);
        rvInputs.setAdapter(genericViewTypeAdapter);*/
        new ConvertAsync().execute();
    }

    private void init_widgets() {
        rvInputs = findViewById(R.id.rvInputs);
        rvInputs.setLayoutManager(new LinearLayoutManager(this));
    }

    private class ConvertAsync extends AsyncTask<Void, Void, List<GenericViewTypeModel>> {

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
            bindInputRecycler(list);
        }
    }

    public void bindInputRecycler(List<GenericViewTypeModel> genericViewTypeModels) {
        genericViewTypeAdapter = new GenericViewTypeAdapter(genericViewTypeModels, this);
        rvInputs.setAdapter(genericViewTypeAdapter);
    }

    public void setInputHashMap(Character key, BigDecimal value) {
        calculatorEntity.inputHashmap.put(key, value);
        Logger.d("Key : " + key + "  Val : " + calculatorEntity.inputHashmap.get(key));
    }

    public void setOutputHashMap(Character key, BigDecimal value) {
        calculatorEntity.outputHashmap.put(key, value);
        Logger.d("Key : " + key + "  Val : " + calculatorEntity.inputHashmap.get(key));
    }

}
