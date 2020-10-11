package com.financialcalculator.newdashboard;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.R;
import com.financialcalculator.home.MainActivity;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.utility.BaseFragment;
import com.financialcalculator.utility.Util;

import java.util.ArrayList;
import java.util.List;

public class NewDashBoardFragment extends BaseFragment {

    public static final String TAG = "NewDashBoard";
    View view;
    //private AdView mAdView;
    RecyclerView rvDashboard;
    HomePageAdapter homePageAdapter;
    List<HomePageModel> homePageModels = new ArrayList<>();
    MainActivity activity;
    SharedPrefManager sharedPrefManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        sharedPrefManager = SharedPrefManager.getInstance(activity);
        //setUPAdd(view);
        init_widgets(view);
        new ParseDashboardList().execute();

        return view;
    }

    private void init_widgets(View view) {
        rvDashboard = view.findViewById(R.id.rvDashboard);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDashboard.setLayoutManager(layoutManager);
        homePageAdapter = new HomePageAdapter(homePageModels, activity);
        rvDashboard.setAdapter(homePageAdapter);
    }

    private class ParseDashboardList extends AsyncTask<Void, Void, List<HomePageModel>> {
        @Override
        protected List<HomePageModel> doInBackground(Void... voids) {
            return Util.parseDashboardListFirebase();
        }

        @Override
        protected void onPostExecute(List<HomePageModel> list) {
            super.onPostExecute(list);
            if (list != null)
                homePageAdapter.updateList(list);
        }
    }
}
