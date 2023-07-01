package com.financialcalculator.generic;

import com.financialcalculator.model.CalculatorEntity;

public interface CalculatorListener {
    void onDataFetched(CalculatorEntity calculatorEntity);

    void onError(String message);
}