package com.financialcalculator.newdashboard.viewholders;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.financialcalculator.R;
import com.financialcalculator.customviews.AutoScrollViewPager;
import com.financialcalculator.model.CarouselEntity;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.newdashboard.LongBannerImagePagerAdapter;
import com.financialcalculator.utility.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ViewPagerViewHolder extends RecyclerView.ViewHolder {

    Context context;
    List<CarouselEntity> carouselEntities;
    HomePageModel homePageModel;
    LinearLayout indicatorLayout;
    AutoScrollViewPager view_pager;

    public ViewPagerViewHolder(View itemView) {
        super(itemView);
        this.indicatorLayout = itemView.findViewById(R.id.indicatorLayout);
        this.view_pager = itemView.findViewById(R.id.view_pager);
    }

    public void setData(Context context, HomePageModel homePageModel) {
        this.context = context;
        this.homePageModel = homePageModel;
        indicatorLayout.setVisibility(View.GONE);
        new ConvertAsync().execute();
    }

    private class ConvertAsync extends AsyncTask<Void, Void, List<CarouselEntity>> {

        @Override
        protected List<CarouselEntity> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<CarouselEntity>>() {
                }.getType();
                return gson.fromJson(homePageModel.getCmpData(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(List<CarouselEntity> list) {
            super.onPostExecute(list);
            setEndToEndBannerView(list);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setEndToEndBannerView(List<CarouselEntity> itemList) {

        view_pager.setId(View.generateViewId());
        /* For auto scroll */
        view_pager.setInterval(3000);
        view_pager.setViewCanStopOnTouch(true);
        view_pager.startAutoScroll();
        try {
            view_pager.setAdapter(new LongBannerImagePagerAdapter(((BaseActivity) context).getSupportFragmentManager(), context, itemList));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}