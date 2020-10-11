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
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GenericCalculatorActivity extends BaseActivity implements CalculatorListener {

    RecyclerView rvInputs;
    GenericViewTypeAdapter genericViewTypeAdapter;
    public CalculatorEntity calculatorEntity;
    RecyclerView rvOutput, rvMoreInfo;
    GenericOutputAdapter genericOutputAdapter;
    CardView cvInput, cvOutput, cvMoreInfo;
    List<GenericViewTypeModel> genericViewTypeModelList;
    NestedScrollView scrollView;
    GenericCalculatorRepository genericCalculatorRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init_widgets();
        if (null != getIntent()) {
            try {
                calculatorEntity = (CalculatorEntity) getIntent().getSerializableExtra(Constants.INTENT_PARAM_CAL);
                getSupportActionBar().setTitle(calculatorEntity.getCalName());
                genericCalculatorRepository = new GenericCalculatorRepository(this, this, calculatorEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //genericCalculatorRepository.getCalculatorDetails();
        // new ConvertAsync().execute();
    }

    private void init_widgets() {
        scrollView = findViewById(R.id.scrollView);
        cvInput = findViewById(R.id.cvInput);
        rvInputs = findViewById(R.id.rvInputs);
        rvInputs.setLayoutManager(new LinearLayoutManager(this));
        cvOutput = findViewById(R.id.cvOutput);
        rvOutput = findViewById(R.id.rvOutput);
        rvOutput.setLayoutManager(new LinearLayoutManager(this));
        cvMoreInfo = findViewById(R.id.cvMoreInfo);
        rvMoreInfo = findViewById(R.id.rvMoreInfo);
        rvMoreInfo.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onDataFetched(CalculatorEntity calculatorEntity) {
        if (calculatorEntity != null) {
            this.calculatorEntity = calculatorEntity;
            bindInputRecycler(calculatorEntity.getInputList());
        }
    }

    @Override
    public void onError(String message) {

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

    public void bindMoreInfoRecycler() {
        if (genericCalculatorRepository.getMoreInfoEntities() != null) {
            cvMoreInfo.setVisibility(View.VISIBLE);
            GenericMoreInfoAdapter genericMoreInfoAdapter = new GenericMoreInfoAdapter(genericCalculatorRepository.getMoreInfoEntities(), this);
            rvMoreInfo.setAdapter(genericMoreInfoAdapter);
        }
    }

    public void bindInputRecycler(List<GenericViewTypeModel> genericViewTypeModels) {
        if (genericViewTypeModels != null && genericViewTypeModels.size() > 0) {
            cvInput.setVisibility(View.VISIBLE);
            genericViewTypeAdapter = new GenericViewTypeAdapter(genericViewTypeModels, this);
            rvInputs.setAdapter(genericViewTypeAdapter);
        }
    }

    public void setHashMapValue(Character key, BigDecimal value) {
        calculatorEntity.inputHashmap.put(key, value);
        Logger.d("Key : " + key + "  Val : " + calculatorEntity.inputHashmap.get(key));
    }

    public void showResult() {
        if (calculatorEntity.getInputList() == null) return;
        for (int i = 0; i < calculatorEntity.getInputList().size(); i++) {
            if (!calculatorEntity.getInputList().get(i).isValid()) {
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
        if (calculatorEntity.getOutputList() == null) return;
        bindOutputRecycler(calculatorEntity.getOutputList());
        bindMoreInfoRecycler();
        //new ParseOutput().execute();
    }

    public void scrollToView(View childView) {
        int scrollTo = ((View) childView.getParent().getParent()).getTop() + childView.getTop();
        scrollView.smoothScrollTo(0, scrollTo);
    }

    public CalculatorEntity getCalculatorEntity() {
        return calculatorEntity;
    }
}
