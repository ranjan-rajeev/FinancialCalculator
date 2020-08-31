package com.financialcalculator.newdashboard;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.financialcalculator.BuildConfig;
import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.R;
import com.financialcalculator.banking.fd.FDCalculatorActivity;
import com.financialcalculator.banking.ppf.PPFCalculatotActivity;
import com.financialcalculator.banking.rd.RDCalculatorActivity;
import com.financialcalculator.dashboard.DashboardRowAdapter;
import com.financialcalculator.emi.emicalculator.EmiCalculatorActivity;
import com.financialcalculator.emi.emicompare.EmiCompareActivity;
import com.financialcalculator.emi.emifixedvsreducing.FixedVsReducingActivity;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.generic.GenericViewTypeAdapter;
import com.financialcalculator.gst.GstCalculatorActivity;
import com.financialcalculator.gst.VatCalculatorActivity;
import com.financialcalculator.home.MainActivity;
import com.financialcalculator.loanprofile.CreateLoanProfileActivity;
import com.financialcalculator.loanprofile.ViewLoanProfile;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.CarouselEntity;
import com.financialcalculator.model.DashBoardRowEntity;
import com.financialcalculator.model.DashboardEntity;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.model.InputTypeEntity;
import com.financialcalculator.sip.LumpSumpSipActivity;
import com.financialcalculator.sip.SIPCalculatorActivity;
import com.financialcalculator.sip.SIPGoalCalculatorActivity;
import com.financialcalculator.utility.Constants;
import com.financialcalculator.utility.Logger;
import com.financialcalculator.utility.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewDashBoardFragment extends Fragment {

    public static final String TAG = "NewDashBoard";
    View view;
    private AdView mAdView;
    RecyclerView rvDashboard;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int localVersion = 0, serverVersion = 0;
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
        //setUPAdd(view);
        init_widgets(view);
        sharedPrefManager = SharedPrefManager.getInstance(activity);
        localVersion = sharedPrefManager.getIntegerValueForKey(Constants.DASHBOARD_VERSION, 0);
        getVersionFromFirebase();
        if (sharedPrefManager.getStringValueForKey(Constants.DEFAULT_DASHBOARD_DATA, "").equals("")) {
            //Data not present in pref
            if (Util.isNetworkConnected(activity)) {
                // data not in pref & network connected
                getDashBoardListFirebase();
            } else {
                // data not in pref & No network
                new LoadDefaultList().execute();
            }
        } else {
            new LoadPrefList().execute();
        }

        return view;
    }

    public void getDashBoardListFirebase() {
        DatabaseReference myRef = database.getReference("components");
        // Read from the database
        myRef.orderByChild("cmpPos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                homePageModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        HomePageModel homePageModel = snapshot.getValue(HomePageModel.class);
                        homePageModels.add(homePageModel);
                        /*if (homePageModel.getCmpType() == 5) {
                            Gson gson = new Gson();
                            Type userListType = new TypeToken<ArrayList<CalculatorEntity>>() {
                            }.getType();
                            ArrayList<CalculatorEntity> userArray = gson.fromJson(homePageModel.getCmpData(), userListType);


                            Type inputTypeToken = new TypeToken<ArrayList<InputTypeEntity>>() {
                            }.getType();
                            ArrayList<InputTypeEntity> inputTypeEntities = gson.fromJson(userArray.get(2).getInput(), inputTypeToken);
                            Log.d(TAG, "" + inputTypeEntities.size());
                        }*/
                        Logger.d(TAG, "" + homePageModel.getFirebaseId());
                    } catch (Exception e) {
                        Logger.d(TAG, "Unable to parse");
                    }
                }
                new StoreDefaultList(homePageModels).execute();
                homePageAdapter.updateList(homePageModels);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void getVersionFromFirebase() {
        DatabaseReference versionRef = database.getReference("version");
        versionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Logger.d("Local Version : " + localVersion + "  Server Version  : " + serverVersion);
                    serverVersion = Integer.parseInt(dataSnapshot.getValue().toString());
                    Logger.d("Local Version : " + localVersion + "  Server Version  : " + serverVersion);

                    if (localVersion < serverVersion) {
                        sharedPrefManager.putIntegerValueForKey(Constants.DASHBOARD_VERSION, serverVersion);
                        getDashBoardListFirebase();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Logger.d(databaseError.getMessage());
            }
        });
    }

    private void init_widgets(View view) {
        rvDashboard = view.findViewById(R.id.rvDashboard);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDashboard.setLayoutManager(layoutManager);
        homePageAdapter = new HomePageAdapter(homePageModels, activity);
        rvDashboard.setAdapter(homePageAdapter);
    }

    private class StoreDefaultList extends AsyncTask<Void, Void, Void> {
        List<HomePageModel> homePageModels;

        StoreDefaultList(List<HomePageModel> homePageModels) {
            this.homePageModels = homePageModels;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Gson gson = new Gson();
            sharedPrefManager.putStringValueForKey(Constants.DEFAULT_DASHBOARD_DATA, gson.toJson(homePageModels));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // homePageAdapter.updateList(homePageModels);
        }
    }

    private class LoadDefaultList extends AsyncTask<Void, Void, List<HomePageModel>> {
        List<HomePageModel> list = new ArrayList<>();

        @Override
        protected List<HomePageModel> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<HomePageModel>>() {
                }.getType();
                return gson.fromJson(Util.loadJSONFromAsset(activity), type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HomePageModel> list) {
            super.onPostExecute(list);
            if (list != null)
                homePageAdapter.updateList(list);
        }
    }

    private class LoadPrefList extends AsyncTask<Void, Void, List<HomePageModel>> {
        List<HomePageModel> list = new ArrayList<>();

        @Override
        protected List<HomePageModel> doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<HomePageModel>>() {
                }.getType();
                String data = sharedPrefManager.getStringValueForKey(Constants.DEFAULT_DASHBOARD_DATA, "");
                return gson.fromJson(data, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HomePageModel> list) {
            super.onPostExecute(list);
            if (list != null)
                homePageAdapter.updateList(list);
        }
    }
}
