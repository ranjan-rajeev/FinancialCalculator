package com.financialcalculator.customviews;

import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;


public abstract class SingleClickListener implements View.OnClickListener {
    private static final long THRESHOLD_MILLIS = 600L;
    private final PublishSubject<View> viewPublishSubject = PublishSubject.create();

    protected SingleClickListener() {
        viewPublishSubject.throttleFirst(THRESHOLD_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view -> onClicked(view));
    }

    @Override
    public void onClick(View v) {
        viewPublishSubject.onNext(v);
    }

    public abstract void onClicked(View v);
}
