package com.financialcalculator.newdashboard;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.financialcalculator.model.CarouselEntity;


import java.util.List;

public class LongBannerImagePagerAdapter extends FragmentStatePagerAdapter {

    private final Context context;
    private List<CarouselEntity> itemList;

    public LongBannerImagePagerAdapter(final FragmentManager fragmentManager, Context context, List<CarouselEntity> itemList) {
        super(fragmentManager);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public Fragment getItem(int position) {
        return LongBannerFragment.newInstance(itemList.get(position), context);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }
}
