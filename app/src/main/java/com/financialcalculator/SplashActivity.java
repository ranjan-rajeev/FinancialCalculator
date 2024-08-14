package com.financialcalculator;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.home.MainActivity;
import com.financialcalculator.model.ConfigModel;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.services.HttpService;
import com.financialcalculator.utility.Constants;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class SplashActivity extends AppCompatActivity {

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        getConfig();
        /*if (BuildConfig.FLAVOR.equals("free") && Constants.APP_TYPE == 0) {
            MobileAds.initialize(this, getResources().getString(R.string.ad_app_id));
        }*/
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here,// we animate towards that value
        int SPLASH_DISPLAY_LENGTH = 2000;
        animation.setDuration(SPLASH_DISPLAY_LENGTH); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void getConfig() {
        Observable<ConfigModel> stateResponseObservable = HttpService.getInstance().getConfig();
        stateResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ConfigModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ConfigModel response) {
                        if (response != null) {
                            sharedPrefManager.putStringValueForKey(Constants.SHD_PRF_CONFIG, new Gson().toJson(response));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
