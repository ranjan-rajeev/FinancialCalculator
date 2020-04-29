package com.financialcalculator.newdashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.financialcalculator.R;
import com.financialcalculator.customviews.SingleClickListener;
import com.financialcalculator.model.CarouselEntity;
import com.financialcalculator.utility.GenericImageLoader;
import com.financialcalculator.utility.Util;


public class LongBannerFragment extends Fragment {
    private final static String SUB_ITEM = "subItem";
    public static Context activity;
    private CarouselEntity item;

    public static Fragment newInstance(CarouselEntity subItems, Context context) {
        final Bundle args = new Bundle();
        activity = context;
        args.putSerializable(SUB_ITEM, subItems);
        LongBannerFragment fragment = new LongBannerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_long_banner, container, false);
        if (null != getArguments()) {
            item = (CarouselEntity) this.getArguments().getSerializable(SUB_ITEM);
        }
        ImageView imageView = view.findViewById(R.id.image_view);
        GenericImageLoader.loadImage(activity, imageView, item.getWebUrl(), 0);
        imageView.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClicked(View v) {
                Util.inAppRedirection(activity, item.getRedUrl(), item.getWebUrl());
            }
        });
        return view;
    }
}
