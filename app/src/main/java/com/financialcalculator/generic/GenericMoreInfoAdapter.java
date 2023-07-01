package com.financialcalculator.generic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.outputholders.MoreInfoViewHolder;
import com.financialcalculator.model.MoreInfoEntity;

import java.util.List;

public class GenericMoreInfoAdapter extends RecyclerView.Adapter {

    public static final int TYPE_EXPRESSION_WITH_FORMULAE = 1;
    Context mContext;
    private List<MoreInfoEntity> list;

    public GenericMoreInfoAdapter(List<MoreInfoEntity> data, Context context) {
        this.list = data;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_info, parent, false);
        return new MoreInfoViewHolder(view);
       /* switch (viewType) {
            case TYPE_EXPRESSION_WITH_FORMULAE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_info, parent, false);
                return new MoreInfoViewHolder(view);
        }*/

    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_EXPRESSION_WITH_FORMULAE;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        MoreInfoEntity moreInfoEntity = list.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_EXPRESSION_WITH_FORMULAE:
                ((MoreInfoViewHolder) holder).setData(mContext, moreInfoEntity, position);
                break;
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