package com.financialcalculator.generic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.outputholders.KeyValueViewHolder;
import com.financialcalculator.generic.viewholders.ButtonViewHolder;
import com.financialcalculator.generic.viewholders.DateViewHolder;
import com.financialcalculator.generic.viewholders.EditTextSpinnerViewHolder;
import com.financialcalculator.generic.viewholders.EditTextViewHolder;
import com.financialcalculator.generic.viewholders.SpinnerTitleViewHolder;
import com.financialcalculator.generic.viewholders.SpinnerViewHolder;
import com.financialcalculator.generic.viewholders.WebViewViewHolder;
import com.financialcalculator.model.GenericOutputEntity;
import com.financialcalculator.model.GenericViewTypeModel;

import java.util.List;

public class GenericOutputAdapter extends RecyclerView.Adapter {

    public static final int TYPE_FORMULAE = 1;
    ;

    private List<GenericOutputEntity> list;
    Context mContext;

    public GenericOutputAdapter(List<GenericOutputEntity> data, Context context) {
        this.list = data;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case TYPE_FORMULAE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_output_key_value, parent, false);
                return new KeyValueViewHolder(view);
           /* case TYPE_SPINNER_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_title, parent, false);
                return new SpinnerTitleViewHolder(view);
            case TYPE_SPINNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);
                return new SpinnerViewHolder(view);
            case TYPE_DATE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edittext_date, parent, false);
                return new DateViewHolder(view);
            case TYPE_BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button, parent, false);
                return new ButtonViewHolder(view);
            case TYPE_EDITTEXT_SPINNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edittext_spinner, parent, false);
                return new EditTextSpinnerViewHolder(view);
            case TYPE_WEBVIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_webview, parent, false);
                return new WebViewViewHolder(view);*/

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return list != null && list.get(position) != null ? list.get(position).getType() : -1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        GenericOutputEntity genericOutputEntity = list.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_FORMULAE:
                ((KeyValueViewHolder) holder).setData(mContext, genericOutputEntity);
                break;
//            case TYPE_SPINNER_TITLE:
//                ((SpinnerTitleViewHolder) holder).setData(mContext, genericViewTypeModel);
//                break;
//            case TYPE_SPINNER:
//                ((SpinnerViewHolder) holder).setData(mContext, genericViewTypeModel);
//                break;
//            case TYPE_DATE:
//                ((DateViewHolder) holder).setData(mContext, genericViewTypeModel);
//                break;
//            case TYPE_BUTTON:
//                ((ButtonViewHolder) holder).setData(mContext, genericViewTypeModel);
//                break;
//            case TYPE_EDITTEXT_SPINNER:
//                ((EditTextSpinnerViewHolder) holder).setData(mContext, genericViewTypeModel);
//                break;
//            case TYPE_WEBVIEW:
//                ((WebViewViewHolder) holder).setData(mContext, genericViewTypeModel);
//                break;
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}