package com.financialcalculator.newdashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.R;
import com.financialcalculator.generic.viewholders.DateViewHolder;
import com.financialcalculator.generic.viewholders.EditTextSpinnerViewHolder;
import com.financialcalculator.generic.viewholders.SpinnerTitleViewHolder;
import com.financialcalculator.generic.viewholders.SpinnerViewHolder;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.newdashboard.viewholders.CalculatorViewHolder;
import com.financialcalculator.newdashboard.viewholders.CarouselViewHolder;

import java.util.List;

public class HomePageAdapter extends RecyclerView.Adapter {

    public static final int TYPE_CAROUSAL = 1;
    public static final int TYPE_WEBVIEW = 2;
    public static final int TYPE_VIEW_PAGER = 3;
    public static final int TYPE_HEADER = 4;
    public static final int TYPE_CALCULATOR_LIST = 5;
    public static final int TYPE_SPACE = 6;
    public static final int TYPE_ADVIEW = 7;
    private List<HomePageModel> list;
    Context mContext;

    public HomePageAdapter(List<HomePageModel> data, Context context) {
        this.list = data;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case TYPE_CAROUSAL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
                return new CarouselViewHolder(view);
            case TYPE_WEBVIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_title, parent, false);
                return new SpinnerTitleViewHolder(view);
            case TYPE_VIEW_PAGER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);
                return new SpinnerViewHolder(view);
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edittext, parent, false);
                return new DateViewHolder(view);
            case TYPE_CALCULATOR_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calculator, parent, false);
                return new CalculatorViewHolder(view);
            case TYPE_SPACE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edittext_spinner, parent, false);
                return new EditTextSpinnerViewHolder(view);
            case TYPE_ADVIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edittext_spinner, parent, false);
                return new EditTextSpinnerViewHolder(view);

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return list != null && list.get(position) != null ? list.get(position).getCmpType() : -1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        HomePageModel genericViewTypeModel = list.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_CAROUSAL:
                ((CarouselViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
            case TYPE_CALCULATOR_LIST:
                ((CalculatorViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
           /* case TYPE_WEBVIEW:
                ((SpinnerTitleViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
            case TYPE_VIEW_PAGER:
                ((SpinnerViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
            case TYPE_HEADER:
                ((DateViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;*/

            /*case TYPE_SPACE:
                ((EditTextSpinnerViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;
            case TYPE_ADVIEW:
                ((EditTextSpinnerViewHolder) holder).setData(mContext, genericViewTypeModel);
                break;*/
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}