package com.financialcalculator.generic.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.model.EditTextEntity;
import com.financialcalculator.model.GenericViewTypeModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ButtonViewHolder extends RecyclerView.ViewHolder {

    TextView button;
    Context context;
    GenericViewTypeModel genericViewTypeModel;
    ButtonEntity buttonEntity;

    public ButtonViewHolder(View itemView) {
        super(itemView);
        this.button = itemView.findViewById(R.id.button);
    }

    public void setData(Context context, GenericViewTypeModel genericViewTypeModel) {
        this.context = context;
        this.genericViewTypeModel = genericViewTypeModel;
        this.genericViewTypeModel.setValid(true);
        button.setText(genericViewTypeModel.getTitle());
        new ConvertAsync().execute();
        //button.setOnClickListener(v -> Toast.makeText(context, "Rajeev", Toast.LENGTH_SHORT).show());
    }

    private class ConvertAsync extends AsyncTask<Void, Void, ButtonEntity> {

        @Override
        protected ButtonEntity doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<ButtonEntity>() {
                }.getType();
                return gson.fromJson(genericViewTypeModel.getData(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(ButtonEntity list) {
            super.onPostExecute(list);
            if (list != null) {
                buttonEntity = list;
                bindButton();
            }

        }
    }

    private void bindButton() {
        button.setOnClickListener(v -> {
            if (buttonEntity.getAction() == 1) {
                ((GenericCalculatorActivity) context).showResult();
            }
        });

    }


    class ButtonEntity {
        String redUrl;
        int action;

        public String getRedUrl() {
            return redUrl;
        }

        public void setRedUrl(String redUrl) {
            this.redUrl = redUrl;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }
    }
}