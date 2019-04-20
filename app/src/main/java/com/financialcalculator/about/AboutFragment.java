package com.financialcalculator.about;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.financialcalculator.R;

import java.util.List;


/**
 * Created by Rajeev Ranjan -  ABPB on 08-04-2019.
 */
public class AboutFragment extends Fragment {

    View view;
    RecyclerView rvWhatsNew;
    WhatsNewAdapter mAdapter;
    List<AboutUsEntity> whatsNewEntities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);
        mAdapter = new WhatsNewAdapter(getActivity(), new AboutUsEntity().getListAboutUsEntity());
        rvWhatsNew = view.findViewById(R.id.rvWhatsNew);
        rvWhatsNew.setHasFixedSize(true);
        rvWhatsNew.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvWhatsNew.setAdapter(mAdapter);
        return view;
    }
}
