package com.financialcalculator.generic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.financialcalculator.R;
import com.financialcalculator.model.GenericViewTypeModel;

import java.util.List;

public class GenericCalculatorActivity extends AppCompatActivity {

    RecyclerView rvInputs;
    GenericViewTypeAdapter genericViewTypeAdapter;
    List<GenericViewTypeModel> genericViewTypeModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init_widgets();
        genericViewTypeModelList = new GenericViewTypeModel().getDummyList();
        genericViewTypeAdapter = new GenericViewTypeAdapter(genericViewTypeModelList, this);
        rvInputs.setAdapter(genericViewTypeAdapter);
    }

    private void init_widgets() {
        rvInputs = findViewById(R.id.rvInputs);
        rvInputs.setLayoutManager(new LinearLayoutManager(this));
    }
}
