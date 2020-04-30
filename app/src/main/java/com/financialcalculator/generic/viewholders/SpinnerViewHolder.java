package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.model.GenericViewTypeModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

public class SpinnerViewHolder extends RecyclerView.ViewHolder {

    Spinner spinner;
    ArrayAdapter<String> arrayAdapter;
    LinkedHashMap<String, String> keyValueHashMap = new LinkedHashMap<>();
    Context context;
    GenericViewTypeModel genericViewTypeModel;

    public SpinnerViewHolder(View itemView) {
        super(itemView);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        this.context = context;
        this.genericViewTypeModel = genericViewTypeModel;
        this.genericViewTypeModel.setValid(true);
        new ConvertAsync().execute();

    }

    private class ConvertAsync extends AsyncTask<Void, Void, List<SpinnerEntity>> {

        @Override
        protected List<SpinnerEntity> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<SpinnerEntity>>() {
                }.getType();
                List<SpinnerEntity> spinnerEntities = gson.fromJson(genericViewTypeModel.getData(), type);
                for (SpinnerEntity spinnerEntity : spinnerEntities) {
                    keyValueHashMap.put(spinnerEntity.getKey(), spinnerEntity.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<SpinnerEntity> list) {
            super.onPostExecute(list);
            bindSpinnerList();
        }
    }

    private void bindSpinnerList() {
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new ArrayList<>(keyValueHashMap.keySet()));
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    BigDecimal value = null;
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    value = new BigDecimal(keyValueHashMap.get(selectedItem)).setScale(0, BigDecimal.ROUND_HALF_UP);
                    ((GenericCalculatorActivity) context).setInputHashMap(genericViewTypeModel.getKey().charAt(0), value);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public class SpinnerEntity {

        /**
         * key : Yearly
         * value : 1
         */

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}