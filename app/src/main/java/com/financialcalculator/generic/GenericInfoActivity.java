package com.financialcalculator.generic;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.viewholders.EditTextSpinnerViewHolder;
import com.financialcalculator.generic.viewholders.EditTextViewHolder;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.GenericOutputEntity;
import com.financialcalculator.model.GenericViewTypeModel;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GenericInfoActivity extends BaseActivity {

    RecyclerView rvInputs;
    GenericViewTypeAdapter genericViewTypeAdapter;
    RecyclerView rvOutput;
    CardView cvOutput;
    GenericOutputAdapter genericOutputAdapter;
    public static CalculatorEntity calculatorEntity;
    List<GenericViewTypeModel> genericViewTypeModelList;
    NestedScrollView scrollView;

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
            if (calculatorEntity != null) {
                getSupportActionBar().setTitle(calculatorEntity.getCalName());
            }
        }
        init_widgets();
        //genericViewTypeModelList = new GenericViewTypeModel().getDummyList();
        /*genericViewTypeAdapter = new GenericViewTypeAdapter(genericViewTypeModelList, this);
        rvInputs.setAdapter(genericViewTypeAdapter);*/
        new ConvertAsync().execute();
        //new ParseOutput().execute();
    }

    private void init_widgets() {
        scrollView = findViewById(R.id.scrollView);
        rvInputs = findViewById(R.id.rvInputs);
        rvInputs.setLayoutManager(new LinearLayoutManager(this));
        cvOutput = findViewById(R.id.cvOutput);
        rvOutput = findViewById(R.id.rvOutput);
        rvOutput.setLayoutManager(new LinearLayoutManager(this));
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
            genericViewTypeModelList = list;
            bindInputRecycler(list);
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
            bindOutputRecycler(list);
        }
    }

    public void bindOutputRecycler(List<GenericOutputEntity> genericOutputEntities) {
        hideKeyBoard(scrollView, this);
        scrollToView(cvOutput);
        genericOutputAdapter = new GenericOutputAdapter(genericOutputEntities, this);
        rvOutput.setAdapter(genericOutputAdapter);
    }

    public void bindInputRecycler(List<GenericViewTypeModel> genericViewTypeModels) {
        genericViewTypeAdapter = new GenericViewTypeAdapter(genericViewTypeModels, this);
        rvInputs.setAdapter(genericViewTypeAdapter);
    }


    public void setInputHashMap(Character key, BigDecimal value) {
        calculatorEntity.inputHashmap.put(key, value);
        Logger.d("Key : " + key + "  Val : " + calculatorEntity.inputHashmap.get(key));
    }


    public void showResult() {
        for (int i = 0; i < genericViewTypeModelList.size(); i++) {
            if (!genericViewTypeModelList.get(i).isValid()) {
                RecyclerView.ViewHolder viewHolder = rvInputs.findViewHolderForAdapterPosition(i);
                if (viewHolder instanceof EditTextViewHolder) {
                    ((EditTextViewHolder) viewHolder).showErrorMessage(true);
                } else if (viewHolder instanceof EditTextSpinnerViewHolder) {
                    ((EditTextSpinnerViewHolder) viewHolder).showErrorMessage(true);
                }
                return;
            }
        }
        cvOutput.setVisibility(View.VISIBLE);
        new ParseOutput().execute();
    }

    public void scrollToView(View childView) {
        int scrollTo = ((View) childView.getParent().getParent()).getTop() + childView.getTop();
        scrollView.smoothScrollTo(0, scrollTo);
    }


}
